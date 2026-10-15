const studentForm = document.getElementById("studentForm");
const messagePopup = document.getElementById("messagePopup");

studentForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const name = document.getElementById("fullName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = new URLSearchParams();

    data.append("fullName", name);
    data.append("email", email);
    data.append("password", password);

    try {

        const response = await fetch(
            "http://localhost:8080/registerData",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result = await response.text();
        console.log("Status:", response.status);
        console.log("Message:", result);
        console.log("Popup:", messagePopup);

        // Put Java's response inside the popup
        messagePopup.textContent = result;

        // Decide popup type based on HTTP statusif (response.ok) {
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

        messagePopup.textContent = "Could not connect to the server.";
        messagePopup.className = "message-popup error";
        messagePopup.classList.add("show");

        console.error("Error:", error);

    }

});