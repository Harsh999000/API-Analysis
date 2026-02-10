package com.leadsubmission.observability.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadsubmission.observability.analysis.dto.DailyAnalysisReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * AI Analysis Service backed by Ollama.
 *
 * HARD GUARANTEES:
 * - Strict HTTP timeouts
 * - No silent hangs
 * - No fake "error reports"
 * - Fail fast, fail honestly
 */
@Service
public class AiAnalysisService {

    private static final Logger log =
            LoggerFactory.getLogger(AiAnalysisService.class);

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");

    @Value("${ai.ollama.url}")
    private String ollamaUrl;

    @Value("${ai.ollama.model:mistral}")
    private String modelName;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AiAnalysisService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))   // hard limit
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public DailyAnalysisReport analyze(LocalDate day, String prompt) {

        long start = System.currentTimeMillis();
        log.info("AI analysis started for day={}", day);

        try {
            // ---- Build request payload ----
            Map<String, Object> payload = Map.of(
                    "model", modelName,
                    "prompt", prompt,
                    "stream", false
            );

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60)) // max inference wait
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // ---- Call Ollama ----
            log.info("Calling Ollama at {}", ollamaUrl);

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info(
                    "Ollama responded with status={} in {} ms",
                    response.statusCode(),
                    System.currentTimeMillis() - start
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama HTTP " + response.statusCode()
                );
            }

            // ---- Parse Ollama wrapper JSON ----
            JsonNode root = objectMapper.readTree(response.body());
            String aiRaw = root.path("response").asText();

            if (aiRaw == null || aiRaw.isBlank()) {
                throw new RuntimeException("AI returned empty response");
            }

            // ---- Parse AI JSON payload ----
            JsonNode aiJson = objectMapper.readTree(aiRaw);

            String executiveSummary =
                    aiJson.path("executiveSummary").asText();

            if (executiveSummary == null || executiveSummary.isBlank()) {
                throw new RuntimeException("Missing executiveSummary in AI output");
            }

            // ---- Extract remaining fields ----
            String userBehaviorAnalysis =
                    aiJson.path("userBehaviorAnalysis").asText("");

            String systemBehaviorAnalysis =
                    aiJson.path("systemBehaviorAnalysis").asText("");

            String misconfigurationSignals =
                    aiJson.path("misconfigurationSignals").asText("");

            String analysisConfidence =
                    aiJson.path("analysisConfidence").asText("");

            String analysisScopeNote =
                    aiJson.path("analysisScopeNote").asText(
                            "This analysis is limited to a single day and aggregated system data."
                    );

            log.info(
                    "AI analysis completed successfully for day={} ({} ms)",
                    day,
                    System.currentTimeMillis() - start
            );

            return new DailyAnalysisReport(
                    day,
                    LocalDateTime.now(IST_ZONE),
                    executiveSummary,
                    userBehaviorAnalysis,
                    systemBehaviorAnalysis,
                    misconfigurationSignals,
                    analysisConfidence,
                    analysisScopeNote
            );

        } catch (Exception e) {
            log.error(
                    "AI analysis FAILED for day={} after {} ms",
                    day,
                    System.currentTimeMillis() - start,
                    e
            );
            throw new RuntimeException("AI analysis failed", e);
        }
    }
}
