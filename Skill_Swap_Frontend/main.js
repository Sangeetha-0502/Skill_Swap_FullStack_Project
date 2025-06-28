document.addEventListener("DOMContentLoaded", function () {
  let userName = localStorage.getItem("userName") || "there";
  const greetingElement = document.getElementById("greeting");
  let userProfilePic = localStorage.getItem("userProfilePic");
  const profile_iconElement = document.getElementById("profile-icon");

  if (userProfilePic && profile_iconElement) {
    userProfilePic = "http://localhost:8080" + userProfilePic + "?t=" + new Date().getTime();
    profile_iconElement.src = userProfilePic;
  }

  if (greetingElement) {
    greetingElement.textContent = `Hey, Hi ${userName}!`;
  }
});
function register() {
  const username = document.getElementById("username").value;
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  fetch("http://localhost:8080/api/user/user-register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      name: username,
      email: email,
      password: password
    })
  })
    .then(response => {
      if (response.ok) {
        alert("🎉 Registration successful!");
        window.location.href = "landing.html";

      } else {
        response.text().then(message => {
          alert("❌ Registration failed: " + message);
        });
      }
    })
    .catch(error => {
      console.error("Error:", error);
      alert("🚨 Something went wrong. Please try again!");
    });
}

function login() {
  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  fetch("http://localhost:8080/api/user/user-login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      email: email,
      password: password
    })
  })
    .then(response => {
      if (response.ok) {
        return response.json(); // parse the JSON body here!
      } else {
        return response.text().then(message => {
          throw new Error(message);
        });
      }
    })
    .then(data => {
      alert("Login successful!");
      localStorage.setItem("userName", data.name);//store username 
      localStorage.setItem("userId", data.id);  // store the id here
      localStorage.setItem("userProfilePic", data.profilePictureUrl);
      console.log("User ID that is returned:", data.id);

      window.location.href = "landing.html";

    })
    .catch(error => {
      console.error("Error:", error);
      alert("🚨 Login not successful! " + error.message);
    });
}
function goToProfile() {
  window.location.href = "userprofile.html";
}
function findMatch() {
  window.location.href = "match.html";
}
function goToNotifications() {
  window.location.href = "notification.html";
}
function togglePassword() {
  const passwordInput = document.getElementById("password");
  const icon = document.querySelector(".toggle-password");

  if (passwordInput.type === "password") {
    passwordInput.type = "text";
    icon.textContent = "🙈"; // change icon when visible
  } else {
    passwordInput.type = "password";
    icon.textContent = "👁️"; // back to original
  }
}


