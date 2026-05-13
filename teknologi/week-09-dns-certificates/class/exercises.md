# Class Exercises: DNS & Certificates

Work through these exercises during class. Ask for help if you get stuck!

---

## Warm-Up (~5 minutes)

Make sure your tools are ready:

```bash
# Should return an IP address
dig +short google.com

# Should return OpenSSL/LibreSSL version
openssl version
```

If `dig` isn't installed natively, start a Docker container:
```bash
docker run -it --rm alpine sh -c "apk add --no-cache bind-tools openssl curl && sh"
```

Have Wireshark open and ready.

---

## Exercise 1: Trace the Full DNS Resolution Path (~20 minutes)

**Goal:** Watch DNS resolution happen step by step — from root servers to the final answer.

### Part A: The trace

Run this command:

```bash
dig +trace github.com
```

This shows every step your resolver takes:

1. **Root servers** (`.`) — the starting point
2. **TLD servers** (`.com`) — "who handles .com?"
3. **Authoritative nameservers** (`github.com`) — "what's the IP?"

### Part B: Read the output

Look at each section in the output:

1. Find the **root server** section — you'll see names like `a.root-servers.net`, `b.root-servers.net`, etc.
2. Find where it asks the **.com TLD** — you'll see `a.gtld-servers.net` or similar
3. Find the **final answer** — the A record with the actual IP address

### Part C: Try another domain

```bash
dig +trace wikipedia.org
```

Notice how the path is different — it goes through `.org` TLD servers instead of `.com`.

### Part D: Try a Danish domain

```bash
dig +trace kea.dk
```

This goes through the `.dk` TLD servers. Denmark has its own set of TLD servers managed by DK Hostmaster.

### Self-check

- [ ] You can see the three levels: root → TLD → authoritative
- [ ] You can identify the final A record in the trace output
- [ ] You understand that different TLDs (.com, .org, .dk) have different paths

---

## Exercise 2: Explore DNS Record Types (~20 minutes)

**Goal:** See all the different types of information stored in DNS.

### Part A: A and AAAA records

```bash
dig google.com A
dig google.com AAAA
```

Google has both IPv4 (A) and IPv6 (AAAA) addresses. Many large sites do.

```bash
dig github.com A
dig github.com AAAA
```

Does GitHub have both?

### Part B: CNAME records

```bash
dig www.github.com
```

Look at the ANSWER SECTION. `www.github.com` is a **CNAME** (alias) pointing to `github.com`. The resolver automatically resolves the alias and gives you the A record too.

```bash
dig www.google.com
```

Is `www.google.com` also a CNAME?

### Part C: MX records (mail servers)

```bash
dig google.com MX
```

These are the servers that handle email for `@gmail.com` / `@google.com`. Notice the **priority number** before each server — lower numbers are preferred.

```bash
dig kea.dk MX
```

Where does KEA's email go?

### Part D: NS records (nameservers)

```bash
dig github.com NS
```

These are the authoritative nameservers for `github.com`. These are the servers that *own* the DNS records.

### Part E: TXT records

```bash
dig google.com TXT
```

You'll see SPF records (which servers are allowed to send email for google.com), verification records, and other metadata. This is how Google proves to other mail servers that an email really came from Google.

### Self-check

- [ ] You can look up A, AAAA, CNAME, MX, NS, and TXT records
- [ ] You understand that CNAME is an alias that redirects to another name
- [ ] You can see MX priorities (lower number = higher priority)
- [ ] You know that TXT records are used for email authentication (SPF) and domain verification

---

## Exercise 3: DNS in Wireshark (~25 minutes)

**Goal:** Capture DNS packets and see the protocol in action.

### Part A: Start capturing

1. Open Wireshark
2. Select your **main network interface** (the one connected to the internet — not loopback)
   - Linux: `eth0`, `wlan0`, or `enp0s3`
   - macOS: `en0` (Wi-Fi) or `en1`
   - Windows: your Wi-Fi or Ethernet adapter
3. Start capturing

### Part B: Generate DNS traffic

In a terminal, run:

```bash
dig example.com
```

### Part C: Filter DNS traffic

In Wireshark's filter bar, type:

```
dns
```

You should see two packets:
1. A **query** (your computer asking "what's the IP for example.com?")
2. A **response** (the DNS server answering with the IP)

### Part D: Examine a DNS query

Click on the DNS query packet. In the packet details (middle pane), expand:
- **Domain Name System (query)** → look at:
  - **Transaction ID**: matches between query and response
  - **Questions**: the domain name you asked about
  - **Type**: A (IPv4 address)

### Part E: Examine the DNS response

