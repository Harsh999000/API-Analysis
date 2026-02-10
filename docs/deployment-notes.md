# Deployment Notes

## Overview

The system is designed to run on modest hardware with clearly defined operational boundaries.

---

## Deployment Model

The system is deployed as multiple independent components:

- Application server
- Database
- AI inference server
- External exposure layer (Cloudflare Tunnel)

Each component operates independently and can be restarted without affecting data correctness.

---

## Application Server

The application server is responsible for:

- Handling API requests
- Logging submission attempts
- Aggregating metrics
- Coordinating AI analysis
- Serving dashboard and static UI

The server contains no AI model logic.

---

## AI Inference Server

The AI inference server runs as a separate process.

Its characteristics:

- Independent lifecycle from the application server
- Dedicated compute usage
- No direct access to system data stores
- Stateless with respect to application state

If the AI server is unavailable, the system continues to operate without AI analysis.

---

## Long-Running Requests

AI analysis requests may take between **20 and 60 seconds**.

Current behavior:

- AI calls are executed synchronously
- The HTTP connection remains open until completion
- The application server blocks for the duration of analysis

This behavior is expected in the current design.

---

## Tunneling and Exposure

External access is provided using **Cloudflare Tunnel (cloudflared)** with a fixed custom domain.

Characteristics of the exposure model:

- No public IP exposure
- No inbound firewall ports opened
- All traffic flows through an outbound-only encrypted tunnel
- TLS termination handled by Cloudflare
- Stable DNS-based access (not ephemeral URLs)

Due to the nature of long-running requests:

- Long-lived HTTP connections may stall without explicit errors
- Tunnel processes may retain stale connection state
- Requests may appear to hang without reaching the AI server

These issues are related to tunnel and connection handling rather than application logic.

---

## Operational Constraints

The system may run on personal or limited hardware.

Operational characteristics include:

- Planned downtime from 12:30 A.M. – 8:00 A.M

Continuous uptime is not guaranteed.

---

## Recovery Procedure

If analysis or connectivity issues occur, recovery is performed by restarting components in sequence:

1. Restart the application server
2. Restart the AI inference server
3. Restart the Cloudflare Tunnel process

This clears stale tunnel connections and restores normal request flow.

---

## Known Limitations

- AI analysis is synchronous
- HTTP connections may be held for extended durations
- Tunnel stability may degrade under long-running requests
- No retry or resume mechanism for in-flight analysis

These limitations are documented and accepted in the current version.

---

## Future Improvements

Planned deployment enhancements include:

- Asynchronous AI job execution
- Background processing workers
- Status-based polling from the UI
- Removal of long-held HTTP connections

These changes will improve reliability and scalability.

---
