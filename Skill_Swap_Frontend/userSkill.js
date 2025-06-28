

function addSkill() {

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

  fetch("http://localhost:8080/api/user-skills/add-user-skill", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(payload)
  })
    .then(res => {
      if (res.ok) {
        alert("Skill added successfully!");
        document.getElementById("skillName").value = "";
      } else {
        alert("Failed to add skill.");
      }
    })
    .catch(err => console.error("Error:", err));
}

