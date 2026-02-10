package com.apianalysis.analysis.service;

import org.springframework.stereotype.Service;

import com.apianalysis.analysis.dto.DailyAnalysisSnapshot;

import java.util.Comparator;
import java.util.Map;

/**
 * Builds a STRICT, JSON-only prompt for AI analysis.
 *
 * Output contract:
 * - AI must return valid JSON
 * - No prose outside JSON
 * - No predictions
 * - No recommendations
 * - No repeated content across fields
 */
@Service
public class AnalysisPromptBuilder {

    public String buildPrompt(DailyAnalysisSnapshot snapshot) {

        StringBuilder prompt = new StringBuilder();

        // ---- System framing ----
        prompt.append("""
            You are an AI system performing retrospective analysis of backend lead submission behavior.

            STRICT RULES:
            - You MUST return valid JSON only.
            - Do NOT include explanations outside JSON.
            - Do NOT predict future behavior.
            - Do NOT recommend actions.
            - Do NOT repeat the same information across fields.
            - Use cautious language ("likely", "suggests", "consistent with").
            - Base analysis ONLY on the facts provided.
            - Analysis scope is ONE calendar day only (IST).

            JSON OUTPUT SCHEMA (MUST FOLLOW EXACTLY):
            {
              "executiveSummary": "High-level overview of the day",
              "userBehaviorAnalysis": "Observations about user activity patterns only",
              "systemBehaviorAnalysis": "Observations about system behavior and constraints only",
              "misconfigurationSignals": "Signals that suggest possible configuration or integration issues",
              "analysisConfidence": "Confidence level and reasoning",
              "analysisScopeNote": "Explicit scope limitations"
            }

            ---
            """);

        // ---- Day scope ----
        prompt.append("ANALYSIS DAY (IST): ")
              .append(snapshot.getDay())
              .append("\n\n");

        // ---- Volume summary ----
        prompt.append("SYSTEM VOLUME SUMMARY:\n");
        prompt.append("- Total attempts: ").append(snapshot.getTotalAttempts()).append("\n");
        prompt.append("- Successful submissions: ").append(snapshot.getSuccessCount()).append("\n");
        prompt.append("- Failed submissions: ").append(snapshot.getFailureCount()).append("\n");
        prompt.append("- Success ratio: ")
              .append(String.format("%.2f", snapshot.getSuccessRatio() * 100))
              .append("%\n\n");

        // ---- Failure shape ----
        prompt.append("FAILURE BREAKDOWN BY REASON:\n");
        appendKeyValueBlock(prompt, snapshot.getFailuresByReason());
        prompt.append("\n");

        // ---- User behavior ----
        prompt.append("HIGH ACTIVITY USERS:\n");
        if (snapshot.getHighActivityUsers().isEmpty()) {
            prompt.append("- None\n");
        } else {
            snapshot.getHighActivityUsers().forEach(user ->
                    prompt.append("- ")
                          .append(user.getUserEmail())
                          .append(" | Attempts: ")
                          .append(user.getAttempts())
                          .append(" | Failures: ")
                          .append(user.getFailures())
                          .append(" | Failure ratio: ")
                          .append(String.format("%.2f", user.getFailureRatio() * 100))
                          .append("%\n")
            );
        }
        prompt.append("\n");

        // ---- Source & page signals ----
        prompt.append("ATTEMPTS BY SOURCE:\n");
        appendKeyValueBlock(prompt, snapshot.getAttemptsBySource());
        prompt.append("\n");

        prompt.append("DUPLICATE FAILURES BY SOURCE:\n");
        appendKeyValueBlock(prompt, snapshot.getDuplicatesBySource());
        prompt.append("\n");

        prompt.append("RATE LIMIT FAILURES BY FINAL PAGE:\n");
        appendKeyValueBlock(prompt, snapshot.getRateLimitsByFinalPage());
        prompt.append("\n");

        // ---- Temporal shape ----
        prompt.append("TEMPORAL BEHAVIOR:\n");
        prompt.append("- Max attempts per minute: ")
              .append(snapshot.getMaxAttemptsPerMinute())
              .append("\n");
        prompt.append("- Burst minutes: ")
              .append(snapshot.getBurstMinuteCount())
              .append("\n\n");

        // ---- Final instruction ----
        prompt.append("""
            ---
            IMPORTANT:
            - Populate each JSON field with UNIQUE information.
            - Do not restate statistics across fields.
            - Keep responses concise and factual.
            - Return JSON ONLY.
            """);

        return prompt.toString();
    }

    private void appendKeyValueBlock(StringBuilder prompt, Map<String, Long> map) {
        if (map.isEmpty()) {
            prompt.append("- None\n");
            return;
        }

        map.entrySet()
           .stream()
           .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
           .forEach(entry ->
               prompt.append("- ")
                     .append(entry.getKey())
                     .append(": ")
                     .append(entry.getValue())
                     .append("\n")
           );
    }
}
