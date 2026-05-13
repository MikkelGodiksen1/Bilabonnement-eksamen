# Post-Class Advanced Tasks: DNS & Certificates

**Estimated time: 1-2 hours total**

These tasks go deeper into DNS and certificates. They're independent — do them in any order. If you get stuck, check [hints.md](hints.md) for guidance.

---

## Task 1: Full DNS Resolution Analysis (~25 minutes)

**Objective:** Document the complete DNS resolution path for a domain and understand every step.

### Part A: Trace a domain

Pick a website you use regularly (your school, a company, a service). Run:

```bash
dig +trace yourdomain.com
```

### Part B: Document each step

Write down:

1. Which **root server** was queried? (Look for names like `a.root-servers.net`)
2. Which **TLD server** handled the next step? (e.g., `a.gtld-servers.net` for `.com`)
3. Which **authoritative nameserver** gave the final answer?
4. What is the final **A record** (IP address)?
5. What is the **TTL** on the final answer?

### Part C: Compare with a different TLD

Run `dig +trace` on a `.dk` domain and a `.org` domain. Notice how the TLD servers are completely different organizations.

### Part D: Query timing

```bash
dig google.com | grep "Query time"
```

Run it twice. The second time should be faster (cached). Try with a domain you've never looked up before — is the first query slower?

### Verify Success

- [ ] You can document the full resolution path (root → TLD → authoritative → answer)
- [ ] You can identify which organizations run the nameservers at each level
- [ ] You understand why repeated lookups are faster (caching)

---

## Task 2: DNS Records for Real Services (~25 minutes)

**Objective:** Investigate how real companies set up their DNS and understand the choices they make.

### Part A: Compare two large services

Pick two large websites (e.g., Google and Microsoft, or GitHub and GitLab). For each, look up:

```bash
dig +short example.com A
dig +short example.com AAAA
dig +short example.com MX
dig +short example.com NS
dig +short example.com TXT
dig +short www.example.com
```

### Part B: Answer these questions

1. How many A records does each domain have? (Multiple A records = load balancing)
2. Does the `www` subdomain use a CNAME or its own A record?
3. How many MX records are there? What are their priorities?
4. Look at the TXT records — can you find the SPF record? What mail services are authorized to send email for this domain?
5. Which nameserver provider do they use? (Look at the NS records — are they self-hosted or using a service like AWS Route 53, Cloudflare, etc.?)

### Part C: Look at your own school's domain

```bash
dig +short kea.dk A
dig +short kea.dk MX
dig +short kea.dk NS
dig +short kea.dk TXT
```

What can you learn about KEA's infrastructure from its DNS records?

### Verify Success

- [ ] You can compare DNS setups across different organizations
- [ ] You understand that multiple A records means load balancing
- [ ] You can read SPF records and understand what they authorize
- [ ] You can identify which DNS provider a company uses from NS records

---

## Task 3: Certificate Chain Deep Analysis (~25 minutes)

**Objective:** Examine certificate chains in detail and understand the trust hierarchy.

### Part A: Full certificate analysis

Pick three different websites (e.g., your bank, a social media site, and a small personal blog). For each:

```bash
echo | openssl s_client -connect example.com:443 -showcerts 2>/dev/null
```

Document for each site:
1. How many certificates are in the chain? (Count the `BEGIN CERTIFICATE` blocks)
2. What is the root CA? (The last issuer in the chain)
3. How long is the certificate valid?
4. Does the certificate cover wildcard domains? (Look for `*.example.com` in the Subject Alternative Names)

### Part B: Examine Subject Alternative Names (SANs)

```bash
echo | openssl s_client -connect google.com:443 2>/dev/null | openssl x509 -text -noout | grep -A1 "Subject Alternative Name"
```

How many domain names does Google's certificate cover? Big companies often put many domains on one certificate.

### Part C: Find a Let's Encrypt certificate

Many smaller sites use Let's Encrypt. Try some personal blogs or smaller services:

