let initialMatchList = [];

/* -----------------------------
 * Load matches when DOM is ready
 * ----------------------------- */
document.addEventListener("DOMContentLoaded", () => {
    loadMatches();

    const searchBox = document.getElementById("nameSearchInput");
    searchBox.addEventListener("input", debounce(searchUsersByName, 300));

    // If there's already input (e.g., autofill), search immediately
    if (searchBox.value.trim() !== "") {
        searchUsersByName();
    }
});

/* -----------------------------
 * Debounce to limit rapid typing calls
 * ----------------------------- */
function debounce(fn, ms = 300) {
    let t;
    return (...args) => {
        clearTimeout(t);
        t = setTimeout(() => fn(...args), ms);
    };
}

/* -----------------------------
 * Load matches from backend
 * ----------------------------- */
function loadMatches() {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("You must log in first.");
        return;
    }

    fetch(`http://localhost:8080/api/skill-matching/match/${userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Match fetch failed");
            return res.json();
        })
        .then(matches => {
            initialMatchList = matches;
            renderMatches(matches);
        })
        .catch(err => {
            console.error(err);
            alert("Could not load matches.");
        });
}

/* -----------------------------
 * Search users by name
 * ----------------------------- */
function searchUsersByName() {
    const searchBox = document.getElementById("nameSearchInput");
    const name = searchBox.value.trim();

    // If input is empty, restore the full list
    if (name === "") {
        renderMatches(initialMatchList);
        return;
    }

    fetch(`http://localhost:8080/api/skill-matching/search-user-name?name=${encodeURIComponent(name)}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to search users");
            return res.json();
        })
        .then(renderMatches)
        .catch(err => {
            console.error("Error searching users:", err);
            alert("Could not search users.");
        });
}

/* -----------------------------
 * Render match cards to the grid
 * ----------------------------- */
function renderMatches(matches) {
    const grid = document.getElementById("matchGrid");
    const empty = document.getElementById("emptyMsg");

    grid.innerHTML = "";
    empty.style.display = matches.length ? "none" : "block";

    matches.forEach(m => {
        const card = document.createElement("div");
        card.className = "card";
        card.innerHTML = `
            <h3>👤 ${m.name}</h3>
            <p class="skills"><strong>🔍 Looking to Learn:</strong> ${m.matchingTeaches.join(", ")}</p>
            <p class="skills"><strong>💬 Skills they Offer:</strong> ${m.matchingLearns.join(", ")}</p>
            <div class="btn-row">
                <button class="btn btn-primary" onclick="sendRequest(${m.userId})">Send Request</button>
                <button class="btn btn-secondary" onclick="viewProfile(${m.userId})">View Profile</button>
            </div>`;
        grid.appendChild(card);
    });
}

/* -----------------------------
 * Navigation functions
 * ----------------------------- */
function sendRequest(matchId) {
    window.location.href = `noteBox.html?matchId=${matchId}`;
}

function viewProfile(matchId) {
    window.location.href = `userprofile.html?userId=${matchId}`;
}
