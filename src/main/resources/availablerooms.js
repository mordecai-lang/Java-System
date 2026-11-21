// -----------------------------------------------------
// 1. HOSTEL ENDPOINT
// -----------------------------------------------------

const HOSTELS_ENDPOINT =
    "http://localhost:8080/availableApplications";


// -----------------------------------------------------
// 2. GET HTML ELEMENT
// -----------------------------------------------------

function $(id) {
    return document.getElementById(id);
}


// -----------------------------------------------------
// 3. LOAD AVAILABLE HOSTELS FROM JAVA
// -----------------------------------------------------

async function loadAvailableHostels() {

    const container = $("hostelsContainer");

    container.textContent = "Loading available hostels...";

    try {

        const response = await fetch(HOSTELS_ENDPOINT);

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const hostels = await response.json();

        if (!Array.isArray(hostels)) {
            throw new Error(
                "The Java endpoint must return a JSON array."
            );
        }

        displayAvailableHostels(hostels);

    } catch (error) {

        console.error("Available hostels loading failed:", error);

        container.textContent =
            "Unable to load available hostels. Please try again later.";

    }
}


// -----------------------------------------------------
// 4. DISPLAY AVAILABLE HOSTELS
// -----------------------------------------------------

function displayAvailableHostels(hostels) {

    const container = $("hostelsContainer");

    container.replaceChildren();

    if (hostels.length === 0) {

        const message = document.createElement("p");

        message.textContent = "No hostels were returned by the server.";

        container.append(message);

        return;
    }

    hostels.forEach(hostel => {

        const availableApplications =
            Number(hostel.availableApplications) || 0;

        // Main hostel section.
        const hostelCard = document.createElement("article");

        hostelCard.className = "available-hostel-card";

        // Hostel name.
        const name = document.createElement("h2");

        name.textContent = hostel.name || "Unnamed hostel";

        // Location.
        const location = document.createElement("p");

        location.textContent =
            `Location: ${hostel.location || "Not provided"}`;

        // Description.
        const description = document.createElement("p");

        description.textContent =
            hostel.description || "No description provided.";

        // Room arrangement.
        const arrangement = document.createElement("p");

        arrangement.textContent =
            `Room arrangement: ${hostel.roomArrangement || "Not specified"}`;

        // Monthly accommodation fee.
        const fee = document.createElement("p");

        fee.textContent =
            `Monthly accommodation fee: KSh ${Number(hostel.accommodationFee || 0).toLocaleString("en-KE")}`;

        // Remaining applications.
        const availability = document.createElement("p");

        availability.textContent =
            `${availableApplications} application spaces remaining`;

        // Add availability information.
        const status = document.createElement("p");

        status.textContent = availableApplications > 0
          ? "Applications available"
          : "Application capacity full";

        status.className = availableApplications > 0
          ? "status-available"
          : "status-full";
        // Add all hostel details.
        hostelCard.append(
            name,
            location,
            description,
            arrangement,
            fee,
            availability,
            status
        );

        container.append(hostelCard);

    });

    if (window.lucide) {
        window.lucide.createIcons();
    }
}


// -----------------------------------------------------
// 5. INITIALIZE PAGE
// -----------------------------------------------------

document.addEventListener("DOMContentLoaded", () => {

    loadAvailableHostels();

    if (window.lucide) {
        window.lucide.createIcons();
    }

});