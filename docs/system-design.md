# System Design

## Overview

The Lead Submission Observability Lab is designed as a backend system that records, aggregates, and analyzes lead submission activity.

The system focuses on:

- Logging every lead submission attempt
- Separating raw data from analysis
- Providing dashboard views over logged data
- Generating AI-based analysis from aggregated logs

---

## Core Design Principles

### Logging First

Every meaningful system action results in logged data.

This includes:

- Successful lead submissions
- Failed submission attempts
- Validation failures
- Rate-limit rejections

Nothing important occurs without being logged.

---

### Immutable Historical Data

Once a day has passed:

- Submission logs for that day are not modified
- Aggregated metrics for that day do not change
- AI analysis for that day is treated as final

Only data related to the **current day** may be updated or regenerated.

---

### Explicit Execution Boundaries

The system separates responsibilities between:

- Data logging
- Data summary
- Data analysis
- Data presentation

AI operates only on aggregated data and does not interact with:

- Raw logging logic
- Submission handling
- Backend Logic

---

## Lead Submission Design

### Submission Handling

Each lead submission attempt is handled independently.

For every attempt, the system logs:

- User identifier
- Submission source
- Timestamp
- Result (success or failure)
- Failure reason, if applicable

Successful attempts result in a lead record.  
Failed attempts are logged without creating a lead.

---

### Failure Transparency

Failures are logged in the same way as successful attempts.

The system logs:

- Failure category
- Failure message or reason
- Context related to the failure

Failure data is visible in dashboards and included in AI analysis.

---

## Metrics Logging Design

### Logging Strategy

Metrics are computed from logged submission attempts.

Summary data are calculated using stored logs.

---

### Time-Bound Views

All metrics and analysis are tied to below time windows.

- Daily summaries
- Today vs all-time metrics
- Date-wise analysis

---

## AI Integration Design

### Observational AI Usage

AI is used only to analyze logged and summarized data.

AI is responsible for:

- Summarizing observed activity
- Describing patterns in failures and success
- Producing human-readable explanations

AI does not:

- Modify stored data
- Trigger system actions
- Enforce rules

---

### Bounded Prompt Construction

AI prompts are built using:

- Summarized daily statistics
- Logged failure distributions
- Explicit instructions and constraints

Only summarized data is provided to the AI.

---

### Structured Output

AI responses follow a fixed structure containing:

- Executive summary
- User behavior analysis
- System behavior analysis
- Misconfiguration signals
- Confidence and scope notes

This structure allows predictable parsing and display.

---

## Execution Model

### Synchronous Analysis

AI analysis runs synchronously as part of the request flow.

When analysis is triggered:

- The request remains open
- AI inference runs to completion
- The result is returned and stored

The UI displays loading feedback during execution.

---

### Known Trade-offs

This model results in:

- Long-running HTTP requests
- Blocking behavior during AI analysis
- Sensitivity to network tunnels or intermediaries

These behaviors are known and documented.

---

## Failure Handling Strategy

### Graceful Degradation

If AI analysis fails:

- A fallback analysis report is generated
- Failure details are included in the report
- The UI receives a valid response

The dashboard remains functional even when AI fails.

---

### Idempotent Analysis Rules

For past dates:

- Analysis is generated once
- Stored results are reused

For the current date:

- Analysis will be regenerated
- Previous results will be replaced

---

## Restart and Recovery

The system supports safe restarts:

- Logged data remains intact
- Partial analysis attempts are not hidden
- Re-running analysis restores expected state

No manual intervention is required after restarts.

---

## Forward-Looking Design

The current design allows future additions such as:

- Asynchronous AI execution
- Background job processing
- Status tracking for analysis
- Polling-based UI updates

These changes can be added without modifying existing data models.

---

## Summary

The system design is centered around:

- Logging all meaningful activity
- Separating logging, summarization, and analysis
- Running AI as an isolated analysis step
- Providing predictable behavior during failures

This results in a system that is easy to inspect, debug, and extend.
