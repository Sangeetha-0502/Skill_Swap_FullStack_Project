
function getToken() {
  return localStorage.getItem("token");
}

function getViewedUserId() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get("userId") || localStorage.getItem("userId");
}

function isOwnProfile() {
  return getViewedUserId() === localStorage.getItem("userId");
}
// ✅ Load user profile on page load
window.onload = () => {
  const viewedUserId = getViewedUserId();
  const token = getToken();
  if (!viewedUserId) {
    alert("You are not logged in!");
    return;
  }

  if (!token) {
    alert("Authentication token not found. Please log in again.");

    window.location.href = "/login.html";
    return;
  }
  loadUserProfile(viewedUserId);
};


function loadUserProfile(userId) {
  const token = getToken();
  fetch(`http://localhost:8080/api/user/user-data/${userId}/`, {
    method: "GET",
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  })
    .then(res => {
      if (!res.ok) {

        if (res.status === 401 || res.status === 403) {
          throw new Error("Unauthorized access. Please log in.");
        }
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      return res.json();
    })
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
        const token = localStorage.getItem("token");
        fetch(`http://localhost:8080${user.profilePictureUrl}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }

        })
          .then(response => {
            if (!response.ok) {
              throw new Error('Failed to fetch image');
            }
            return response.blob();
          })
          .then(blob => {
            const imageUrl = URL.createObjectURL(blob);
            pp.src = imageUrl;
            pp.style.display = "block";
          })
          .catch(error => {
            console.error("Error fetching profile picture:", error);
            pp.style.display = "none";
          });
      } else {
        pp.style.display = "none";
      }

      const certBox = document.getElementById("certificates");
      certBox.innerHTML = "";

      (user.certificateUrls || []).forEach(url => {
        const wrap = document.createElement("div");
        wrap.className = "cert-item";

        const img = document.createElement("img");
        img.alt = "Certificate";
        img.className = "certificate-img";
        // Fetch the certificate image with the token
        fetch(`http://localhost:8080${url}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }

        })
          .then(res => {
            if (!res.ok) {
              throw new Error(`Failed to load certificate image: ${res.status}`);
            }
            return res.blob();
          })
          .then(blob => {
            const certImageUrl = URL.createObjectURL(blob);
            img.src = certImageUrl;
            img.onclick = () => openModal(certImageUrl);
          })
          .catch(err => {
            console.error("Error loading certificate image:", err);
            img.src = "";
            img.alt = "Failed to load image";
          });

        wrap.appendChild(img);

        if (isOwnProfile()) {
          const del = document.createElement("button");
          del.textContent = "Delete";
          del.classList.add("del-btn")
          del.onclick = () => deleteCertificate(url);
          wrap.appendChild(del);
        }
        certBox.appendChild(wrap);
      });


      fetch(`http://localhost:8080/api/user-skills/get-user-skills/${userId}`,
        {
          method: "GET",
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }

        }
      )
        .then(res => {
          if (!res.ok) {
            if (res.status === 401 || res.status === 403) {
              throw new Error("Unauthorized access to skills.");
            }
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.json();
        })
        .then(renderSkills)
        .catch(err => console.error("Error loading skills:", err));
    })
    .catch(err => {
      console.error("Failed to load user profile:", err);
      alert("Error loading profile: " + err.message);

      if (err.message.includes("Unauthorized")) {
        window.location.href = "/login.html";
      }
    });
}


if (!isOwnProfile()) {
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

    if (isOwnProfile()) {
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
  const viewedUserId = getViewedUserId();
  const token = getToken();
  if (!confirm("Delete this skill?")) return;

  fetch(`http://localhost:8080/api/user-skills/delete-user-skill/${userSkillId}`, {
    method: "DELETE",
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }

  })
    .then(r => {
      if (!r.ok) throw new Error("Deletion failed");
      return r.text();
    })
    .then(() => {
      alert("Skill deleted ✅");
      loadUserProfile(viewedUserId);
    })
    .catch(e => { console.error(e); alert("Could not delete skill"); });
}



document.getElementById("skillsTable")
  .addEventListener("click", handleEditClick);
function handleEditClick(e) {
  const btn = e.target.closest(".edit-btn");
  if (!btn) return;

  const td = btn.parentElement;
  const span = td.querySelector(".value");
  const original = span.textContent.trim();

  const userSkillId = btn.dataset.userSkillId;
  const field = btn.dataset.field;

  let editor;  // <input> or <select>
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

  let saved = false; // 

  async function save() {
    if (saved) return;
    saved = true;

    const token = getToken();
    const updated = editor.value.trim();
    if (!updated || updated === original) { revert(original); return; }

    try {
      if (field === "skillName") {
        const qs = new URLSearchParams({ skillName: updated }).toString();
        await fetch(`http://localhost:8080/api/user-skills/update-user-skill-name/${userSkillId}?${qs}`,
          {
            method: "PUT",
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }

          });
        alert("skillName updated successfully");
      } else {
        await fetch(`http://localhost:8080/api/user-skills/update-user-skill-type/${userSkillId}?type=${encodeURIComponent(updated)}`,
          {
            method: "PUT",
            headers: {
              "Authorization": `Bearer ${token}`,
              "Content-Type": "application/json"
            }
          });
        alert("Skill type updated successfully");
      }
      revert(updated);
    } catch (err) {
      console.error(err);
      alert("Update failed – please try again.");
      revert(original);
    }
  }



  function revert(text) {
    const newSpan = document.createElement("span");
    newSpan.className = "value";
    newSpan.textContent = text;

    if (td.contains(editor)) {
      td.replaceChild(newSpan, editor);
    } else {
      td.appendChild(newSpan);
    }
  }
}

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

