# Pre-Class Exercises: DNS & Certificates

**Estimated time: 30-60 minutes**

Work through these exercises before class. If you get stuck on any exercise for more than 10 minutes, note where you had problems and move on — we'll troubleshoot together in class.

---

## Exercise 1: Set Up Your Tools

**Goal:** Make sure `dig`, `nslookup`, and `openssl` are available on your machine.

### 1.1 Check nslookup (all platforms)

`nslookup` is built into Windows, macOS, and Linux. Open a terminal and run:

```bash
nslookup google.com
```

You should see output showing an IP address. If this works, you have at least one DNS tool ready.

### 1.2 Check dig

**Linux:**
```bash
dig google.com
```
If not installed: `sudo apt install dnsutils` (Debian/Ubuntu) or `sudo dnf install bind-utils` (Fedora).

**macOS:**
```bash
dig google.com
```
`dig` is built-in on macOS.

**Windows:**
`dig` is not included with Windows. Use Docker instead:
```bash
docker run -it --rm alpine sh -c "apk add --no-cache bind-tools && sh"
```
Then run `dig google.com` inside the container. You'll use this approach for class exercises too.

### 1.3 Check openssl

**Linux/macOS:**
```bash
openssl version
```
Should show something like `OpenSSL 3.0.x` or `LibreSSL 3.x.x`.

**Windows:**
If you have Git Bash installed, `openssl` is included. Try running it in Git Bash. Otherwise, use Docker:
```bash
docker run -it --rm alpine sh -c "apk add --no-cache openssl && sh"
```

### Self-check

- [ ] `nslookup google.com` returns an IP address
- [ ] `dig google.com` works (natively or in Docker)
- [ ] `openssl version` shows a version number (natively or in Docker/Git Bash)

---

## Exercise 2: Your First DNS Lookups

**Goal:** Use `dig` to look up DNS records and understand the output.

### 2.1 Look up an A record

```bash
dig github.com
```

Find the **ANSWER SECTION** in the output. You should see something like:

```
;; ANSWER SECTION:
github.com.        60    IN    A    140.82.121.4
```

This tells you: `github.com` has an IPv4 address of `140.82.121.4`, and this answer can be cached for 60 seconds.

### 2.2 Try the short format

```bash
dig +short github.com
```

This gives you just the IP address — useful for scripting.

### 2.3 Look up different record types

Try each of these and look at the ANSWER SECTION:

```bash
dig github.com A          # IPv4 address
dig github.com AAAA       # IPv6 address
dig google.com MX         # Mail servers
dig github.com NS         # Nameservers
dig github.com TXT        # Text records (SPF, verification, etc.)
```

### 2.4 Follow a CNAME

```bash
dig www.github.com
```

Notice that `www.github.com` is a **CNAME** (alias) that points to `github.com`. The resolver then gives you the A record for `github.com` too.

### 2.5 Query a specific DNS server

```bash
dig @8.8.8.8 github.com        # Ask Google's DNS
dig @1.1.1.1 github.com        # Ask Cloudflare's DNS
```

The results should be the same (same IP), but the response time and TTL might differ.

### Self-check

- [ ] You can find the ANSWER SECTION in `dig` output
- [ ] You know what A, AAAA, CNAME, MX, and NS records are
- [ ] You can see a CNAME alias when looking up `www.github.com`
- [ ] You can query a specific DNS server with `@`

---

## Exercise 3: Inspect a Certificate in Your Browser

**Goal:** See a real certificate chain of trust in your browser.

### 3.1 Open a website with HTTPS

Open `https://github.com` in your browser (Chrome, Firefox, or Edge).

### 3.2 View the certificate

**Chrome/Edge:**
1. Click the **padlock** icon (or tune icon) in the address bar
2. Click **"Connection is secure"**
3. Click **"Certificate is valid"**

**Firefox:**
1. Click the **padlock** icon in the address bar
2. Click **"Connection secure"**
3. Click **"More information"**
4. Click **"View Certificate"**

### 3.3 Examine the certificate details

Look for:
- **Issued to (Subject):** The domain name(s) — should include `github.com`
- **Issued by (Issuer):** The Certificate Authority — note the name
- **Valid from / to:** When the certificate was issued and when it expires
- **Certificate chain:** Look for the hierarchy — you should see 2-3 levels:
  - Server certificate (github.com)
  - Intermediate CA
  - Root CA

### 3.4 Compare with another site

Open `https://google.com` and view its certificate. Notice:
- Different issuer (likely Google Trust Services)
- Different validity dates
- Different chain structure

### Self-check

- [ ] You can find and open the certificate viewer in your browser
- [ ] You can identify the subject, issuer, and validity dates
- [ ] You can see the certificate chain (server → intermediate → root)
- [ ] You notice that different sites use different Certificate Authorities

---

## Exercise 4: Inspect a Certificate with openssl

**Goal:** Use the command line to view certificate details — more information than the browser shows.

### 4.1 Connect and view a certificate

```bash
echo | openssl s_client -connect github.com:443 2>/dev/null | openssl x509 -noout -subject -issuer -dates
```

You should see output like:
```
subject=CN = github.com
issuer=C = US, O = DigiCert Inc, CN = DigiCert SHA2 Extended Validation Server CA
notBefore=Feb 14 00:00:00 2023 GMT
notAfter=Mar 14 23:59:59 2025 GMT
```

This tells you: the certificate is for `github.com`, signed by DigiCert, valid from Feb 2023 to Mar 2025.

### 4.2 See the certificate chain

```bash
echo | openssl s_client -connect github.com:443 -showcerts 2>/dev/null
```

Look for lines that say `s:` (subject) and `i:` (issuer). You should see a chain:
- Certificate 0: `github.com` issued by an intermediate CA
- Certificate 1: The intermediate CA issued by the root CA

### 4.3 Try another site

```bash
echo | openssl s_client -connect google.com:443 2>/dev/null | openssl x509 -noout -subject -issuer -dates
```

Compare the output with github.com — different CA, different dates.

### Self-check

- [ ] You can use `openssl s_client` to view a certificate
- [ ] You can identify the subject, issuer, and dates from the output
- [ ] You can see the certificate chain with `-showcerts`

---

## Troubleshooting

**"dig: command not found"**
- Install it: `sudo apt install dnsutils` (Linux) or use the Docker approach from Exercise 1.2

**"openssl: command not found"**
- Try Git Bash on Windows, or use the Docker approach from Exercise 1.3

**openssl hangs / doesn't return**
- Make sure you're piping `echo |` at the start: `echo | openssl s_client -connect ...`
- Or add `</dev/null` at the end: `openssl s_client -connect ... </dev/null`

**"Connection refused" with openssl**
- Make sure you're using port 443 (HTTPS port): `-connect example.com:443`

---

## Before Class Checklist

### Tools Ready
- [ ] `dig` works (natively or in Docker)
- [ ] `nslookup` works
- [ ] `openssl` works (natively, Git Bash, or Docker)
- [ ] Wireshark installed and working (from Week 5)

### Knowledge
- [ ] You know what DNS does (translates domain names to IP addresses)
- [ ] You can identify A, AAAA, CNAME, MX, and NS records
- [ ] You can view a certificate in your browser and identify the chain of trust
- [ ] You can use `openssl s_client` to inspect a certificate from the command line
