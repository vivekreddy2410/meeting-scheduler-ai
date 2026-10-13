const api = "/api";

async function getJson(url, options = {}) {
    const res = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Request failed");
    }

    if (res.status === 204) return null;
    return res.json();
}

async function loadUsers() {
    const users = await getJson(`${api}/users`);
    const organizer = document.getElementById("organizer");

    organizer.innerHTML = '<option value="">Choose organizer</option>';

    document.getElementById("userList").innerHTML = users.map(u => `
        <div class="card">
            <span class="tag">PARTICIPANT</span>
            <h3>${escapeHtml(u.name)}</h3>
            <p class="muted">${escapeHtml(u.email)}</p>
            <p class="muted">Timezone: ${escapeHtml(u.timezone || "-")}</p>
        </div>
    `).join("");

    users.forEach(u => {
        organizer.innerHTML += `
            <option value="${u.id}">
                ${escapeHtml(u.name)} — ${escapeHtml(u.email)}
            </option>`;
    });
}

async function loadMeetings() {
    const meetings = await getJson(`${api}/meetings`);
    const list = document.getElementById("meetingList");

    if (!meetings.length) {
        list.innerHTML = '<p class="muted">No meetings yet.</p>';
        return;
    }

    list.innerHTML = meetings.map(m => `
        <div class="card">
            <span class="tag">${escapeHtml(m.status)}</span>
            <h3>${escapeHtml(m.title)}</h3>
            <p>${escapeHtml(m.meetingDate)} •
                ${escapeHtml(m.startTime)} - ${escapeHtml(m.endTime)}</p>
            <p class="muted">${escapeHtml(m.description || "No description")}</p>
            ${m.status !== "CANCELLED"
                ? `<button class="primary-btn" onclick="cancelMeeting(${m.id})">Cancel</button>`
                : ""}
        </div>
    `).join("");
}

// Load all saved availability records.
async function loadAvailability() {
    const list = document.getElementById("availabilityList");
    if (!list) return;

    list.innerHTML = '<p class="muted">Loading availability...</p>';

    try {
        const records = await getJson(`${api}/availability`);

        if (!records.length) {
            list.innerHTML = '<p class="muted">No availability added yet.</p>';
            return;
        }

        list.innerHTML = records.map(a => `
            <div class="card">
                <h3>${escapeHtml(a.user?.name || "Participant")}</h3>
                <p>
                    ${escapeHtml(a.availableDate)}
                    • ${escapeHtml(a.startTime)}
                    – ${escapeHtml(a.endTime)}
                </p>
                <button class="primary-btn"
                    onclick="deleteAvailability(${a.id})">
                    Delete
                </button>
            </div>
        `).join("");
    } catch (e) {
        list.innerHTML = `
            <p class="error">Could not load availability.
                ${escapeHtml(e.message)}</p>`;
    }
}

// Save availability entered through the dashboard.
async function saveAvailability(event) {
    event.preventDefault();

    const userId = Number(document.getElementById("availabilityUser").value);
    const availableDate = document.getElementById("availableDate").value;
    const startTime = document.getElementById("availabilityStart").value;
    const endTime = document.getElementById("availabilityEnd").value;
    const message = document.getElementById("availabilityMessage");

    if (!userId || !availableDate || !startTime || !endTime) {
        message.textContent = "Please complete all fields.";
        return;
    }

    if (startTime >= endTime) {
        message.textContent = "End time must be later than start time.";
        return;
    }

    try {
        await getJson(`${api}/availability`, {
            method: "POST",
            body: JSON.stringify({
                user: { id: userId },
                availableDate,
                startTime,
                endTime
            })
        });

        message.textContent = "Availability saved successfully.";
        document.getElementById("availabilityForm").reset();

        await loadAvailability();
    } catch (e) {
        message.textContent = `Could not save availability: ${e.message}`;
    }
}

async function deleteAvailability(id) {
    if (!confirm("Delete this availability record?")) return;

    try {
        await getJson(`${api}/availability/${id}`, { method: "DELETE" });
        await loadAvailability();
    } catch (e) {
        alert(`Could not delete availability: ${e.message}`);
    }
}

async function findSlots() {
    const request = document.getElementById("aiRequest").value.trim();
    const organizerId = document.getElementById("organizer").value;
    const result = document.getElementById("aiResult");

    if (!request) {
        result.innerHTML = '<p class="error">Enter a meeting request first.</p>';
        return;
    }

    result.innerHTML = '<p class="muted">AI agent is analyzing the request...</p>';

    try {
        const data = await getJson(`${api}/ai/schedule`, {
            method: "POST",
            body: JSON.stringify({
                request,
                organizerId: organizerId ? Number(organizerId) : null
            })
        });

        const suggestions = data.suggestions || [];

        result.innerHTML = `
            <div class="card">
                <span class="tag">INTERPRETED REQUEST</span>
                <h3>${escapeHtml(data.interpretedTitle)}</h3>
                <p>${data.durationMinutes} minutes •
                    ${escapeHtml(data.preferredPeriod)} •
                    ${escapeHtml(data.message)}</p>
                ${suggestions.map(s => `
                    <div class="slot">
                        <div>
                            <strong>${escapeHtml(s.date)}</strong><br>
                            ${escapeHtml(s.startTime)} - ${escapeHtml(s.endTime)}
                        </div>
                        <button class="primary-btn"
                            onclick='createMeeting(${JSON.stringify(data)}, ${JSON.stringify(s)})'>
                            Book
                        </button>
                    </div>
                `).join("")}
            </div>`;
    } catch (e) {
        result.innerHTML = `<p class="error">${escapeHtml(e.message)}</p>`;
    }
}

async function createMeeting(ai, slot) {
    const organizerId = Number(document.getElementById("organizer").value);

    if (!organizerId) {
        alert("Choose an organizer.");
        return;
    }

    try {
        await getJson(`${api}/meetings`, {
            method: "POST",
            body: JSON.stringify({
                title: ai.interpretedTitle || "Team Meeting",
                description: "Created by MeetingAI scheduling agent.",
                meetingDate: slot.date,
                startTime: slot.startTime,
                endTime: slot.endTime,
                organizerId,
                participantIds: []
            })
        });

        alert("Meeting scheduled successfully.");
        await loadMeetings();
    } catch (e) {
        alert(`Could not schedule meeting: ${e.message}`);
    }
}

async function cancelMeeting(id) {
    if (!confirm("Cancel this meeting?")) return;

    try {
        await fetch(`${api}/meetings/${id}`, { method: "DELETE" });
        await loadMeetings();
    } catch (e) {
        alert(`Could not cancel meeting: ${e.message}`);
    }
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[c]));
}


async function initializeDashboard() {
    try {
        await loadUsers();

        // Copy the loaded organizer options into the availability dropdown.
        const organizer = document.getElementById("organizer");
        const userSelect = document.getElementById("availabilityUser");

        if (organizer && userSelect) {
            userSelect.innerHTML = organizer.innerHTML;
            userSelect.options[0].textContent = "Choose team member";
        }
    } catch (e) {
        console.error("Could not load users:", e);
    }

    await Promise.allSettled([
        loadMeetings(),
        loadAvailability()
    ]);
}

initializeDashboard();


function toggleParticipants() {
    const userList = document.getElementById("userList");
    const toggleButton = document.getElementById("toggleParticipantsBtn");

    if (!userList || !toggleButton) return;

    userList.hidden = !userList.hidden;

    toggleButton.textContent = userList.hidden
        ? "Show Participants"
        : "Hide Participants";
}

