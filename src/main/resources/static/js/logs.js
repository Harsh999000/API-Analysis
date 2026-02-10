/* ---------- Logs Page JS ---------- */

/* ---------- CSV Export Utility ---------- */

function exportToCSV(filename, headers, rows) {
    if (!rows || rows.length === 0) {
        alert("No data available to export.");
        return;
    }

    const csvContent = [
        headers.join(","),
        ...rows.map(row =>
            headers.map(header => {
                const value = row[header] ?? "";
                return `"${String(value).replace(/"/g, '""')}"`;
            }).join(",")
        )
    ].join("\n");

    const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);

    const link = document.createElement("a");
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    URL.revokeObjectURL(url);
}

/* ---------- Table Render Functions ---------- */

function renderUsers(users) {
    const tbody = document.querySelector("#users-table tbody");
    if (!tbody) return;

    tbody.innerHTML = "";
    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${user.email}</td>
            <td>${user.createdAt}</td>
        `;
        tbody.appendChild(row);
    });
}

function renderLeads(leads) {
    const tbody = document.querySelector("#leads-table tbody");
    if (!tbody) return;

    tbody.innerHTML = "";
    leads.forEach(lead => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${lead.leadName}</td>
            <td>${lead.leadEmail}</td>
            <td>${lead.source}</td>
            <td>${lead.finalPage || "-"}</td>
            <td>${lead.createdAt}</td>
        `;
        tbody.appendChild(row);
    });
}

function renderLeadSubmissions(submissions) {
    const tbody = document.querySelector("#submissions-table tbody");
    if (!tbody) return;

    tbody.innerHTML = "";
    submissions.forEach(entry => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${entry.userEmail}</td>
            <td>${entry.leadName}</td>
            <td>${entry.leadEmail}</td>
            <td>${entry.source}</td>
            <td>${entry.finalPage || "-"}</td>
            <td>${entry.outcome}</td>
            <td>${entry.failureReason || "-"}</td>
            <td>${entry.createdAt}</td>
        `;
        tbody.appendChild(row);
    });
}

/* ---------- Page Initialization ---------- */

document.addEventListener("DOMContentLoaded", () => {

    // ---- Initial table load (last 20 only) ----

    fetch("/api/logs/users?limit=20")
        .then(res => res.json())
        .then(renderUsers);

    fetch("/api/logs/leads?limit=20")
        .then(res => res.json())
        .then(renderLeads);

    fetch("/api/logs/submissions?limit=20")
        .then(res => res.json())
        .then(renderLeadSubmissions);

    // ---- Export buttons (fetch ALL data) ----

    const exportButtons = document.querySelectorAll(".export-btn");

    exportButtons.forEach((button, index) => {
        button.addEventListener("click", () => {

            switch (index) {

                case 0:
                    fetch("/api/logs/users/export")
                        .then(res => res.json())
                        .then(data =>
                            exportToCSV(
                                "users.csv",
                                ["email", "createdAt"],
                                data
                            )
                        );
                    break;

                case 1:
                    fetch("/api/logs/leads/export")
                        .then(res => res.json())
                        .then(data =>
                            exportToCSV(
                                "leads.csv",
                                ["leadName", "leadEmail", "source", "finalPage", "createdAt"],
                                data
                            )
                        );
                    break;

                case 2:
                    fetch("/api/logs/submissions/export")
                        .then(res => res.json())
                        .then(data =>
                            exportToCSV(
                                "lead_submissions.csv",
                                [
                                    "userEmail",
                                    "leadName",
                                    "leadEmail",
                                    "source",
                                    "finalPage",
                                    "outcome",
                                    "failureReason",
                                    "createdAt"
                                ],
                                data
                            )
                        );
                    break;
            }
        });
    });
});