function submitLinkedIn() {
  const token = getToken();
  const userId = localStorage.getItem("userId");
  const linkedInUrl = document.getElementById("linkedInInput").value.trim();

  if (!userId || !linkedInUrl) {
    alert("Please enter your LinkedIn URL!");
    return;
  }
  if (!token) { alert("Not authenticated."); return; } // Added token check

  fetch(`http://localhost:8080/api/user/add-linkedin/${userId}?linkedInUrl=${encodeURIComponent(linkedInUrl)}`, {
    method: "PUT",
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }

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
  const token = getToken();
  const userId = localStorage.getItem("userId");
  const githubUrl = document.getElementById("githubInput").value.trim();

  if (!userId || !githubUrl) {
    alert("Please enter the GitHub URL");
    return;
  }
  if (!token) { alert("Not authenticated."); return; }

  fetch(`http://localhost:8080/api/user/add-github/${userId}?githubUrl=${encodeURIComponent(githubUrl)}`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    }
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
  const token = getToken();
  const userId = localStorage.getItem("userId");
  if (!userId) {
    alert("User ID missing. Please login again.");
    return;
  }
  if (!token) { alert("Not authenticated."); return; }

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
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
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
  const token = getToken();
  const userId = localStorage.getItem("userId");
  const fileInput = document.getElementById("profilePicInput");
  const file = fileInput.files[0];

  if (!file || !userId) {
    alert("Please select an image and make sure you're logged in.");
    return;
  }
  if (!token) { alert("Not authenticated."); return; } // Added token check

  const formData = new FormData();
  formData.append("file", file);

  fetch(`http://localhost:8080/api/user/upload-profile-picture/${userId}`, {
    method: "POST",

    headers: {
      "Authorization": `Bearer ${token}`
    },
    body: formData
  })
    .then(res => {
      if (!res.ok) {
        throw new Error(`Upload failed: ${res.status} ${res.statusText}`);
      }
      return res.text();
    })
    .then(imageUrl => {
      alert("✅ Profile picture updated!");
      const img = document.getElementById("profilePic");

      fetch(`http://localhost:8080${imageUrl}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
        .then(res => res.blob())
        .then(blob => {
          img.src = URL.createObjectURL(blob);
          img.style.display = "block";
        })
        .catch(err => {
          console.error("Error displaying uploaded profile picture:", err);
          img.style.display = "none";
        });

      fileInput.value = "";
    })
    .catch(err => {
      console.error(err);
      alert("Failed to upload profile picture: " + err.message);
    });
}

function deleteProfilePic() {
  const token = getToken();
  const userId = localStorage.getItem("userId");
  if (!userId) {
    alert("You're not logged in.");
    return;
  }
  if (!token) { alert("Not authenticated."); return; }

  if (!confirm("Are you sure you want to delete your profile picture?")) return;

  fetch(`http://localhost:8080/api/user/delete-profile-picture/${userId}`, {
    method: "DELETE",
    headers: {
      "Authorization": `Bearer ${token}`
    }
  })
    .then(res => {
      if (!res.ok) {
        throw new Error(`Deletion failed: ${res.status} ${res.statusText}`);
      }
      return res.text();
    })
    .then(msg => {
      alert("🗑️ " + msg);
      const img = document.getElementById("profilePic");
      img.style.display = "none";
      img.src = "";
    })
    .catch(err => {
      console.error(err);
      alert("Failed to delete profile picture: " + err.message);
    });
}

function uploadCertificate() {
  const token = getToken();
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
  if (!token) { alert("Not authenticated."); return; }

  const formData = new FormData();
  formData.append("file", file);

  fetch(`http://localhost:8080/api/user/upload-certificate/${userId}`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`
    },
    body: formData
  })
    .then(res => {
      if (!res.ok) {
        throw new Error(`Upload failed: ${res.status} ${res.statusText}`);
      }
      return res.text();
    })
    .then(url => {
      const certContainer = document.getElementById("certificates");

      const certDiv = document.createElement("div");
      certDiv.classList.add("cert-item");

      const certImg = document.createElement("img");
      certImg.alt = "Certificate";
      certImg.classList.add("certificate-img");


      fetch(`http://localhost:8080${url}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
        .then(res => res.blob())
        .then(blob => {
          const certImageUrl = URL.createObjectURL(blob);
          certImg.src = certImageUrl;
          certImg.onclick = () => openModal(certImageUrl);
        })
        .catch(err => {
          console.error("Error displaying uploaded certificate:", err);
          certImg.src = "";
          certImg.alt = "Failed to load image";
        });

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
      alert("Certificate upload failed: " + err.message);
    });
}


function openModal(imgSrc) {
  const token = getToken();
  const modal = document.getElementById("imageModal");
  const modalImg = document.getElementById("modalImage");
  modal.style.display = "flex";
  modalImg.src = imgSrc;
}

function closeModal() {
  document.getElementById("imageModal").style.display = "none";
}

function deleteCertificate(certificateUrl) {
  const token = getToken();
  const userId = localStorage.getItem("userId");
  if (!userId || !certificateUrl) {
    alert("User or certificate URL missing!");
    return;
  }
  if (!token) { alert("Not authenticated."); return; }

  fetch(`http://localhost:8080/api/user/delete-certificate/${userId}?certificateUrl=${encodeURIComponent(certificateUrl)}`, {
    method: "DELETE",
    headers: {
      "Authorization": `Bearer ${token}`,
    }
  })
    .then(res => {
      if (!res.ok) throw new Error("Failed to delete certificate");
      return res.text();
    })
    .then(msg => {
      alert(msg);
      loadUserProfile(userId);
    })
    .catch(err => {
      console.error(err);
      alert("Error deleting certificate: " + err.message);
    });
}