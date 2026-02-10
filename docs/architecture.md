# System Architecture

## Overview

The Lead Submission Observability Lab is designed as an **observability-first backend system** with the following parts:

- Data ingestion through lead submission APIs
- Data storage for leads and lead submission attempts (stored separately)
- Dashboard for data overview and AI-based analysis of logged submission attempts
- User creation, which is required for submitting leads
- Dashboard access and AI analysis are available without user creation

The system is exposed externally using a **secure tunneling-based access model** and does not rely on direct public network exposure.

---

## High-Level Architecture

At a high level, the system consists of the following layers:

1. **Edge / Exposure Layer (Cloudflare Tunnel)**
2. **UI Layer**
3. **Application Server**
4. **AI Analysis Service**
5. **Database Layer**

---

## Component Breakdown

### 1. Edge / Exposure Layer (Cloudflare Tunnel)

This layer is responsible for secure external access.

It provides:

- TLS termination at the edge
- DNS-based access via a fixed domain
- Outbound-only encrypted connectivity to the application server
- Protection against direct public IP exposure

Key characteristics:

- No inbound firewall ports
- No public IP routing to the server
- Traffic forwarded internally to the application server over localhost

This layer is transport-focused and does not inspect or modify application logic.

---

### 2. UI Layer

The UI layer provides:

- Lead submission forms
- Dashboard views showing aggregated metrics
- Date-wise AI analysis triggers
- Loading and execution state for long-running analysis
- Sends HTTP requests to backend APIs
- Renders API responses
- Displays execution status to the user

The UI does not perform validation, aggregation, or AI logic.

All UI traffic enters the system through the Cloudflare Tunnel.

---

### 3. Application Server

The application server handles all core system operations.

It is responsible for:

- Accepting lead submission requests
- Validating request payloads
- Logging every submission attempt
- Logging successful lead data
- Generating daily and summary metrics
- Enforcing submission rules and limits
- Coordinating AI analysis execution
- Serving dashboard APIs
- Serving static frontend assets

The application server exposes:

- Public APIs for lead submission and dashboard access
- Internal APIs for triggering AI analysis

All AI calls originate from the application server.

---

### 4. AI Analysis Service

The AI analysis service runs as a separate process and is accessed over HTTP.

Its responsibilities are limited to:

- Receiving a pre-constructed prompt
- Running inference on the prompt
- Returning structured analysis output

The AI service:

- Does not access the database
- Does not receive raw logs
- Does not modify system state
- Does not trigger any downstream actions

It operates only on the data explicitly provided in the prompt.

---

### 5. Database Layer

The Database layer stores all system data.

This includes:

- User records
- Lead records
- Lead submission attempt logs
- Daily and total lead data summaries
- AI-generated daily analysis reports

Key characteristics:

- Submission logs are append-only
- Historical data is never modified
- AI analysis for past dates is immutable
- AI analysis for the current date is generated on every new request

All dashboards and AI analysis are derived from database data.

---

## Data Flow

### Lead Submission Flow

1. Lead data is submitted by the UI
2. Request enters via Cloudflare Tunnel
3. Application server validates the request
4. Submission attempt is recorded
5. On success, lead data is stored along with attempt logs
6. On failure, failure details are logged

---

### Dashboard Metrics Flow

1. UI requests dashboard metrics
2. Request enters via Cloudflare Tunnel
3. Application server queries stored data
4. Metrics are computed
5. Metrics are returned synchronously
6. UI renders charts and counters

---

### AI Analysis Flow (Date-wise)

1. User selects a date and triggers analysis
2. Application server checks for existing analysis for the date
3. If analysis exists and is immutable, it is returned
4. Otherwise:
   - Summarized data is assembled
   - A bounded prompt is constructed
   - The AI service is called synchronously
   - The result is validated and persisted
5. The analysis response is returned to the UI

---

## Execution Model

- AI analysis is executed synchronously
- The triggering request remains open until completion  
- Connections may time out after ~60 seconds if AI execution exceeds limits
- Past-date analysis is reused when available
- Current-date analysis is generated on every request

---

## Architectural Constraints

The current architecture includes the following constraints:

- AI inference can be slow due to hardware limitations
- Long-running HTTP requests are expected
- Network tunnels may drop or stall long-lived connections
- Restarting services clears in-flight requests

These behaviors are observed and documented.

---

## Forward Compatibility

The current structure supports future changes such as:

- Asynchronous AI execution
- Background job processing
- Status persistence for analysis jobs
- Polling-based UI updates

These changes can be introduced without modifying existing data models.

---
