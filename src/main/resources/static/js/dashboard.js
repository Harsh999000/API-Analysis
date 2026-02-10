// ---------- Dashboard JS ----------

// Global chart instance (to avoid duplicates)
let dailyTrendChart = null;

// Track user-selected date explicitly
let userSelectedDate = null;

// Analysis UI state
let analysisState = {
    loading: false,
    status: null, // "OK" | "NO_DATA" | "ERROR"
    message: null
};

// Load summary metrics & trends on page load
loadDashboardSummary();
loadDailyTrends();

// Attach date-wise analysis handler once DOM is ready
document.addEventListener("DOMContentLoaded", () => {
    const analyzeBtn = document.getElementById("runAnalysisBtn");
    const dateInput = document.getElementById("analysisDate");

    if (dateInput) {
        // Compute IST "today" explicitly (YYYY-MM-DD)
        const istToday = new Intl.DateTimeFormat("en-CA", {
            timeZone: "Asia/Kolkata",
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        }).format(new Date());

        // Prevent future dates
        dateInput.max = istToday;

        // Track only user intent
        dateInput.addEventListener("change", () => {
            userSelectedDate = dateInput.value;
        });
    }

    if (analyzeBtn && dateInput) {
        analyzeBtn.addEventListener("click", () => {
            const istToday = new Intl.DateTimeFormat("en-CA", {
                timeZone: "Asia/Kolkata",
                year: "numeric",
                month: "2-digit",
                day: "2-digit"
            }).format(new Date());

            const dayToAnalyze = userSelectedDate || istToday;

            // ------------------------------------------------
            // OBSERVABILITY: log Analyze button click
            // ------------------------------------------------
            fetch("/api/track-visit", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    page: "/analyze"
                })
            }).catch(() => {
                // Fire-and-forget: must never block analysis
            });

            runDateWiseAnalysis(dayToAnalyze);
        });
    }
});

// ================================
// API CALLS
// ================================

function loadDashboardSummary() {
    fetch("/api/dashboard/summary")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load dashboard summary");
            }
            return response.json();
        })
        .then(data => {
            renderSummaryMetrics(data);
        })
        .catch(error => {
            console.error("Dashboard summary error:", error);
        });
}

