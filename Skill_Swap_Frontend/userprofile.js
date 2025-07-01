
const urlParams = new URLSearchParams(window.location.search);
const viewedUserId = urlParams.get("userId") || localStorage.getItem("userId");
const isOwnProfile = viewedUserId === localStorage.getItem("userId");

// ✅ Load user profile on page load
window.onload = () => {
  if (!viewedUserId) {
    alert("You are not logged in!");
    return;
  }
  loadUserProfile(viewedUserId);
};

// ✅ Profile loading function (
function loadUserProfile(userId) {
  fetch(`http://localhost:8080/api/user/user-data/${userId}/`)
    .then(res => res.json())
    .then(user => {
      document.getElementById("username").textContent = user.name || "No name provided";
      document.getElementById("email").innerText = user.email || "Not provided";
      document.getElementById("bio").textContent = user.bio || "No bio added.";

      const linkedInLink = document.getElementById("linkedInLink");
      if (user.linkedInUrl) {
        linkedInLink.href = user.linkedInUrl;
        linkedInLink.textContent = "🔗 LinkedIn";
      } else {
        linkedInLink.href = "#";
        linkedInLink.textContent = "No LinkedIn";
      }

      const githubLink = document.getElementById("githubLink");
      if (user.githubUrl) {
        githubLink.href = user.githubUrl;
        githubLink.textContent = "🔗 GitHub";
      } else {
        githubLink.href = "#";
        githubLink.textContent = "No GitHub";
      }

      const pp = document.getElementById("profilePic");
      if (user.profilePictureUrl) {
        pp.src = `http://localhost:8080${user.profilePictureUrl}?t=${Date.now()}`;
        pp.style.display = "block";
      } else {
        pp.style.display = "none";
      }

      const certBox = document.getElementById("certificates");
      certBox.innerHTML = "";

      (user.certificateUrls || []).forEach(url => {
        const wrap = document.createElement("div");
        wrap.className = "cert-item";

        const img = document.createElement("img");
        img.src = `http://localhost:8080${url}?t=${Date.now()}`;
        img.alt = "Certificate";
        img.className = "certificate-img";
        wrap.appendChild(img);

        /* add delete button ONLY on your own profile */
        if (isOwnProfile) {
          const del = document.createElement("button");
          del.textContent = "Delete";
          del.classList.add("del-btn")
          del.onclick = () => deleteCertificate(url);
          wrap.appendChild(del);
        }
        certBox.appendChild(wrap);
      });


      fetch(`http://localhost:8080/api/user-skills/get-user-skills/${userId}`)
        .then(res => res.json())
        .then(renderSkills)
        .catch(err => console.error("Error loading skills:", err));
    })
    .catch(err => {
      console.error("Failed to load user profile:", err);
      alert("Error loading profile.");
    });
}

//to hide the edit button in other user profile
if (!isOwnProfile) {
  document.querySelectorAll("button").forEach(btn => {
    const text = btn.textContent.toLowerCase();
    if (text.includes("edit") || text.includes("upload") || text.includes("delete") || text.includes("add")) {
      btn.style.display = "none";
    }
  });

  ["nameInputWrapper", "emailInputWrapper", "bioInputWrapper", "linkedInInputWrapper", "githubInputWrapper"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.style.display = "none";
  });

  const actionElement = document.getElementById("actionId");
  actionElement.style.display = "none";
  const addSkillBtn = document.querySelector(".add-skill-button");
  const certUploadSec = document.querySelector(".upload-certificate-section");
  if (addSkillBtn) addSkillBtn.style.display = "none";
  if (certUploadSec) certUploadSec.style.display = "none";

  const h1 = document.querySelector("h1");
  if (h1) h1.textContent = "User Profile";
}

/* ---------------------------------------------------------------
   RENDER  SKILLS  TABLE   (adds edit buttons with data-* attrs)
   --------------------------------------------------------------- */