Click on the DNS response packet. Expand **Domain Name System (response)**:
- **Answers**: the IP address(es) returned
- **Type**: A
- **TTL**: how long to cache this answer

Notice that DNS uses **UDP port 53** (not TCP). It's fast because UDP doesn't need a handshake — just ask and answer.

### Part F: Capture different record types

Clear the capture (Ctrl+X to restart) and try:

```bash
dig google.com MX
```

In Wireshark, filter with `dns` and look at the response. You'll see MX records instead of A records.

### Self-check

- [ ] You can capture DNS traffic in Wireshark
- [ ] You can identify DNS queries and responses
- [ ] You can see the domain name, record type, and TTL in the packet details
- [ ] You notice that DNS uses UDP (not TCP)

---

## Exercise 4: Certificate Chain Inspection (~25 minutes)

**Goal:** Use `openssl` to inspect real certificate chains and understand the trust hierarchy.

### Part A: View a full certificate

```bash
echo | openssl s_client -connect github.com:443 2>/dev/null | openssl x509 -text -noout
```

This is a lot of output. Look for these key sections:

- **Issuer**: Who signed this certificate (the CA)
- **Validity**: Not Before / Not After dates
- **Subject**: The domain name(s)
- **Subject Alternative Name (SAN)**: Additional domain names this cert covers
- **X509v3 Key Usage**: What the certificate is allowed to do

### Part B: See the chain

```bash
echo | openssl s_client -connect github.com:443 -showcerts 2>/dev/null
```

Count the certificates in the output. Look for lines starting with:
```
 0 s:                (server certificate)
 1 s:                (intermediate CA)
```

The `s:` line is the **subject** (who the cert is for) and the `i:` line is the **issuer** (who signed it).

**Verify the chain:** The issuer of certificate 0 should match the subject of certificate 1. That's the chain!

### Part C: Compare chains across sites

```bash
echo | openssl s_client -connect google.com:443 2>/dev/null | openssl x509 -noout -subject -issuer
echo | openssl s_client -connect github.com:443 2>/dev/null | openssl x509 -noout -subject -issuer
echo | openssl s_client -connect cloudflare.com:443 2>/dev/null | openssl x509 -noout -subject -issuer
```

Notice:
- Google uses its own CA (Google Trust Services)
- GitHub might use DigiCert or another CA
- Cloudflare uses its own CA for its services

Different companies trust different CAs to sign their certificates.

### Part D: Check certificate dates

```bash
echo | openssl s_client -connect github.com:443 2>/dev/null | openssl x509 -noout -dates
```

How long until this certificate expires? Certificates from Let's Encrypt are valid for 90 days. Commercial CAs often issue certificates valid for 1-2 years.

### Self-check

- [ ] You can use `openssl` to view a certificate's subject, issuer, and dates
- [ ] You can see the certificate chain with `-showcerts`
- [ ] You can verify that the issuer of cert 0 matches the subject of cert 1
- [ ] You know that different sites use different Certificate Authorities

---

## Exercise 5: Watching TLS in Wireshark (~20 minutes)

**Goal:** See the TLS handshake and certificate exchange in Wireshark.

### Part A: Capture TLS traffic

1. Start a fresh Wireshark capture on your main network interface
2. In your terminal:
   ```bash
   curl -s https://example.com > /dev/null
   ```
3. Stop the capture

### Part B: Filter TLS traffic

In Wireshark, use the filter:
```
tls.handshake
```

You should see packets like:
- **Client Hello**: Your computer saying "I want to connect, I support these encryption methods"
- **Server Hello**: The server saying "Let's use this method"
- **Certificate**: The server sending its certificate chain

### Part C: Examine the certificate in the packet

Click on the **Certificate** packet (or the packet containing "Server Hello, Certificate"). In the packet details, expand:
- **Transport Layer Security** → **Handshake Protocol** → **Certificate**

You should see the certificate(s) listed, including:
- The server's certificate (with the domain name)
- The intermediate CA certificate

This is the same chain you saw with `openssl`, but now you can see it actually being transmitted over the network!

### Part D: See what's encrypted

After the handshake, look at the remaining packets. They'll show as **Application Data** — the actual HTTP request and response are inside, but encrypted. You can see the data is there, but you can't read it.

Compare this to Week 8 where HTTP traffic was readable in plain text. That's the difference a certificate and TLS make.

### Self-check

- [ ] You can capture and filter TLS handshake packets
- [ ] You can find the certificate inside the TLS handshake
- [ ] You can see that application data after the handshake is encrypted
- [ ] You understand the connection between the certificate you saw in `openssl` and what Wireshark shows

---

