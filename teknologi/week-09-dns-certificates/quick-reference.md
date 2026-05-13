# Week 9 Quick Reference Card

Print this page or keep it open while working!

---

## DNS Record Types

| Record | Points To | Example | Purpose |
|--------|-----------|---------|---------|
| **A** | IPv4 address | `93.184.216.34` | Main address record |
| **AAAA** | IPv6 address | `2606:2800:220:1:...` | IPv6 address record |
| **CNAME** | Another domain name | `www.example.com → example.com` | Alias / redirect |
| **MX** | Mail server | `mail.example.com` | Where to deliver email |
| **NS** | Nameserver | `ns1.example.com` | Who is authoritative for this domain |
| **TXT** | Free text | `"v=spf1 include:..."` | Verification, SPF, DKIM |
| **SOA** | Zone info | serial, refresh, retry... | Zone administration |

---

## DNS Hierarchy

```
.                          ← Root (13 root server addresses worldwide)
├── com.                   ← TLD (Top-Level Domain)
│   ├── google.com.        ← Second-level domain
│   └── github.com.
├── dk.                    ← Country-code TLD
│   └── kea.dk.
├── org.
│   └── wikipedia.org.
└── net.
```

Every domain name ends with a dot (the root). Your browser just hides it.

---

## dig Commands

| Command | What It Does |
|---------|-------------|
| `dig example.com` | Look up A record (IPv4 address) |
| `dig example.com AAAA` | Look up IPv6 address |
| `dig example.com MX` | Look up mail servers |
| `dig example.com NS` | Look up nameservers |
| `dig example.com TXT` | Look up TXT records |
| `dig example.com ANY` | Try to get all records |
| `dig +short example.com` | Show just the answer |
| `dig +trace example.com` | Show full resolution path (root → TLD → authoritative) |
| `dig @8.8.8.8 example.com` | Query a specific DNS server (Google) |
| `dig @1.1.1.1 example.com` | Query a specific DNS server (Cloudflare) |

### Reading dig Output

```
;; ANSWER SECTION:
example.com.        3600    IN    A    93.184.216.34
│                   │       │     │    │
│                   │       │     │    └── The IP address
│                   │       │     └── Record type
│                   │       └── Internet class
│                   └── TTL (seconds until cache expires)
└── Domain name (note the trailing dot)
```

---

## nslookup Commands

| Command | What It Does |
|---------|-------------|
| `nslookup example.com` | Basic lookup (works on all platforms) |
| `nslookup -type=MX example.com` | Look up mail servers |
| `nslookup -type=NS example.com` | Look up nameservers |
| `nslookup example.com 8.8.8.8` | Query a specific DNS server |

---

## Certificate Inspection

### With openssl

```bash
# View certificate for a website
openssl s_client -connect example.com:443 -showcerts </dev/null 2>/dev/null | openssl x509 -text -noout

# Quick view — just subject and issuer
openssl s_client -connect example.com:443 </dev/null 2>/dev/null | openssl x509 -noout -subject -issuer -dates

# See the full certificate chain
openssl s_client -connect example.com:443 -showcerts </dev/null
```

### In Browser

1. Click the **padlock** icon in the address bar
2. Click **"Connection is secure"** (or similar)
3. Click **"Certificate"** or **"Certificate is valid"**
4. Look at:
   - **Issued to**: The domain the certificate is for
   - **Issued by**: The Certificate Authority
   - **Valid from / to**: Expiration dates
   - **Certificate chain**: Root CA → Intermediate CA → Server cert

---

## Certificate Chain of Trust

```
Root CA (pre-installed in your browser/OS)
  │
  ├── Signed the intermediate CA's certificate
  │
  Intermediate CA
  │
  ├── Signed the server's certificate
  │
  Server Certificate (example.com)
    Contains: domain name, public key, expiration, issuer signature
```

Your browser trusts a server certificate because:
1. The server cert is signed by an intermediate CA
2. The intermediate CA cert is signed by a root CA
3. The root CA is already trusted (pre-installed)

---

## Wireshark DNS Filters

| Filter | What It Shows |
|--------|--------------|
| `dns` | All DNS traffic |
| `dns.qry.name == "example.com"` | Queries for a specific domain |
| `dns.qry.type == 1` | A record queries only |
| `dns.qry.type == 28` | AAAA record queries only |
| `dns.qry.type == 15` | MX record queries only |
| `dns.flags.response == 0` | DNS queries only (not responses) |
| `dns.flags.response == 1` | DNS responses only |
| `udp.port == 53` | All traffic on DNS port |

---

## The Full HTTPS Chain

```
1. DNS Lookup        dig google.com → 142.250.74.46
2. TCP Handshake     SYN → SYN-ACK → ACK (Week 5)
3. TLS Handshake     ClientHello → ServerHello + Certificate → Keys (Week 9)
4. HTTP Request      GET / HTTP/1.1 (Week 8, but now encrypted)
5. HTTP Response     HTTP/1.1 200 OK (encrypted)
6. Connection Close  FIN → FIN-ACK
```

---

## Common Public DNS Servers

| Provider | IPv4 | IPv6 |
|----------|------|------|
| Google | `8.8.8.8`, `8.8.4.4` | `2001:4860:4860::8888` |
| Cloudflare | `1.1.1.1`, `1.0.0.1` | `2606:4700:4700::1111` |
| Quad9 | `9.9.9.9` | `2620:fe::fe` |

---

## Docker DNS

- Default bridge network: containers **cannot** resolve each other by name
- Custom networks: Docker's embedded DNS (`127.0.0.11`) resolves container names
- This is why `docker compose` creates a custom network — so services find each other by name

---

## Remember

1. **DNS translates names to IPs** — without it, you'd need to memorize `142.250.74.46`
2. **dig +trace** shows the full resolution path from root servers
3. **A records** point to IPv4, **AAAA** to IPv6, **CNAME** is an alias
4. **TTL** controls caching — lower TTL means more frequent lookups
5. **Certificates prove identity** — they're signed by a trusted CA
6. **Chain of trust**: server cert → intermediate CA → root CA (pre-installed)
7. **The padlock** in your browser means the certificate was verified and traffic is encrypted