function runDateWiseAnalysis(day) {

    // Prevent duplicate / rapid clicks
    if (analysisState.loading) return;

    // ---- Reset state + UI immediately ----
    analysisState = {
        loading: true,
        status: null,
        message: null
    };

    resetAnalysisUI();
    showLoadingState();
    disableAnalyzeButton();

    fetch(`/internal/analysis/run?day=${day}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to run date-wise analysis");
            }
            return response.json();
        })
        .then(data => {
            analysisState.loading = false;

            // Defensive NO DATA handling
            if (!data || !data.executiveSummary) {
                analysisState.status = "NO_DATA";
                analysisState.message = "No data available for selected date.";
                renderNoDataMessage();
            } else {
                analysisState.status = "OK";
                renderDateWiseAnalysis(data);
            }

            enableAnalyzeButton();
        })
        .catch(error => {
            console.error("Date-wise analysis error:", error);

            analysisState.loading = false;
            analysisState.status = "ERROR";
            analysisState.message = "Analysis unavailable. Please try again later.";

            renderAnalysisError(analysisState.message);
            enableAnalyzeButton();
        });
}

function loadDailyTrends() {
    fetch("/api/dashboard/daily-trends")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load daily trends");
            }
            return response.json();
        })
        .then(data => {
            renderDailyTrendChart(data);
        })
        .catch(error => {
            console.error("Daily trends error:", error);
        });
}

// ================================
// RENDERING
// ================================

function renderSummaryMetrics(summary) {

    // ---- Overall ----
    setMetricValue("Total Users", summary.totalUsers);
    setMetricValue("Total Leads", summary.totalLeads);
    setMetricValue("Submission Attempts", summary.submissionAttempts);
    setMetricValue("Success Count", summary.successCount);
    setMetricValue("Success Rate", formatPercent(summary.successRate));

    // ---- Today ----
    setMetricValue("Total Leads — Today", summary.totalLeadsToday);
    setMetricValue("Submission Attempts — Today", summary.submissionAttemptsToday);
    setMetricValue("Success Count — Today", summary.successCountToday);
    setMetricValue(
        "Success Rate — Today",
        formatPercent(summary.successRateToday)
    );
}

function renderDateWiseAnalysis(data) {

    const summaryEl = document.getElementById("analysisSummary");
    const failuresEl = document.getElementById("analysisFailures");
    const sourcesEl = document.getElementById("analysisSources");

    if (!summaryEl || !failuresEl || !sourcesEl) return;

    summaryEl.innerHTML = `
        <p><strong>Date:</strong> ${data.day}</p>

        <div style="margin-top: 12px;">
            <strong>Activity Summary</strong>
        </div>
        <p>${data.executiveSummary}</p>

        <p style="margin-top: 10px; font-size: 0.9em; color: #666;">
            <em>${data.analysisConfidence}</em>
        </p>
    `;

    failuresEl.innerHTML = `
        <p>${data.userBehaviorAnalysis}</p>
        <p>${data.misconfigurationSignals}</p>
    `;

    sourcesEl.innerHTML = `
        <p>${data.systemBehaviorAnalysis}</p>
    `;
}

function renderDailyTrendChart(data) {
    const canvas = document.getElementById("dailyTrendChart");
    if (!canvas) return;

    const labels = data.map(row => row.day);
    const successData = data.map(row => row.successCount);
    const failureData = data.map(row => row.failureCount);

    const maxValue = Math.max(
        ...successData,
        ...failureData,
        1
    );

    if (dailyTrendChart) {
        dailyTrendChart.destroy();
    }

    dailyTrendChart = new Chart(canvas, {
        type: "bar",
        data: {
            labels,
            datasets: [
                {
                    label: "Successful Submissions",
                    data: successData
                },
                {
                    label: "Failed Submissions",
                    data: failureData
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: Math.ceil(maxValue * 1.2),
                    ticks: {
                        precision: 0
                    }
                }
            }
        }
    });
}

// ================================
// UI HELPERS
// ================================

function resetAnalysisUI() {
    const summaryEl = document.getElementById("analysisSummary");
    const failuresEl = document.getElementById("analysisFailures");
    const sourcesEl = document.getElementById("analysisSources");

    if (summaryEl) summaryEl.innerHTML = "<p>—</p>";
    if (failuresEl) failuresEl.innerHTML = "<p>—</p>";
    if (sourcesEl) sourcesEl.innerHTML = "<p>—</p>";
}

function showLoadingState() {
    const summaryEl = document.getElementById("analysisSummary");
    if (summaryEl) {
        summaryEl.innerHTML = `
            <div class="analysis-loading">
                <div class="spinner"></div>
                <p><strong>Analyzing…</strong></p>
                <p class="analysis-hint">
                    This can take up to 1 minute. Please wait.
                </p>
            </div>
        `;
    }
}

function disableAnalyzeButton() {
    const btn = document.getElementById("runAnalysisBtn");
    if (btn) {
        btn.disabled = true;
        btn.textContent = "Analyzing…";
    }
}

function enableAnalyzeButton() {
    const btn = document.getElementById("runAnalysisBtn");
    if (btn) {
        btn.disabled = false;
        btn.textContent = "Analyze";
    }
}

// ================================
// METRIC HELPERS
// ================================

function setMetricValue(titleText, value) {
    const cards = document.querySelectorAll(".metric-card");

    cards.forEach(card => {
        const title = card.querySelector("h3");
        if (title && title.textContent.trim() === titleText) {
            const valueEl = card.querySelector(".metric-value");
            if (valueEl) {
                valueEl.textContent = value;
            }
        }
    });
}

function formatPercent(value) {
    if (value === null || value === undefined) {
        return "—";
    }
    return value.toFixed(2) + "%";
}