```bash
echo | openssl s_client -connect letsencrypt.org:443 2>/dev/null | openssl x509 -noout -issuer
```

When you find one, note:
- The issuer will say something about "Let's Encrypt" or "ISRG" (Internet Security Research Group)
- The validity period is 90 days (much shorter than commercial CAs)

### Part D: Certificate with many SANs vs wildcard

Compare:
```bash
# Google typically uses many SANs
echo | openssl s_client -connect google.com:443 2>/dev/null | openssl x509 -text -noout | grep "DNS:" | tr ',' '\n'

# Some sites use wildcards instead
echo | openssl s_client -connect github.com:443 2>/dev/null | openssl x509 -text -noout | grep "DNS:" | tr ',' '\n'
```

What's the difference in approach? Why might a company choose one over the other?

### Verify Success

- [ ] You can inspect and compare certificate chains across different sites
- [ ] You can identify root CAs, intermediate CAs, and server certificates
- [ ] You can find and read Subject Alternative Names
- [ ] You understand the difference between Let's Encrypt and commercial CAs

---

## Task 4: DNS Caching Investigation (~20 minutes)

**Objective:** Understand how DNS caching works and its practical implications.

### Part A: Observe TTL countdown

Run this several times quickly:

```bash
dig example.com | grep -A1 "ANSWER SECTION"
```

Watch the TTL number decrease with each query. When it hits 0, your resolver will ask the authoritative server again and the TTL resets.

### Part B: Compare TTLs across sites

```bash
dig +short +answer google.com | head -1      # Note the TTL
dig +short +answer github.com | head -1
dig +short +answer wikipedia.org | head -1
```

Wait — `+short` hides the TTL. Use the full format instead:

```bash
dig google.com A | grep "ANSWER" -A2
dig github.com A | grep "ANSWER" -A2
dig wikipedia.org A | grep "ANSWER" -A2
```

Which site has the lowest TTL? Which has the highest? Why might a site choose a very low TTL?

### Part C: Different resolvers, different caches

```bash
dig @8.8.8.8 github.com | grep "ANSWER" -A2
dig @1.1.1.1 github.com | grep "ANSWER" -A2
```

The TTL values might be different because each resolver caches independently. If one resolver fetched the record 30 seconds ago and the other 5 seconds ago, their remaining TTLs will differ.

### Part D: What happens when DNS changes?

Think about this scenario: you change your website's IP address. The old TTL was 3600 (1 hour).

1. How long before all users see the new IP?
2. What would happen if the TTL was 60 seconds? 86400 seconds (24 hours)?
3. Why might you lower the TTL *before* making a change?

Write your answers down — this comes up in real-world operations.

### Verify Success

- [ ] You can observe TTL decreasing with repeated queries
- [ ] You understand why different sites choose different TTLs
- [ ] You know that different resolvers have independent caches
- [ ] You can explain the trade-off between high and low TTLs

---

## Task 5: Build Your Knowledge Document (~15 minutes)

**Objective:** Connect what you've learned to the bigger picture and identify what you now understand.

Create a short document (text file, notes app, or paper) that includes:

### Part A: In your own words

Explain the journey from typing `https://github.com` to seeing the page, covering:
1. DNS resolution
2. TCP handshake
3. Certificate verification
4. TLS encryption
5. HTTP request/response

Use your own words — don't copy from the materials.

### Part B: Three things that clicked

Write down three things that now make sense that didn't before:
- Something about DNS?
- Something about how HTTPS actually works?
- Something that connects back to Week 5 or Week 8?

### Part C: Questions for upcoming weeks

- Week 10 covers encryption (symmetric vs asymmetric). What questions do you have about how the actual encryption works inside TLS?
- When a certificate contains a "public key" — what does that actually mean?
- How does SSH key authentication (which you've been using since Week 4) relate to the certificates you learned about today?

### Verify Success

- [ ] You can explain the full journey from URL to web page in your own words
- [ ] You've identified three concepts that clicked during this week
- [ ] You have questions ready for Week 10 (encryption)
