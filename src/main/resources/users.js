const addUserForm = document.getElementById("addUserForm");
const messagePopup = document.getElementById("messagePopup");

console.log("users.js loaded");
console.log("addUserForm:", addUserForm);

addUserForm.addEventListener("submit", async function(event) {

    event.preventDefault();
    
    console.log("ADD USER FORM SUBMITTED");

    const name = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;

    const data = new URLSearchParams();

    data.append("fullName", name);
    data.append("email", email);
    data.append("password", password);
    data.append("role", role);

    try {

        const response = await fetch(
            "http://localhost:8080/adminUserData",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },

                body: data
            }
        );
         console.log("Java responded!");
        console.log("Status:", response.status);

        const result = await response.text();

        console.log("Status:", response.status);
        console.log("Message:", result);
        console.log("Popup:", messagePopup);

        // Put Java's response inside the popup
        messagePopup.textContent = result;

        // Decide popup type based on HTTP status
        if (response.ok) {
            messagePopup.classList.add("success");
            messagePopup.classList.remove("error");
        } else {
            messagePopup.classList.add("error");
            messagePopup.classList.remove("success");
        }

        // Show popup
        messagePopup.classList.add("show");

        // Hide popup after 3 seconds
        setTimeout(function() {
            messagePopup.classList.remove("show");
        }, 3000);

    } catch (error) {

        messagePopup.textContent =
            "Could not connect to the server.";

        messagePopup.className =
            "message-popup error";

        messagePopup.classList.add("show");

        console.error("Error:", error);
    }

});

const removeUserForm = document.getElementById("removeUserForm");

removeUserForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = document.getElementById("removeUserEmail").value;

    const confirmed = confirm(
        `Are you sure you want ${email} removed?`
    );

    if (!confirmed) {
        return;
    }

    const data = new URLSearchParams();

    data.append("removeUserEmail", email);

    try {

        const response = await fetch(
            "http://localhost:8080/adminRemoveUser",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: data
            }
        );

        console.log("Java responded!");
        console.log("Status:", response.status);

        const result = await response.text();

        console.log("Message:", result);
        console.log("Popup:", messagePopup);

        // Put Java's response inside the popup
        messagePopup.textContent = result;

        // Decide popup type
        if (response.ok) {
            messagePopup.classList.add("success");
            messagePopup.classList.remove("error");
        } else {
            messagePopup.classList.add("error");
            messagePopup.classList.remove("success");
        }

        // Show popup
        messagePopup.classList.add("show");

        // Hide after 3 seconds
        setTimeout(function() {
            messagePopup.classList.remove("show");
        }, 3000);

    } catch (error) {

        messagePopup.textContent =
            "Could not connect to the server.";

        messagePopup.className =
            "message-popup error";

        messagePopup.classList.add("show");

        console.error("Error:", error);
    }

});

async function loadUsers() {

    try {

        const response = await fetch("/adminGetUsers");

        const users = await response.json();

        const tableBody = document.getElementById("usersTableBody");

        tableBody.innerHTML = "";

        users.forEach(function(user) {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td>
                    <button>Remove</button>
                </td>
            `;

            tableBody.appendChild(row);
        });

    } catch (error) {

        console.error("Could not load users:", error);

    }
}

loadUsers();