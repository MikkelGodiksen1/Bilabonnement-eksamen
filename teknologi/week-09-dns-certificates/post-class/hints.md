# Hints for Advanced Tasks

Try to work through each task on your own first! These hints are here if you get stuck.

---

## Task 1: Full DNS Resolution Analysis

<details>
<summary>Hint 1: How to read dig +trace output</summary>

The output is divided into sections. Each section represents one step in the resolution:

1. The first section shows **root servers** (the `.` zone) — you'll see NS records for `a.root-servers.net`, `b.root-servers.net`, etc.
2. The next section shows **TLD servers** — for `.com`, you'll see `a.gtld-servers.net`, etc.
3. The final section shows the **authoritative answer** — the A record with the actual IP.

Look for the line that says `Received xxx bytes from ...` — it tells you which server answered at each step.
</details>

<details>
<summary>Hint 2: Finding the query time</summary>

At the bottom of `dig` output, look for:
```
;; Query time: 23 msec
```

The first lookup for a domain will typically be 20-100ms. Cached lookups drop to 0-5ms. This is because your local DNS resolver already has the answer stored.
</details>

<details>
<summary>Hint 3: Understanding TTL on the final answer</summary>

In the ANSWER SECTION:
```
github.com.    60    IN    A    140.82.121.4
```
The `60` is the TTL in seconds. This means your resolver can cache this answer for 60 seconds before it needs to ask again. GitHub uses a low TTL so they can change IPs quickly if needed (e.g., during an incident).
</details>

---

## Task 2: DNS Records for Real Services

<details>
<summary>Hint 1: Multiple A records and load balancing</summary>

If `dig +short example.com A` returns multiple IP addresses, the domain is using DNS-based load balancing. Each time a client queries, the DNS server may rotate the order of IPs, spreading traffic across multiple servers.

Example:
```
$ dig +short google.com A
142.250.74.46
142.250.74.78
142.250.74.110
```
</details>

<details>
<summary>Hint 2: Reading SPF records in TXT</summary>

SPF (Sender Policy Framework) records start with `v=spf1`. They list which servers are authorized to send email for that domain:

```
"v=spf1 include:_spf.google.com include:servers.mcsv.net -all"
```

This means:
- `include:_spf.google.com` — Google's mail servers can send email for this domain
- `include:servers.mcsv.net` — Mailchimp can send email too
- `-all` — nobody else is authorized (reject all others)
</details>

<details>
<summary>Hint 3: Identifying DNS providers from NS records</summary>

Common NS record patterns:
- `ns1.google.com` → Google Cloud DNS
- `xxx.awsdns-xx.com` → AWS Route 53
- `xxx.cloudflare.com` → Cloudflare DNS
- `ns1.p08.nsone.net` → NS1 (used by GitHub)
- `xxx.domaincontrol.com` → GoDaddy

The NS records tell you which company manages the domain's DNS, which might be different from where the website is hosted.
</details>

---

## Task 3: Certificate Chain Deep Analysis

<details>
<summary>Hint 1: Counting certificates in the chain</summary>

When you run `openssl s_client -connect ... -showcerts`, look for `BEGIN CERTIFICATE` markers. Each one is a separate certificate:

```
-----BEGIN CERTIFICATE-----
(base64 encoded data)
-----END CERTIFICATE-----
```

Typically you'll see 2 certificates: the server cert and the intermediate CA. The root CA is usually not sent — your browser already has it.
</details>

<details>
<summary>Hint 2: Finding Subject Alternative Names</summary>

SANs are in the `X509v3 extensions` section. Use this command to extract them:

```bash
echo | openssl s_client -connect google.com:443 2>/dev/null | openssl x509 -text -noout | grep -A1 "Subject Alternative Name"
```

You'll see something like:
```
X509v3 Subject Alternative Name:
    DNS:*.google.com, DNS:google.com, DNS:*.youtube.com, ...
```

Google famously puts dozens of domains on a single certificate.
</details>

<details>
<summary>Hint 3: Wildcards vs many SANs</summary>

