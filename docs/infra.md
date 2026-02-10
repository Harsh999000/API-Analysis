# Infrastructure & Deployment Architecture

## Overview

This project is deployed using a **local-first, zero-exposure infrastructure model** built on top of **Cloudflare Tunnel**.

The application runs on a **personal Linux machine** and is exposed securely to the internet **without opening any inbound ports or exposing a public IP**.

The setup is intentionally designed to resemble **production-grade access patterns** while remaining lightweight and self-managed.

---

## High-Level Architecture

- Application: Spring Boot (Java 17)
- Runtime host: Personal Linux machine
- Web server: Embedded Tomcat
- External access: Cloudflare Tunnel (cloudflared)
- Domain: `apianalysis.harshjha.co.in`
- TLS termination: Cloudflare edge
- Logs: Local-only (not versioned)

All external traffic reaches the application **only through Cloudflare’s edge network**.

---

## Traffic Flow

1. A user accesses  
   `https://apianalysis.harshjha.co.in`

2. Cloudflare receives the request at the edge and terminates TLS.

3. Cloudflare routes the request to a **named Cloudflare Tunnel**.

4. The tunnel establishes an **outbound-only encrypted connection** from the local machine to Cloudflare.

5. Traffic is forwarded internally to:
http://localhost:8110

6. The Spring Boot application processes the request and returns the response through the same tunnel.

At no point does the local machine accept unsolicited inbound connections.

---

## Cloudflare Tunnel Model

This project uses a **named Cloudflare Tunnel**, not ephemeral or quick tunnels.

Key characteristics:

- No `trycloudflare.com` URLs
- Stable DNS mapping via Cloudflare
- Tunnel credentials stored outside the Git repository
- Tunnel process managed via shell scripts

The tunnel is started using:

```bash
cloudflared tunnel run apianalysis
