package com.apianalysis.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apianalysis.dto.TrackVisitRequest;
import com.apianalysis.service.PageVisitLogService;

@RestController
@RequestMapping("/api")
public class VisitTrackingController {

    private final PageVisitLogService pageVisitLogService;

    public VisitTrackingController(PageVisitLogService pageVisitLogService) {
        this.pageVisitLogService = pageVisitLogService;
    }

    @PostMapping("/track-visit")
    public ResponseEntity<Void> trackVisit(
            @RequestBody TrackVisitRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = extractClientIp(httpRequest);
        String page = request.getPage();

        // Persist visit
        pageVisitLogService.logVisit(ipAddress, page);

        // Custom debug headers (Cloudflare-safe)
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Visit-Tracked", "true");
        headers.add("X-Visit-Page", page != null ? page : "null");
        headers.add("X-Visit-IP", ipAddress);
        headers.add("X-Visit-Proxy", detectProxy(httpRequest));

        return ResponseEntity.ok().headers(headers).build();
    }

    private String extractClientIp(HttpServletRequest request) {
        // 1. Cloudflare Tunnel / CDN
        String cfIp = request.getHeader("CF-Connecting-IP");
        if (cfIp != null && !cfIp.isBlank()) {
            return cfIp.trim();
        }

        // 2. Standard reverse proxy
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        // 3. Fallback
        return request.getRemoteAddr();
    }

    private String detectProxy(HttpServletRequest request) {
        if (request.getHeader("CF-Connecting-IP") != null) {
            return "cloudflare";
        }
        if (request.getHeader("X-Forwarded-For") != null) {
            return "reverse-proxy";
        }
        return "direct";
    }
}
