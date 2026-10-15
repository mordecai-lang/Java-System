const loginForm = document.getElementById("userForm");
const messagePopup = document.getElementById("messagePopup");

loginForm.addEventListener("submit", async function(event) {

    event.preventDefault();
     console.log("Login form submitted");
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = new URLSearchParams();

    data.append("email", email);
    data.append("password", password);

    try {

        const response = await fetch(
            "http://localhost:8080/loginData",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result = await response.text();

        // Successful login
        if (response.ok) {
        messagePopup.textContent = "Login successful!";
        messagePopup.classList.add("success");
        messagePopup.classList.remove("error");
        messagePopup.classList.add("show");

        setTimeout(function() {

            messagePopup.classList.remove("show");
  
        if (result === "ADMIN") {
            window.location.href = "/admin";
        } else if (result === "USER") {
            window.location.href = "/home";
        }

        }, 1000);

       } else {

    messagePopup.textContent = result;

    messagePopup.classList.add("error");
    messagePopup.classList.remove("success");
    messagePopup.classList.add("show");

    setTimeout(function() {
        messagePopup.classList.remove("show");
    }, 1000);
    }

    } catch (error) {

        messagePopup.textContent = "Could not connect to the server.";

        messagePopup.classList.add("error");
        messagePopup.classList.remove("success");

        messagePopup.classList.add("show");

        setTimeout(function() {
            messagePopup.classList.remove("show");
        }, 3000);

        console.error("Error:", error);
    }

});