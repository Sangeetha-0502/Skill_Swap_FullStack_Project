let initialMatchList = [];


document.addEventListener("DOMContentLoaded", () => {
    loadMatches();

    const searchBox = document.getElementById("nameSearchInput");
    searchBox.addEventListener("input", debounce(searchUsersByName, 300));


    if (searchBox.value.trim() !== "") {
        searchUsersByName();
    }
});


function debounce(fn, ms = 300) {
    let t;
    return (...args) => {
        clearTimeout(t);
        t = setTimeout(() => fn(...args), ms);
    };
}


function loadMatches() {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");
    if (!userId) {
        alert("You must log in first.");
        return;
    }

    fetch(`http://localhost:8080/api/skill-matching/match/${userId}`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
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


function searchUsersByName() {
    const token = localStorage.getItem("token");
    const searchBox = document.getElementById("nameSearchInput");
    const name = searchBox.value.trim();


    if (name === "") {
        renderMatches(initialMatchList);
        return;
    }

    fetch(`http://localhost:8080/api/skill-matching/search-user-name?name=${encodeURIComponent(name)}`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    })
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


function sendRequest(matchId) {
    window.location.href = `noteBox.html?matchId=${matchId}`;
}

function viewProfile(matchId) {
    window.location.href = `userprofile.html?userId=${matchId}`;
}