A **wildcard certificate** (`*.example.com`) covers all subdomains of one domain. It's simpler but only works for one level of subdomain.

**Multiple SANs** (`DNS:google.com, DNS:youtube.com, DNS:gmail.com`) can cover completely different domain names on one certificate. This is more flexible but the certificate gets larger.

Companies choose based on their domain structure. If you have many subdomains of one domain, a wildcard works well. If you have many separate domains, you need SANs.
</details>

---

## Task 4: DNS Caching Investigation

<details>
<summary>Hint 1: Watching TTL decrease</summary>

Run `dig example.com` several times quickly. Look at the number in the ANSWER SECTION:

```
;; ANSWER SECTION:
example.com.    3600    IN    A    93.184.216.34    ← first query
example.com.    3587    IN    A    93.184.216.34    ← 13 seconds later
example.com.    3574    IN    A    93.184.216.34    ← 26 seconds later
```

The TTL counts down from the original value. When it hits 0, the resolver fetches a fresh answer.
</details>

<details>
<summary>Hint 2: Why sites choose different TTLs</summary>

- **Low TTL (60-300 seconds)**: The site changes IPs frequently, uses CDNs with dynamic routing, or wants fast failover during outages. Cost: more DNS queries.
- **High TTL (3600-86400 seconds)**: The site's IP rarely changes. Benefit: faster loading (no DNS lookup needed), less load on DNS servers.

Content Delivery Networks (CDNs) like Cloudflare often use very low TTLs because they route users to the nearest server, and the "nearest" might change.
</details>

<details>
<summary>Hint 3: Pre-change TTL strategy</summary>

When planning to change a server's IP, a common practice is:

1. **Days before**: Lower the TTL from 3600 to 60
2. **Wait**: Let the old TTL expire everywhere (up to 24+ hours)
3. **Make the change**: Update the A record to the new IP
4. **After confirmation**: Raise the TTL back to 3600

This way, when you make the actual change, the old IP is only cached for 60 seconds maximum, instead of up to an hour.
</details>

---

## Task 5: Build Your Knowledge Document

<details>
<summary>Hint: Structure for the journey explanation</summary>

Here's a framework:

1. **DNS**: "My browser asks a DNS resolver for the IP of github.com. The resolver walks the DNS hierarchy (root → .com → github.com's nameservers) and returns 140.82.121.4."

2. **TCP**: "My browser opens a TCP connection to 140.82.121.4:443 using the three-way handshake (SYN, SYN-ACK, ACK)."

3. **Certificate**: "The server sends its TLS certificate, which is signed by an intermediate CA, which is signed by a root CA my browser already trusts."

4. **TLS**: "After verifying the certificate, my browser and the server negotiate encryption keys. All traffic from now on is encrypted."

5. **HTTP**: "Inside the encrypted tunnel, my browser sends GET / HTTP/1.1 and the server responds with the web page."

Each step builds on the previous one. DNS gives you the address. TCP gives you a connection. Certificates give you identity verification. TLS gives you encryption. HTTP gives you the actual content.
</details>

---

## General Troubleshooting

**"dig: command not found"**
- Linux: `sudo apt install dnsutils`
- macOS: Built-in — try closing and reopening terminal
- Windows: Use Docker: `docker run -it --rm alpine sh -c "apk add --no-cache bind-tools && sh"`

**openssl shows "verify error"**
- This is normal in some cases. The `-showcerts` flag still shows the certificates even if verification fails. Some servers don't send the full chain.

**Wireshark doesn't show DNS packets**
- Make sure you're capturing on the right interface (your internet-connected interface, not loopback)
- Make sure the filter is exactly `dns` (lowercase)
- Try running `dig` while actively capturing

**TTL doesn't seem to change**
- Your DNS resolver might return a new cached copy each time. Try querying a specific upstream resolver: `dig @8.8.8.8 example.com`

---

## Still Stuck?

- [ ] Re-read the relevant section in [reading.md](../pre-class/reading.md)
- [ ] Check the [quick-reference.md](../quick-reference.md) for command syntax
- [ ] Ask in class or on the course forum
- [ ] Search for the specific error message you're seeing
