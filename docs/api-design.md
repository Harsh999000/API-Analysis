# API Design

## Overview

The API layer exposes all system behavior through explicit HTTP endpoints.

It supports:

- Lead submission and validation
- Logging of submission attempts
- Dashboard data retrieval
- AI-based analysis execution

Each API endpoint has a clearly defined purpose and produces observable effects.

---

## Design Principles

### Explicit Responsibilities

Each API endpoint performs a single task.

Endpoints do not:

- Perform hidden side effects
- Modify unrelated data
- Trigger background processing implicitly

Any logging, summarization, or analysis is triggered directly by the endpoint handling the request.

---

### Deterministic Behavior

For the same input and system state, an endpoint returns the same result.

This applies to:

- Validation outcomes
- Summarized metrics
- AI analysis results for past dates

This makes API behavior predictable and debuggable.

---

### Separation of API Groups

APIs are grouped by function:

- Submission APIs
- User APIs
- Dashboard APIs
- Log inspection APIs
- Internal analysis APIs

Internal APIs are not exposed for public use.

---

## Public APIs

### Lead Submission API

Handles lead submission attempts from clients.

**Behavior**
- Validates incoming request data
- Applies rate limits
- Logs every submission attempt
- Creates a lead record on success

**Key Properties**
- Every request is logged
- Both success and failure are recorded
- Failed submissions do not create leads

No submission attempt is ignored or dropped silently.

---

### User APIs

Handles user-related operations.

**Behavior**
- User creation
- User lookup
- Identity validation

User APIs do not return submission data, logs, or analytics.

---

## Dashboard APIs

### Dashboard Summary API

Provides summarized metrics for the dashboard.

**Behavior**
- Returns total counts
- Computes success rates
- Separates today’s data from all-time data

**Characteristics**
- Read-only
- Computed from logged submission data
- Does not invoke AI

---

### Daily Trends API

Provides date-wise submission statistics.

**Behavior**
- Returns submission counts per day
- Separates successful and failed attempts

**Characteristics**
- Used for chart rendering
- No mutation or analysis
- Lightweight summarization only

---

## Log Inspection APIs

### Submission Logs API

Exposes raw submission attempt logs.

**Behavior**
- Returns individual submission records
- Supports pagination
- Supports filtering by date or status

**Characteristics**
- No summarization
- No interpretation
- Represents raw logged data only

These APIs expose recorded facts, not conclusions.

---

## Internal Analysis APIs

### Daily AI Analysis API

Triggers AI analysis for a specific date.

**Behavior**
- Validates requested date
- Summarizes logged data for that date
- Builds a structured AI prompt
- Calls the AI service
- Stores the generated analysis

**Characteristics**
- Explicitly invoked
- Runs synchronously
- May take significant time to complete

The response is returned only after analysis completes.

---

### Analysis Data Preparation APIs

Internal-only endpoints used to prepare AI input.

**Behavior**
- Summarize daily statistics
- Normalize counts and rates
- Remove direct identifiers

These endpoints are not accessible from the UI or external clients.

---

## Error Handling

### Explicit Error Responses

All API errors include:

- HTTP status code
- Clear error type
- Human-readable message

No API fails silently.

---

### Validation Errors

Validation failures:

- Do not create leads
- Do not trigger AI analysis
- Are logged as failed submission attempts

Validation errors are observable through logs and metrics.

---

## Authentication and Authorization

The API enforces access boundaries:

- Submission APIs are protected by user credentials
- Dashboard APIs are read-only
- Internal analysis APIs are not exposed externally

Authorization is enforced at the API layer.

---

## Date and Time Handling

Date-based APIs:

- Accept explicit date values
- Treat dates as logical business days
- Do not infer dates implicitly

All date-based summarization and analysis is deterministic.

---

## Current Limitations

- AI analysis APIs execute synchronously
- Long-running requests may block until completion
- Network tunnels may terminate long-lived requests

These limitations are known and documented.

---

## Future Improvements

Planned API extensions include:

- Asynchronous AI execution endpoints
- Analysis job status APIs
- Result polling APIs
- Non-blocking request handling

These changes can be added without breaking existing APIs.

---

## Summary

The API layer provides:

- Explicit execution paths
- Full visibility into system behavior
- Clear separation between logging, summarization, and analysis

Every endpoint exists to either log data, expose logged data, or analyze logged data.
