# Database Design

## Overview

The database supports an observability-focused system where:

- Every lead submission attempt is logged
- Success and failure are stored explicitly
- Historical data is not modified
- Aggregated data is derived from logged events

The schema is designed to make system behavior easy to inspect and explain.

---

## Design Principles

### Append-Only Logging

Core event tables follow an append-only model.

Once a record is written:

- It is not updated
- It is not deleted
- Corrections are recorded as new rows

This preserves a complete and accurate history of system behavior.

---

### Events Before Aggregates

Raw submission events are logged first.  
Aggregated views are computed from these logs.

This allows:

- Recomputing metrics if logic changes
- Verifying dashboard numbers against raw data
- Debugging issues using full event context

Aggregates never replace raw logs.

---

### Explicit Time Tracking

Every important table includes:

- A creation timestamp
- A logical date value where applicable

No record exists without a clear time reference.

---

## Core Tables

### User

Represents a registered user of the system.

**Purpose**
- Identity association for submissions
- Per-user rate limiting
- Attribution for logs and metrics

**Key Characteristics**
- Created once
- Not modified after creation
- Referenced by all submission-related records

---

### Lead

Represents a successfully created lead.

**Purpose**
- Track successful submissions
- Support business-level reporting

**Key Characteristics**
- Created only after a successful submission
- Does not represent failed attempts
- Not modified after creation

---

### Lead Submission Log

Represents **every** lead submission attempt.

This is the primary source of truth in the system.

**Purpose**
- Record system behavior
- Capture failures explicitly
- Provide input for metrics and AI analysis

**Key Characteristics**
- One row per submission attempt
- Includes success or failure outcome
- Stores failure reason when applicable
- Append-only
- Not modified after creation

All dashboards and analysis are derived from this table.

---

### User Rate Limit

Tracks submission limits per user.

**Purpose**
- Prevent abuse
- Enforce fair usage
- Protect system resources

**Key Characteristics**
- Updated deterministically
- Checked before processing submissions
- Changes are traceable through logs

---

## Analysis and Reporting Tables

### Daily Analysis Snapshot

Stores aggregated submission statistics for a specific date.

**Purpose**
- Support fast dashboard queries
- Provide a stable input for AI analysis

**Key Characteristics**
- One snapshot per date
- Derived from submission logs
- Not modified after the day is finalized

Snapshots summarize logs but never replace them.

---

### Daily Analysis Report

Stores AI-generated analysis for a specific date.

**Purpose**
- Provide summaries and explanations
- Highlight patterns and anomalies
- Support observability workflows

**Key Characteristics**
- Linked to a single date
- Generated from snapshot data
- Stored for reproducibility
- Treated as read-only for past dates

AI output is stored so results can be reviewed later.

---

## Relationships

### User → Lead Submission Log

- One user can have many submission attempts
- Each submission attempt belongs to one user

---

### Lead Submission Log → Lead

- One-to-zero-or-one relationship
- Only successful submissions create leads
- Failed submissions never create lead records

---

### Daily Snapshot → Submission Logs

- One snapshot summarizes many submission logs
- Relationship is logical, not enforced by foreign keys
- Raw logs always remain available

---

### Daily Report → Snapshot

- One report corresponds to one snapshot
- Ensures AI analysis matches the exact data set used

---

## Immutability Rules

The following rules are enforced by design:

- Submission logs are never modified
- Snapshots for past dates are not modified
- AI reports for past dates are not modified

If re-analysis is required, a new report must be created explicitly.

---

## Failure Representation

Failures are stored directly, not inferred.

Each failed submission includes:

- Failure category
- Error message or code
- Relevant contextual data

This enables accurate failure analysis and AI interpretation.

---

## Future Compatibility

The schema supports future additions such as:

- Asynchronous AI job tracking
- Versioned analysis reports
- Precomputed long-term trends
- Export to external analytics systems

These changes can be added without altering existing tables.

---

## Summary

The database design focuses on:

- Logging real system behavior
- Keeping historical data intact
- Making failures visible and explainable

All metrics and AI insights are grounded in logged data, ensuring every conclusion can be traced back to recorded events.
