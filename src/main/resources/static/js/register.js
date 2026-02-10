document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const responseBox = document.getElementById("responseBox");

    responseBox.classList.add("hidden");
    responseBox.textContent = "";
    responseBox.classList.remove("success", "error");

    if (!email || !password) {
        responseBox.textContent = "Email and password are required.";
        responseBox.classList.add("error");
        responseBox.classList.remove("hidden");
        return;
    }

    try {
        const response = await fetch("/api/users", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (!response.ok) {
            responseBox.textContent = data.message || "Failed to create user.";
            responseBox.classList.add("error");
            responseBox.classList.remove("hidden");
            return;
        }

        responseBox.textContent = "User created successfully. You can now submit leads.";
        responseBox.classList.add("success");
        responseBox.classList.remove("hidden");

        // Redirect after short delay
        setTimeout(() => {
            window.location.href = "/index.html";
        }, 1200);

    } catch (err) {
        responseBox.textContent = "Network error. Please try again.";
        responseBox.classList.add("error");
        responseBox.classList.remove("hidden");
    }
});
