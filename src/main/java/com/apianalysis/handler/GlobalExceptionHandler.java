package com.apianalysis.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.apianalysis.dto.ErrorResponse;
import com.apianalysis.exception.RateLimitExceededException;
import com.apianalysis.exception.UnauthorizedException;
import com.apianalysis.exception.UserAlreadyExistsException;
import com.apianalysis.exception.UserNotFoundException;
import com.apianalysis.service.LeadAuditService;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LeadAuditService leadAuditService;

    public GlobalExceptionHandler(LeadAuditService leadAuditService) {
        this.leadAuditService = leadAuditService;
    }

    /**
     * AUTHENTICATION FAILURES
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        String userEmail = request.getHeader("X-User-Email");

        leadAuditService.logAttempt(
                null,
                userEmail,
                null,
                null,
                null,
                null,
                "FAILED",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "AUTHENTICATION_FAILED",
                        ex.getMessage()
                ));
    }

    /**
     * RATE LIMIT EXCEEDED
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex,
            HttpServletRequest request
    ) {
        String userEmail = request.getHeader("X-User-Email");

        leadAuditService.logAttempt(
                null,
                userEmail,
                null,
                null,
                null,
                null,
                "FAILED",
                "RATE_LIMIT_EXCEEDED"
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse(
                        "RATE_LIMIT_EXCEEDED",
                        ex.getMessage()
                ));
    }

    /**
     * USER ALREADY EXISTS
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "USER_ALREADY_EXISTS",
                        ex.getMessage()
                ));
    }

    /**
     * DUPLICATE LEAD (DB safety net)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DUPLICATE_LEAD",
                        "Lead already submitted for this page"
                ));
    }

    /**
     * User Not Found
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "USER_NOT_FOUND",
                        ex.getMessage()
                ));
    }

}