function renderSkills(skills) {
  const tableWrap = document.getElementById("skillsTableContainer");
  const tbody = document.getElementById("skillsTable");

  if (!skills.length) {
    tableWrap.style.display = "none";
    tbody.innerHTML = "";
    return;
  }

  tableWrap.style.display = "block";
  tbody.innerHTML = "";

  skills.forEach(us => {
    const row = document.createElement("tr");

    // --- skill name column ---
    const nameTd = document.createElement("td");
    const nameSpan = document.createElement("span");
    nameSpan.className = "value";
    nameSpan.textContent = us.skill.skillName;
    nameTd.appendChild(nameSpan);

    const typeTd = document.createElement("td");
    const typeSpan = document.createElement("span");
    typeSpan.className = "value";
    typeSpan.textContent = us.type;
    typeTd.appendChild(typeSpan);
    row.appendChild(nameTd);
    row.appendChild(typeTd);

    // add pencil only on own profile
    if (isOwnProfile) {
      const editBtn = document.createElement("button");
      editBtn.className = "edit-btn";
      editBtn.dataset.userSkillId = us.id;
      editBtn.dataset.skillId = us.skill.id;
      editBtn.dataset.field = "skillName";
      editBtn.style.border = "none";
      editBtn.style.background = "none";
      editBtn.style.cursor = "pointer";
      editBtn.innerHTML = '<i class="fas fa-pencil-alt" style="color:#555;"></i>';
      nameTd.appendChild(editBtn);
      const delTd = document.createElement("td");
      const delBtn = document.createElement("button");
      delBtn.textContent = "🗑️";
      delBtn.title = "Delete skill";
      delBtn.style.cursor = "pointer";
      delBtn.onclick = () => deleteSkill(us.id);
      delTd.appendChild(delBtn);
      row.appendChild(delTd);
      const editBtn2 = document.createElement("button");
      editBtn2.className = "edit-btn";
      editBtn2.dataset.userSkillId = us.id;
      editBtn2.dataset.field = "type";
      editBtn2.style.border = "none";
      editBtn2.style.background = "none";
      editBtn2.style.cursor = "pointer";
      editBtn2.innerHTML = '<i class="fas fa-pencil-alt" style="color:#555;"></i>';
      typeTd.appendChild(editBtn2);

    }

    tbody.appendChild(row);
  });
}
function deleteSkill(userSkillId) {
  if (!confirm("Delete this skill?")) return;

  fetch(`http://localhost:8080/api/user-skills/delete-user-skill/${userSkillId}`, {
    method: "DELETE"
  })
    .then(r => {
      if (!r.ok) throw new Error("Deletion failed");
      return r.text();
    })
    .then(() => {
      alert("Skill deleted ✅");
      loadUserProfile(viewedUserId);   // refresh list
    })
    .catch(e => { console.error(e); alert("Could not delete skill"); });
}


/* -----------------------------------------------------------
   INLINE‑EDIT handler for skill name  ⟶ PUT update‑name
   and for skill type (TEACH/LEARN)   ⟶ PUT update‑type
   --------------------------------------------------------- */
document.getElementById("skillsTable")
  .addEventListener("click", handleEditClick);    // only ONE listener now

function handleEditClick(e) {
  const btn = e.target.closest(".edit-btn");
  if (!btn) return;

  const td = btn.parentElement;
  const span = td.querySelector(".value");
  const original = span.textContent.trim();

  const userSkillId = btn.dataset.userSkillId;
  const field = btn.dataset.field;

  let editor;                                   // <input> or <select>
  if (field === "skillName") {
    editor = document.createElement("input");
    editor.type = "text";
    editor.value = original;
  } else {
    editor = document.createElement("select");
    ["TEACH", "LEARN"].forEach(val => {
      const o = new Option(val, val, false, val === original);
      editor.appendChild(o);
    });
  }
  editor.className = "inline-input";

  td.replaceChild(editor, span);
  editor.focus();

  editor.addEventListener("keydown", ev => { if (ev.key === "Enter") save(); });
  editor.addEventListener("blur", save);

  let saved = false;          // <‑‑ lives in the closure, starts false

  async function save() {
    if (saved) return;        // ignore any second call
    saved = true;             // mark as handled

    const updated = editor.value.trim();
    if (!updated || updated === original) { revert(original); return; }

    try {
      if (field === "skillName") {
        const qs = new URLSearchParams({ skillName: updated }).toString();
        await fetch(`http://localhost:8080/api/user-skills/update-user-skill-name/${userSkillId}?${qs}`,
          { method: "PUT" });
        alert("skillName updated successfully");
      } else {
        await fetch(`http://localhost:8080/api/user-skills/update-user-skill-type/${userSkillId}?type=${encodeURIComponent(updated)}`,
          { method: "PUT" });
      }
      revert(updated);
    } catch (err) {
      console.error(err);
      alert("Update failed – please try again.");
      revert(original);
    }
  }


  /* FIX‑3: guard against “node not a child” error */
  function revert(text) {
    const newSpan = document.createElement("span");
    newSpan.className = "value";
    newSpan.textContent = text;

    if (td.contains(editor)) {                 // only replace if still present
      td.replaceChild(newSpan, editor);
    } else {                                   // editor already gone (double blur)
      td.appendChild(newSpan);
    }
  }
}


