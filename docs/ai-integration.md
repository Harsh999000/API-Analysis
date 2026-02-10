# AI Integration

## Overview

The system integrates a locally hosted Large Language Model (LLM) to generate human-readable analysis from logged and aggregated system data.

The AI component is used only for **analysis and explanation**.  
It does not participate in core system execution.

AI output is stored as analysis data and does not modify any operational records.

---

## Role of AI in the System

The AI subsystem is used to:

- Analyze aggregated submission data
- Summarize system and user behavior
- Highlight observable patterns and anomalies

The AI subsystem does **not**:

- Make decisions
- Enforce rules
- Modify logs or metrics
- Trigger system actions

If the AI subsystem is unavailable, the rest of the system continues to operate normally.

---

## Execution Model

### Synchronous Execution (Current)

AI analysis is currently executed synchronously.

Flow:

1. An API request triggers analysis for a specific date
2. The application server prepares analysis data
3. The AI server is called over HTTP
4. The request blocks until AI inference completes
5. The result is stored and returned to the caller

This model was chosen for simplicity and traceability.

---

### Execution Time Characteristics

AI inference time depends on:

- Model size
- Prompt size
- Current model load state
- Available CPU resources

Typical execution time ranges between **20 and 60 seconds**.

---

## AI Service Responsibilities

The AI integration layer is responsible for:

- Preparing structured, bounded prompts
- Sending requests to the AI server
- Enforcing non-streaming responses
- Validating response structure
- Extracting analysis fields
- Creating analysis records

The AI service has no access to databases or internal APIs.

---

## Prompt Construction

### Data Sources Used

Prompts are constructed using:

- Aggregated daily metrics
- Submission success and failure counts
- Failure category distributions
- System behavior summaries

Only aggregated and non-sensitive data is included.

---

### Scope and Constraints

Prompts explicitly restrict the AI to:

- Analyze only provided data
- Avoid assumptions beyond the input
- Avoid operational recommendations
- Avoid action-oriented instructions

The AI is instructed to describe observations, not propose system changes.

---

## Output Format

The AI is instructed to return **strict JSON only**.

Expected fields include:

- Executive summary
- User behavior analysis
- System behavior analysis
- Misconfiguration signals
- Analysis confidence
- Scope notes

This ensures predictable parsing and safe rendering in the UI.

---

## Storage of AI Output

AI-generated analysis is stored immediately after successful generation.

Stored analysis is used to:

- Render dashboard views
- Avoid repeated inference for the same date
- Preserve historical explanations

For past dates, stored analysis is treated as immutable.

---

## Failure Handling

### Possible Failure Scenarios

AI execution may fail due to:

- Long inference time
- Network interruptions
- AI server unavailability
- Invalid or malformed output

Failures do not break dashboard functionality.

---

### Fallback Behavior

If AI analysis fails:

- A placeholder analysis record is returned
- Failure context is included in the response
- Confidence is explicitly marked as low

The UI remains functional and displays a clear failure message.

---

## Networking Characteristics

The AI server runs as a separate process from the application server.

This introduces:

- Network latency between services
- Dependency on long-lived HTTP connections
- Sensitivity to tunneling or proxy behavior

Long-running requests may be interrupted by network intermediaries.

---

## Known Limitations

- AI analysis is synchronous
- Requests block until AI completes
- Long-running connections may stall under tunneling
- No intermediate progress updates are available

These limitations are known and documented.

---

## Planned Improvements

Future improvements include:

- Asynchronous AI job execution
- Fire-and-forget analysis triggering
- Persistent job status tracking
- UI-based polling for completion
- Avoidance of long-held HTTP connections

These changes can be implemented without altering existing data models.

---

## Security Boundaries

- AI receives only aggregated, non-sensitive data
- No personal identifiers are included
- AI output is not trusted for decision-making

The AI subsystem operates within strict data boundaries.

---

## Summary

The AI integration provides analytical insight without influencing system behavior.

It operates as a read-only analysis component that explains observed data while preserving system correctness and stability.
