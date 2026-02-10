# System Information

## Purpose

This document describes the hardware environment on which the system is currently running.

## Application Server / Database / Tunneling Host

This machine runs:

- Spring Boot application server
- Database
- External tunneling service

### Hardware Configuration

- **Processor**: AMD A6-6310 APU  
- **CPU Cores**: 4  
- **Logical Threads**: 4  
- **Memory (RAM)**: Limited system memory  
- **GPU**: AMD Radeon R4/R5 Graphics (integrated)

### Notes

- CPU resources are shared between application logic, database operations, and tunneling
- Long-running synchronous requests can temporarily block request handling
- Under load, dashboard and API response times may increase

---

## AI Inference Server

This machine is dedicated to running the AI model and handling inference requests.

### Hardware Configuration

- **Processor**: AMD Ryzen 9 4900HS  
- **CPU Cores**: 8  
- **Logical Threads**: 16  
- **Memory (RAM)**: 16 GB  
- **GPU**:
  - NVIDIA GeForce RTX 2060 (Max-Q)
  - AMD Radeon Graphics (integrated)

### Notes

- AI inference is CPU- and memory-intensive
- GPU availability improves inference performance but does not eliminate latency
- Concurrent inference requests may increase response time

---

## Operational Impact

Given the above hardware characteristics:

- AI analysis requests may take **upto 60 seconds**
- Synchronous AI execution may block API threads temporarily
- Tunneling layers may stall under long-lived connections
- Restarting services may be required to clear stale state

These behaviors are hardware- and infrastructure-related, not application defects.

---

## Summary

The listed hardware constraints may result in:

- Longer AI analysis execution times
- Slower API responses under load
- Increased latency for synchronous requests

These characteristics are expected given the available resources.

---
