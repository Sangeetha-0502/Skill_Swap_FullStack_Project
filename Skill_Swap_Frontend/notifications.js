document.addEventListener("DOMContentLoaded", function () {
    const userId = localStorage.getItem("userId");

    if (!userId) {
        alert("User not logged in!");
        window.location.href = "login.html";
        return;
    }

    fetch(`http://localhost:8080/api/swap-requests/get-notifications/${userId}`)
        .then(response => response.json())
        .then(data => {
            const container = document.getElementById("notification-list");

            if (!data.length) {
                container.innerHTML = "<p>No notifications yet!</p>";
                return;
            }

            data.forEach(notification => {
                const card = document.createElement("div");
                card.className = "notification-card";

                const who = notification.userRole === "sender"
                    ? `You sent a request to ${notification.receiverName}`
                    : `${notification.senderName} sent you a request`;

                card.innerHTML = `
          <h3>${who}</h3>
          <p><strong>Offered:</strong> ${notification.offeredSkill}</p>
          <p><strong>Requested:</strong> ${notification.requestedSkill}</p>
          <p><strong>Note:</strong> ${notification.note}</p>
          <p><strong>Status:</strong> ${notification.status}</p>
        `;

                container.appendChild(card);


                if (notification.userRole === "receiver" && notification.status === "Pending") {
                    const actionButtons = document.createElement("div");
                    actionButtons.className = "action-buttons";

                    const acceptBtn = document.createElement("button");
                    acceptBtn.textContent = "Accept";
                    acceptBtn.className = "accept";
                    acceptBtn.onclick = () => updateStatus(notification.requestId, "Accepted");

                    const rejectBtn = document.createElement("button");
                    rejectBtn.textContent = "Reject";
                    rejectBtn.className = "reject";
                    rejectBtn.onclick = () => updateStatus(notification.requestId, "Rejected");

                    actionButtons.appendChild(acceptBtn);
                    actionButtons.appendChild(rejectBtn);
                    card.appendChild(actionButtons);
                }

                if (notification.status === "Accepted") {
                    const chatBtn = document.createElement("button");
                    chatBtn.textContent = "Chat";
                    chatBtn.className = "chat-btn";

                    const currentUserId = parseInt(userId);
                    const otherUserId = currentUserId === notification.senderId
                        ? notification.receiverId
                        : notification.senderId;

                    chatBtn.onclick = () => {
                        console.log("✅ Chat button clicked");
                        console.log("Current User ID:", currentUserId);
                        console.log("Sender ID from notification:", notification.senderId);
                        console.log("Receiver ID from notification:", notification.receiverId);
                        console.log("Calculated Other User ID (Chat Receiver):", otherUserId);

                        localStorage.setItem("chatReceiverId", otherUserId);
                        window.location.href = "chat.html";
                    };


                    card.appendChild(chatBtn);
                }


            });
        })
        .catch(error => {
            console.error("Failed to fetch notifications:", error);
            document.getElementById("notification-list").innerHTML = "<p>Failed to load notifications.</p>";
        });
});

function goBack() {
    window.location.href = "landing.html";
}
function updateStatus(requestId, newStatus) {
    fetch(`http://localhost:8080/api/swap-requests/update-request-status/${requestId}?status=${newStatus}`, {
        method: "PUT",
    })
        .then(response => {
            if (response.ok) {
                alert(`Request ${newStatus.toLowerCase()} successfully!`);
                location.reload(); // reload the page to reflect the updated status
            } else {
                alert("Failed to update status");
            }
        })
        .catch(error => {
            console.error("Update failed:", error);
            alert("Something went wrong.");
        });
}