// Toggle functions
function toggleLinkedInInput() {
  toggleDisplay("linkedInInputWrapper");
}

function toggleGithubInput() {
  toggleDisplay("githubInputWrapper");
}

function toggleEmailInput() {
  toggleDisplay("emailInputWrapper");
}

function toggleNameInput() {
  toggleDisplay("nameInputWrapper");
}

function toggleBioInput() {
  toggleDisplay("bioInputWrapper");
}

function toggleDisplay(elementId) {
  const el = document.getElementById(elementId);
  if (el.style.display === "none" || el.style.display === "") {
    el.style.display = "inline-block";
  } else {
    el.style.display = "none";
  }
}

// Submit functions
function submitLinkedIn() {
  const userId = localStorage.getItem("userId");
  const linkedInUrl = document.getElementById("linkedInInput").value.trim();

  if (!userId || !linkedInUrl) {
    alert("Please enter your LinkedIn URL!");
    return;
  }

  fetch(`http://localhost:8080/api/user/add-linkedin/${userId}?linkedInUrl=${encodeURIComponent(linkedInUrl)}`, {
    method: "PUT"
  })
    .then(res => {
      if (!res.ok) throw new Error("Failed to update LinkedIn URL");
      return res.json();
    })
    .then(data => {
      alert("✅ LinkedIn URL updated!");
      const link = document.getElementById("linkedInLink");
      link.href = data.linkedInUrl;
      link.textContent = "🔗 LinkedIn";

      document.getElementById("linkedInInput").value = "";
      document.getElementById("linkedInInputWrapper").style.display = "none";
    })
    .catch(error => {
      console.error("Error updating LinkedIn:", error);
      alert("🚨 Error updating LinkedIn URL.");
    });
}

function submitGithub() {
  const userId = localStorage.getItem("userId");
  const githubUrl = document.getElementById("githubInput").value.trim();

  if (!userId || !githubUrl) {
    alert("Please enter the GitHub URL");
    return;
  }

  fetch(`http://localhost:8080/api/user/add-github/${userId}?githubUrl=${encodeURIComponent(githubUrl)}`, {
    method: "PUT"
  })
    .then(res => {
      if (!res.ok) throw new Error("Failed to update GitHub URL");
      return res.json();
    })
    .then(data => {
      alert("✅ GitHub URL updated!");
      const link = document.getElementById("githubLink");
      link.href = data.githubUrl;
      link.textContent = "🔗 GitHub";

      document.getElementById("githubInput").value = "";
      document.getElementById("githubInputWrapper").style.display = "none";
    })
    .catch(error => {
      console.error("Error updating GitHub:", error);
      alert("🚨 Error updating GitHub URL.");
    });
}

function submitProfileUpdate() {
  const userId = localStorage.getItem("userId");
  if (!userId) {
    alert("User ID missing. Please login again.");
    return;
  }

  const nameInput = document.getElementById("nameInput");
  const bioInput = document.getElementById("bioInput");
  const emailInput = document.getElementById("emailInput");

  const name = nameInput.value.trim();
  const bio = bioInput.value.trim();
  const email = emailInput.value.trim();

  const payload = {};
  if (name) payload.name = name;
  if (bio) payload.bio = bio;
  if (email) payload.email = email;

  if (Object.keys(payload).length === 0) {
    alert("Please enter at least one field to update.");
    return;
  }

  fetch("http://localhost:8080/api/user/update-profile", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ userId, ...payload })
  })
    .then(response => {
      if (response.ok) {
        alert("Profile updated successfully!");

        if (name) {
          document.getElementById("username").textContent = name;
          nameInput.value = "";
          document.getElementById("nameInputWrapper").style.display = "none";
        }

        if (bio) {
          document.getElementById("bio").textContent = bio;
          bioInput.value = "";
          document.getElementById("bioInputWrapper").style.display = "none";
        }

        if (email) {
          document.getElementById("email").textContent = email;
          emailInput.value = "";
          document.getElementById("emailInputWrapper").style.display = "none";
        }
      } else {
        response.text().then(text => alert("Update failed: " + text));
      }
    })
    .catch(error => {
      console.error("Error:", error);
      alert("Something went wrong.");
    });
}

