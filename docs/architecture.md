# System Architecture

## Overview

The Lead Submission Observability Lab is designed as an **observability-first backend system** with the following parts:

- Data ingestion through lead submission APIs
- Data storage for leads and lead submission attempts (stored separately)
- Dashboard for data overview and AI-based analysis of logged submission attempts
- User creation, which is required for submitting leads
- Dashboard access and AI analysis are available without user creation

---

## High-Level Architecture

At a high level, the system consists of the following layers:

1. **UI Layer**
2. **Application Server**
3. **AI Analysis Service**
4. **Database Layer**

---

## Component Breakdown

### 1. UI Layer

It provides:

- Lead submission forms
- Dashboard views showing aggregated metrics
- Date-wise AI analysis triggers
- Loading and execution state for long-running analysis
- Sends HTTP requests to backend APIs
- Renders API responses
- Displays execution status to the user

The UI does not perform validation, aggregation, or AI logic.

---

### 2. Application Server

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

### 3. AI Analysis Service

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

### 4. Database Layer

The Database layer stores all system data.

This includes:

- User records
- Lead records
- Lead submission attempt logs
- Daily and Total lead data summary - Leads, Total Submission, Successful submission, success percentage 
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

1. Lead data is submitted by UI
2. Application server validates the request
3. Submission attempt is recorded
4. On success, lead data is stored along with attempt logs
5. On failure, failure details are logged

---

### Dashboard Metrics Flow

1. UI requests dashboard metrics
2. Application server queries stored data
3. Metrics are computed
4. Metrics are returned synchronously
5. UI renders charts and counters

---

### AI Analysis Flow (Date-wise)

1. User selects a date and triggers analysis
2. Application server checks for existing analysis for the date
3. If analysis exists and is immutable, it is returned
4. Otherwise:
   - Generated summarized data is assembled
   - A bounded prompt is constructed
   - The AI service is called synchronously
   - The result is validated and persisted
5. The analysis response is returned to the UI

---

## Execution Model

- AI analysis is executed synchronously
- The triggering request remains open until completion. Coonection is timed out after 60 seconds if AI is taking longer top generate response
- Execution time may range from several seconds to over a minute
- Past-date analysis is reused when available fior past dates, current data analysis is generated everytime the API is called

---

## Architectural Constraints

The current architecture includes the following constraints:

- AI inference can be slow (Due to System constrainst. you can check system information in system-info.md)
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
