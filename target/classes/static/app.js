const api = "/api";

async function getJson(url, options={}) {
    const res = await fetch(url, {
        headers: {"Content-Type":"application/json"},
        ...options
    });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Request failed");
    }
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
        </div>`).join("");

    users.forEach(u => {
        organizer.innerHTML += `<option value="${u.id}">${escapeHtml(u.name)} — ${escapeHtml(u.email)}</option>`;
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
            <p>${escapeHtml(m.meetingDate)} • ${escapeHtml(m.startTime)} - ${escapeHtml(m.endTime)}</p>
            <p class="muted">${escapeHtml(m.description || "No description")}</p>
            ${m.status !== "CANCELLED" ? `<button class="primary-btn" onclick="cancelMeeting(${m.id})">Cancel</button>` : ""}
        </div>`).join("");
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
            method:"POST",
            body: JSON.stringify({request, organizerId: organizerId ? Number(organizerId) : null})
        });

        result.innerHTML = `
            <div class="card">
                <span class="tag">INTERPRETED REQUEST</span>
                <h3>${escapeHtml(data.interpretedTitle)}</h3>
                <p>${data.durationMinutes} minutes • ${escapeHtml(data.preferredPeriod)} • ${escapeHtml(data.message)}</p>
                ${data.suggestions.map((s, i) => `
                    <div class="slot">
                        <div><strong>${escapeHtml(s.date)}</strong><br>
                        ${escapeHtml(s.startTime)} - ${escapeHtml(s.endTime)}</div>
                        <button class="primary-btn" onclick='createMeeting(${JSON.stringify(data)}, ${JSON.stringify(s)})'>Book</button>
                    </div>`).join("")}
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

    await getJson(`${api}/meetings`, {
        method:"POST",
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
}

async function cancelMeeting(id) {
    if (!confirm("Cancel this meeting?")) return;
    await fetch(`${api}/meetings/${id}`, {method:"DELETE"});
    await loadMeetings();
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, c => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[c]));
}

loadUsers().catch(console.error);
loadMeetings().catch(console.error);
