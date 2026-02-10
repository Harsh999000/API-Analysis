# API-Analysis

## Overview

**API Analysis** simulates lead generation from multiple sources and endpoints and produces **AI-based daily summaries** derived from lead submission logs.

The system is built as an **submission behaviour backend project**, focusing on:

- Tracking real submission behavior  
- Identifying failures and anomalies  
- Explaining system behavior using AI-assisted analysis  

AI summaries are generated using **Ollama with the LLaMA 3.1 model (8B parameter variant)** and are strictly **observational in nature**.

---

## Live Demo / Dashboard Access

Please use the link below to try the project or check the live dashboard:

** https://francis-lion-bar-benchmark.trycloudflare.com**
**Server currently down - change in progress - moving from dynamic url tunneling to fixed url tunnel**

## Live Demo / Dashboard Access
- For a quick check on database and AI Analysis Flow you can check pdf of flow in draw.io_files, and you can also use xml files to import the design in draw.io
- For further information on system parts you can check part specific files in docs

> **Availability Notice**  
> The server is running on my personal laptop, so availability is limited.
>
> **Expected downtime:**  
> **12:30 A.M. – 8:00 A.M.**

If you need to access the project during the expected downtime window, or if you want to collaborate on a project, feel free to reach out:

**jhaharsh101@gmail.com**

Do reach out if you want information on any part of the project.

**Happy Coding! **

---

## Key Capabilities

### Lead Submission Tracking
- Simulates lead submissions from multiple sources
- Tracks attempts, successes, and failures
- Maintains immutable historical logs

### Dashboard & Metrics
- Overall system metrics
- Day-wise submission metrics
- Success and failure rates
- Visual trend analysis

### AI-Based Daily Analysis
- Date-wise summaries generated from stored logs
- User behavior analysis
- System behavior explanation
- Misconfiguration signal detection
- Confidence and scope annotations

> AI does **not** mutate data or trigger automated actions.

---

## Known Limitations (Current Phase)

The following limitations are known and accepted at this stage:

- AI analysis calls can take **upto 60 seconds**
- The system currently uses **synchronous HTTP**
- Cloudflare combined with long-running requests can cause **silent stalls**
- Restarting services clears **stale tunnel / connection state**

---

## Future Improvements

Planned enhancements include:

- Fire-and-forget AI analysis jobs
- Logging AI execution status in the database
- Eliminating long-lived upstream connections
- Full async decoupling of AI execution

---

## Documentation Roadmap

---

## Project Status

The project is **stable, functional, and observable**.

All core flows are validated:

- Data capture  
- Logging
- Summarization  
- AI analysis  
- Visualization  

Future work focuses on **performance and stability**.

---
