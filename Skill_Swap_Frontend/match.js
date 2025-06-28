/* Load matches as soon as the document is ready */
document.addEventListener("DOMContentLoaded", loadMatches);
let initialMatchList = [];

/* -------------------------------------------------
 *  Fetch perfect matches and render as cards
 * ------------------------------------------------*/
function loadMatches() {
    const userId = localStorage.getItem("userId");
    if (!userId) { alert("You must log in first."); return; }

    fetch(`http://localhost:8080/api/skill-matching/match/${userId}`)
        .then(res => {
            if (!res.ok) throw new Error("Match fetch failed");
            return res.json();
        })
        .then(matches => {
            initialMatchList = matches;
            renderMatches(matches)
        })

        .catch(err => {
            console.error(err);
            alert("Could not load matches.");
        });
}

/* -------------------------------------------------
 *  Render match cards inside the grid
 * ------------------------------------------------*/
// function renderMatches(matches) {
//     const grid = document.getElementById("matchGrid");
//     const empty = document.getElementById("emptyMsg");

//     grid.innerHTML = "";                // clear previous
//     empty.style.display = "none";

//     if (!matches || matches.length === 0) {
//         empty.style.display = "block";
//         return;
//     }

//     matches.forEach(m => {
//         const card = document.createElement("div");
//         card.className = "card";
//         card.innerHTML = `
//       <h3>👤 ${m.name}</h3>
//       <p class="skills">✍️ Teaches : <strong>${(m.teachSkills || []).join(", ")}</strong></p>
//       <p class="skills">📚 Wants  : <strong>${(m.learnSkills || []).join(", ")}</strong></p>
//       <div class="btn-row">
//         <button class="btn btn-primary" onclick="sendRequest(${m.userId})">Send Request</button>
//         <button class="btn btn-secondary" onclick="viewProfile(${m.userId})">View Profile</button>
//       </div>`;
//         grid.appendChild(card);
//     });
// }

const searchBox = document.getElementById("nameSearchInput");
searchBox.addEventListener("input", debounce(searchUsersByName, 300));

function debounce(fn, ms = 300) {
    let t; return (...a) => { clearTimeout(t); t = setTimeout(() => fn(...a), ms); };
}

function searchUsersByName() {
    const name = document.getElementById("nameSearchInput").value.trim();

    if (name === "") {
        loadMatchingUsers(); // your original function to load matches
        return;
    }

    const q = searchBox.value.trim();      // ← .trim() kills spaces

    /* ⇢ empty box → restore original list */
    if (q === "") {
        renderMatches(initialMatchList);
        return;
    }

    fetch(`http://localhost:8080/api/skill-matching/search-user-name?name=${encodeURIComponent(name)}`)
        .then(res => {
            if (!res.ok) throw new Error("Failed to search users");
            return res.json();
        })
        .then(renderMatches) // reuse your existing render function
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
      <p class="skills"><strong>💬 Skills they Offers</strong> ${m.matchingLearns.join(", ")}</p>
      <div class="btn-row">
        <button class="btn btn-primary" onclick="sendRequest(${m.userId})">Send Request</button>
        <button class="btn btn-secondary" onclick="viewProfile(${m.userId})">View Profile</button>
      </div>`;
        grid.appendChild(card);
    });
}


/* -------- stub actions you can expand later -------- */
function sendRequest(matchId) {
    window.location.href = `noteBox.html?matchId=${matchId}`;
}
function viewProfile(matchId) {


    window.location.href = `userprofile.html?userId=${matchId}`;


}


