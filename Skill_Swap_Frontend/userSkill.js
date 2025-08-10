

function addSkill() {
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const skillName = document.getElementById("skillName").value;
  const skillType = document.getElementById("skillType").value;

  if (!userId || !skillName || !skillType) {
    alert("All fields are required!");
    return;
  }

  const payload = {
    userId: userId,
    skillName: skillName,
    type: skillType
  };

  fetch(`${APP_BASE_API_URL}/api/user-skills/add-user-skill`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  })
    .then(res => {
      if (res.ok) {
        alert("Skill added successfully!");
        document.getElementById("skillName").value = "";
        window.location.href = "userprofile.html";
      } else {
        alert("Failed to add skill.");
      }
    })
    .catch(err => console.error("Error:", err));
}

