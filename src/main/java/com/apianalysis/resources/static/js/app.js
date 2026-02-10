document.addEventListener("DOMContentLoaded", function () {

    const API_BASE_URL = "";

    /**
     * -----------------------------------------
     * PAGE VISIT TRACKING (SILENT)
     * -----------------------------------------
     */
    try {
        fetch(`${API_BASE_URL}/api/track-visit`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                page: window.location.pathname
            })
        }).catch(() => {
            // Intentionally ignored: visit tracking must never affect UX
        });
    } catch (e) {
        // Safety net: never let tracking break the page
    }

    /**
     * -----------------------------------------
     * LEAD SUBMISSION LOGIC
     * -----------------------------------------
     */
    const form = document.getElementById("leadForm");
    const responseBox = document.getElementById("responseBox");
    const submitButton = form?.querySelector("button[type='submit']");

    let autoClearTimer = null;

    // This page might not always have the form (future reuse)
    if (!form || !responseBox || !submitButton) {
        return;
    }

    function showResponse(message, type) {
        responseBox.textContent = message;
        responseBox.classList.remove("hidden");

        if (type === "success") {
            responseBox.style.backgroundColor = "#e6f4ea";
            responseBox.style.color = "#1e4620";
        } else if (type === "error") {
            responseBox.style.backgroundColor = "#fdecea";
            responseBox.style.color = "#611a15";
        } else {
            responseBox.style.backgroundColor = "#eef2ff";
            responseBox.style.color = "#1e3a8a";
        }
    }

    function clearResponse() {
        responseBox.classList.add("hidden");
        responseBox.textContent = "";
    }

    function scheduleAutoClear() {
        if (autoClearTimer) {
            clearTimeout(autoClearTimer);
        }
        autoClearTimer = setTimeout(() => {
            clearResponse();
        }, 3000);
    }

    function setLoadingState(isLoading) {
        submitButton.disabled = isLoading;
        submitButton.textContent = isLoading ? "Submitting..." : "Submit Lead";
    }

    function buildHeaders(userEmail, userPassword) {
        return {
            "Content-Type": "application/json",
            "X-User-Email": userEmail,
            "X-User-Password": userPassword
        };
    }

    function buildRequestBody() {
        return {
            name: document.getElementById("leadName").value.trim(),
            email: document.getElementById("leadEmail").value.trim(),
            source: document.getElementById("source").value.trim(),
            finalPage: document.getElementById("finalPage").value.trim()
        };
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        clearResponse();
        setLoadingState(true);
        showResponse("Submitting lead…", "info");

        const userEmail = document.getElementById("userEmail").value.trim();
        const userPassword = document.getElementById("userPassword").value;

        try {
            const response = await fetch(`${API_BASE_URL}/api/leads`, {
                method: "POST",
                headers: buildHeaders(userEmail, userPassword),
                body: JSON.stringify(buildRequestBody())
            });

            let data = null;
            try {
                data = await response.json();
            } catch {}

            if (!response.ok) {
                let message = "Something went wrong. Please try again.";

                if (data && data.message) {
                    message = data.message;
                }

                if (data && data.error === "RATE_LIMIT_EXCEEDED") {
                    message += " Try again in 60 seconds.";
                }

                showResponse(`❌ ${message}`, "error");
                return;
            }

            showResponse(`✅ ${data.message}`, "success");
            form.reset();
            scheduleAutoClear();

        } catch (err) {
            showResponse("❌ Network error. Please check your connection.", "error");
        } finally {
            setLoadingState(false);
        }
    });
});