## Exercise 6: The Complete Chain — DNS to HTTPS (~20 minutes)

**Goal:** See the entire journey from typing a URL to getting a web page, in Wireshark.

### Part A: Capture everything

1. Start a fresh Wireshark capture
2. In your terminal:
   ```bash
   curl -v https://www.github.com 2>&1 | head -30
   ```
3. Stop the capture after a few seconds

### Part B: Find all the layers

Apply no filter (or use `ip.addr == <github's IP>`). Look for these packets in order:

1. **DNS query**: Your computer asks "what's the IP for www.github.com?"
2. **DNS response**: "It's a CNAME to github.com, which is 140.82.121.4"
3. **TCP SYN**: Start the TCP handshake to 140.82.121.4:443
4. **TCP SYN-ACK**: Server responds
5. **TCP ACK**: Handshake complete
6. **TLS Client Hello**: Start the TLS handshake
7. **TLS Server Hello + Certificate**: Server sends its certificate
8. **TLS Key Exchange**: Encryption keys negotiated
9. **Application Data**: The encrypted HTTP request and response

**This is everything you've learned since Week 5, in one capture.** DNS → TCP → TLS → HTTP.

### Part C: Connect it all with curl -v

Look at the `curl -v` output. It shows the same journey in text:

```
* Trying 140.82.121.4:443...          ← TCP connection (Step 3-5)
* Connected to github.com             ← TCP handshake done
* TLS handshake...                    ← TLS (Step 6-8)
* Server certificate:                 ← Certificate verification
*   subject: CN=github.com
*   issuer: ...DigiCert...
* SSL connection using TLS...         ← Encrypted tunnel ready
> GET / HTTP/1.1                      ← HTTP request (Step 9)
< HTTP/1.1 200 OK                     ← HTTP response
```

### Self-check

- [ ] You can see DNS, TCP, TLS, and HTTP packets in a single capture
- [ ] You understand the order: DNS → TCP → TLS → HTTP
- [ ] You can match the Wireshark packets to the `curl -v` output
- [ ] You see how Weeks 5, 8, and 9 all connect

---

## Exercise 7: DNS in Docker (~15 minutes)

**Goal:** Understand how Docker resolves container names — and why it only works on custom networks.

### Part A: Default bridge — no DNS

Start two containers on the default network:

```bash
docker run -d --name web1 nginx
docker run -it --rm alpine sh
```

From inside the alpine container, try:

```bash
# This will work (direct IP)
ping -c 2 $(getent hosts web1 | awk '{print $1}') 2>/dev/null || echo "Cannot resolve web1"

# This will NOT work (name resolution)
ping -c 2 web1
```

DNS name resolution doesn't work on the default bridge network. Exit the container (`exit`).

### Part B: Custom network — DNS works

```bash
docker network create testnet
docker run -d --name web2 --network testnet nginx
docker run -it --rm --network testnet alpine sh
```

From inside the alpine container:

```bash
# This works!
ping -c 2 web2
```

Docker's embedded DNS server (`127.0.0.11`) resolves container names on custom networks. This is the same reason `docker compose` services can find each other by name — Compose automatically creates a custom network.

Exit the container and clean up:

```bash
exit
```

```bash
docker stop web1 web2
docker rm web1 web2
docker network rm testnet
```

### Self-check

- [ ] You understand that Docker's default bridge network does **not** support DNS name resolution
- [ ] You understand that custom networks **do** support DNS name resolution
- [ ] You know this is why Docker Compose services can find each other by name

---

## Cleanup

Remove any containers from exercises:

```bash
docker ps -a --filter "name=web" -q | xargs -r docker rm -f
docker network ls --filter "name=testnet" -q | xargs -r docker network rm
```

---

## Summary

Today you explored:

| Topic | What You Did | Key Tool |
|-------|-------------|----------|
| DNS Resolution | Traced the full path from root servers to the answer | `dig +trace` |
| DNS Record Types | Looked up A, AAAA, CNAME, MX, NS, and TXT records | `dig` |
| DNS in Wireshark | Captured and analyzed DNS query/response packets | Wireshark |
| Certificate Chains | Inspected real certificate chains of trust | `openssl s_client` |
| TLS in Wireshark | Watched the certificate exchange during TLS handshake | Wireshark |
| The Full Chain | Saw DNS → TCP → TLS → HTTP in one capture | Wireshark + `curl -v` |
| Docker DNS | Learned why Docker Compose creates custom networks | Docker |

**Key takeaway:** Every time you visit a website, all of this happens automatically — DNS resolution, TCP handshake, certificate verification, TLS encryption, and finally the HTTP request. Now you've seen every step.