function uploadProfilePic() {
  const userId = localStorage.getItem("userId");
  const fileInput = document.getElementById("profilePicInput");
  const file = fileInput.files[0];

  if (!file || !userId) {
    alert("Please select an image and make sure you're logged in.");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  fetch(`http://localhost:8080/api/user/upload-profile-picture/${userId}`, {
    method: "POST",
    body: formData
  })
    .then(res => res.text())
    .then(imageUrl => {
      alert("✅ Profile picture updated!");
      const img = document.getElementById("profilePic");
      img.src = "http://localhost:8080" + imageUrl + "?t=" + new Date().getTime();//important
      img.style.display = "block";
      fileInput.value = ""; // Reset input
    })
    .catch(err => {
      console.error(err);
      alert("Failed to upload profile picture.");
    });
}

function deleteProfilePic() {
  const userId = localStorage.getItem("userId");
  if (!userId) {
    alert("You're not logged in.");
    return;
  }

  if (!confirm("Are you sure you want to delete your profile picture?")) return;

  fetch(`http://localhost:8080/api/user/delete-profile-picture/${userId}`, {
    method: "DELETE"
  })
    .then(res => res.text())
    .then(msg => {
      alert("🗑️ " + msg);
      const img = document.getElementById("profilePic");
      img.style.display = "none";
      img.src = ""; // Remove src for safety
    })
    .catch(err => {
      console.error(err);
      alert("Failed to delete profile picture.");
    });
}

function uploadCertificate() {
  const fileInput = document.getElementById("certificateInput");
  const file = fileInput.files[0];

  if (!file) {
    alert("Please select a certificate image to upload.");
    return;
  }

  const userId = localStorage.getItem("userId");
  if (!userId) {
    alert("User not logged in!");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  fetch(`http://localhost:8080/api/user/upload-certificate/${userId}`, {
    method: "POST",
    body: formData
  })
    .then(res => res.text())
    .then(url => {
      const certContainer = document.getElementById("certificates");

      const certDiv = document.createElement("div");
      certDiv.classList.add("cert-item");

      const certImg = document.createElement("img");
      certImg.src = "http://localhost:8080" + url + "?t=" + new Date().getTime();
      certImg.alt = "Certificate";
      certImg.classList.add("certificate-img");
      certImg.onclick = () => openModal(certImg.src);

      const delBtn = document.createElement("button");
      delBtn.textContent = "Delete";
      delBtn.classList.add("del-btn");
      delBtn.onclick = () => deleteCertificate(url);

      certDiv.appendChild(certImg);
      certDiv.appendChild(delBtn);

      certContainer.appendChild(certDiv);

      alert("Certificate uploaded successfully!");
      fileInput.value = "";
    })
    .catch(err => {
      console.error("Upload failed", err);
      alert("Certificate upload failed!");
    });
}

//function to open the image 
function openModal(imgSrc) {
  const modal = document.getElementById("imageModal");
  const modalImg = document.getElementById("modalImage");
  modal.style.display = "flex";
  modalImg.src = imgSrc;
}

function closeModal() {
  document.getElementById("imageModal").style.display = "none";
}

function deleteCertificate(certificateUrl) {
  const userId = localStorage.getItem("userId");
  if (!userId || !certificateUrl) {
    alert("User or certificate URL missing!");
    return;
  }

  fetch(`http://localhost:8080/api/user/delete-certificate/${userId}?certificateUrl=${encodeURIComponent(certificateUrl)}`, {
    method: "DELETE"
  })
    .then(res => {
      if (!res.ok) throw new Error("Failed to delete certificate");
      return res.text();
    })
    .then(msg => {
      alert(msg);
      loadUserProfile(userId); // Refresh the UI with updated list
    })
    .catch(err => {
      console.error(err);
      alert("Error deleting certificate");
    });
}


