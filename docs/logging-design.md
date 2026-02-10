# Logging Design

## Overview

Logging records **what actually happened** in the system.

Logs are structured records of events and outcomes.  
They are written at execution time and stored for later inspection.

Logging supports:

- Debugging
- Auditing
- Metrics generation
- AI-based analysis

---

## Logging Approach

### Event-Based Logging

Logs record events and their results.

They do not:

- Interpret intent
- Assign responsibility
- Draw conclusions

All interpretation is performed later using logged data.

---

### All Submission Attempts Are Logged

For lead submission workflows:

- Successful attempts are logged
- Failed attempts are logged
- Rejected attempts are logged

Every request results in a log entry.

No attempt is ignored or dropped silently.

---

## Log Record Structure

Each log entry includes:

- Timestamp
- User identifier
- Action type
- Execution outcome
- Failure category (if applicable)
- Relevant contextual metadata

This structure is consistent across all logged events.

---

## Append-Only Logging

Logs follow an append-only model.

Once a log entry is written:

- It is not updated
- It is not deleted

This preserves a complete and accurate history of system behavior.

---

## Separation from Technical Logs

Business-event logs are stored separately from:

- Application debug output
- Framework logs
- Infrastructure or server logs

Only business-relevant events are used for dashboards and analysis.

---

## AI Compatibility

Logs are designed to be:

- Structured and machine-readable
- Easy to aggregate by date and category
- Safe to include in AI analysis inputs

Sensitive information is not included in logged fields.

---

## Error Logging

Errors are logged with:

- Explicit classification
- Clear outcome
- Relevant execution context

Internal stack traces and low-level details are not exposed through business logs.

---

## Usage in Dashboards and Analysis

Logged events are used to:

- Generate dashboard metrics
- Identify failure patterns
- Build daily analysis snapshots
- Provide factual input to AI analysis

Dashboards and AI outputs never bypass or replace logs.

---

## Summary

Logging serves as the system’s factual record.

All metrics, analysis, and explanations are derived from logged events.  
If the logs are correct, the system can be understood and debugged reliably.
