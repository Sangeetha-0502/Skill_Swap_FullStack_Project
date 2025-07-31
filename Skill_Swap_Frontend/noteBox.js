

// ✅ Get matchId from URL
function getMatchIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("matchId");
}


document.addEventListener("DOMContentLoaded", () => {
    const noteInput = document.getElementById("noteInput");
    const submitNoteBtn = document.getElementById("submitNoteBtn");
    const skipBtn = document.getElementById("skipBtn");
    const requiredSkillInput = document.getElementById("requestedSkill");
    const offeredSkillIdInput = document.getElementById("offeredSkill");


    const matchId = getMatchIdFromUrl();

    submitNoteBtn.addEventListener("click", async () => {
        const skillRequested = requiredSkillInput.value.trim();
        const skillOffered = offeredSkillIdInput.value.trim();
        const note = noteInput.value.trim();

        if (!skillRequested || !skillOffered) {
            alert("Please fill in both skill fields.");
            return;
        }

        try {
            const { requestedSkillId, offeredSkillId } = await fetchSkillIds(skillRequested, skillOffered);
            sendSwapRequest(matchId, requestedSkillId, offeredSkillId, note);
        } catch (err) {
            alert(err.message);
        }
    });

    skipBtn.addEventListener("click", async () => {
        const skillRequested = requiredSkillInput.value.trim();
        const skillOffered = offeredSkillIdInput.value.trim();

        if (!skillRequested || !skillOffered) {
            alert("Please fill in both skill fields.");
            return;
        }

        try {
            const { requestedSkillId, offeredSkillId } = await fetchSkillIds(skillRequested, skillOffered);
            sendSwapRequest(matchId, requestedSkillId, offeredSkillId, "");
        } catch (err) {
            alert(err.message);
        }
    });
});

async function fetchSkillIds(requestedSkill, offeredSkill) {
    const token = localStorage.getItem("token");
    const url = `http://localhost:8080/api/skills/get-skills-ids?requested=${encodeURIComponent(requestedSkill)}&offered=${encodeURIComponent(offeredSkill)}`;

    const response = await fetch(url, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });
    if (!response.ok) {
        throw new Error("Could not fetch skill IDs. Check if skill names are correct.");
    }

    return await response.json(); // returns { requestedSkill, offeredSkill }
}


function sendSwapRequest(receiverId, requestedSKillId, offeredSkillId, note) {
    const token = localStorage.getItem("token");
    const senderId = localStorage.getItem("userId");

    // Optional: Add offeredSkillId and requestedSkillId based on dropdown or auto selection
    const requestBody = {
        senderId: senderId,
        receiverId: receiverId,
        requestedSkillId: requestedSKillId, // optional if you want to allow them to pick
        offeredSkillId: offeredSkillId,
        note: note

    };

    fetch("http://localhost:8080/api/swap-requests/send-swap-request", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`

        },
        body: JSON.stringify(requestBody)
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to send request");
            return res.text();
        })
        .then(msg => {
            alert("✅ Request sent successfully!");
        })
        .catch(err => {
            console.error("Error sending request:", err);
            alert("🚨 Failed to send swap request");
        });
}
