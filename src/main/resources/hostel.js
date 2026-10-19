
console.log("hostels.js is working!");



// 2. GET HTML ELEMENTS
// These IDs must match the IDs in hostels.html.
const hostelIdInput = document.getElementById("hostelId");
const hostelNameInput = document.getElementById("hostelName");
const hostelLocationInput = document.getElementById("hostelLocation");
const hostelDescriptionInput =document.getElementById("hostelDescription");
const hostelStatusInput = document.getElementById("hostelStatus");
const hostelTableBody = document.getElementById("hostelTableBody");
const hostelSearchInput = document.getElementById("hostelSearch");
const hostelStatusFilter = document.getElementById("hostelStatusFilter");
const toast = document.getElementById("toast");

// 3. HELPER FUNCTIONS


function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;",
    })[character]);
}

function showToast(message) {
    if (!toast) {
        console.log(message);
        return;
    }

    toast.textContent = message;
    toast.classList.add("show");

    window.setTimeout(() => {
        toast.classList.remove("show");
    }, 2800);
}

function resetHostelForm() {
    hostelForm.reset();
    hostelIdInput.value = "";

    document.getElementById("formHeading").textContent =
        "Add New Hostel";

    document.getElementById("saveHostelBtn").innerHTML =
        '<i data-lucide="plus"></i> Add Hostel';

    if (window.lucide) {
        lucide.createIcons();
    }
}
 
 // 4. DISPLAY HOSTELS IN THE TABLE

let hostels = [];

// Fetch hostels saved in MySQL through your Java backend.
async function loadHostels() {
    try {
        const response = await fetch(
            "http://localhost:8080/adminViewHostels"
        );

        if (!response.ok) {
            throw new Error("Failed to load hostels");
        }

        hostels = await response.json();

        renderHostels();
    } catch (error) {
        console.error("Error loading hostels:", error);

        hostelTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-state">
                    Could not load hostels from the server.
                </td>
            </tr>
        `;
    }
}

// Display hostels in the table.
function renderHostels() {
    const searchTerm =
        hostelSearchInput.value.trim().toLowerCase();

    const filteredHostels = hostels.filter(hostel => {
        const searchableText =
            `${hostel.name} ${hostel.location} ${hostel.description || ""}`
                .toLowerCase();

        return searchableText.includes(searchTerm);
    });

    if (filteredHostels.length === 0) {
        hostelTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-state">
                    No hostels found. Try another search or add a hostel.
                </td>
            </tr>
        `;
    } else {
        hostelTableBody.innerHTML = filteredHostels
            .map((hostel, index) => `
                <tr>
                    <td>${index + 1}</td>

                    <td>
                        <strong>${escapeHtml(hostel.name)}</strong>
                    </td>

                    <td>${escapeHtml(hostel.location)}</td>

                    <td>
                        ${escapeHtml(hostel.description || "—")}
                    </td>

                    <td>
                        <button
                            type="button"
                            class="table-action-btn"
                            data-action="rooms"
                            data-id="${hostel.id}">
                            Manage Rooms
                        </button>
                    </td>
                </tr>
            `)
            .join("");
    }

    document.getElementById("hostelCount").textContent =
        `Showing ${filteredHostels.length} of ${hostels.length} hostels`;
}

// Reload the displayed rows whenever the search changes.
hostelSearchInput.addEventListener("input", renderHostels);

// Load the actual database records when the page opens.
loadHostels();



// 5. ADD HOSTEL: SEND DATA TO THE JAVA BACKEND

const hostelForm = document.getElementById("hostelForm");
const messagePopup = document.getElementById("messagePopup");

hostelForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    // Get values from the hostel form
    const hostelName = document.getElementById("hostelName").value;
    const location = document.getElementById("hostelLocation").value;
    const description = document.getElementById("hostelDescription").value;

    // Prepare form data for Java
    const data = new URLSearchParams();

    data.append("hostelName", hostelName);
    data.append("location", location);
    data.append("description", description);

    try {

        const response = await fetch(
            "http://localhost:8080/adminAddHostels",
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

        // Decide popup type based on HTTP status
        if (response.ok) {
            messagePopup.classList.add("success");
            messagePopup.classList.remove("error");

            // Reset only after a successful response
            hostelForm.reset();
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
        messagePopup.className = "message-popup error show";
        console.error("Error:", error);
    }

});


// 6. TABLE ACTIONS
// Edit and list/unlist are frontend-only at this stage.

hostelTableBody.addEventListener("click", event => {
    const button = event.target.closest("button[data-action]");

    if (!button) return;

    const hostel = hostels.find(
        item => item.id === Number(button.dataset.id)
    );

    if (!hostel) return;

    if (button.dataset.action === "edit") {
        hostelIdInput.value = hostel.id;
        hostelNameInput.value = hostel.name;
        hostelLocationInput.value = hostel.location;
        hostelDescriptionInput.value = hostel.description;
        hostelStatusInput.value = hostel.status;

        document.getElementById("formHeading").textContent =
            "Edit Hostel";

        document.getElementById("saveHostelBtn").innerHTML =
            '<i data-lucide="save"></i> Save Changes';

        if (window.lucide) {
            lucide.createIcons();
        }

        document.getElementById("hostelFormSection").scrollIntoView({
            behavior: "smooth",
            block: "start",
        });
    }

    if (button.dataset.action === "toggle") {
        hostel.status =
            hostel.status === "LISTED" ? "UNLISTED" : "LISTED";

        renderHostels();

        showToast(
            `${hostel.name} is now ${hostel.status.toLowerCase()} in the preview.`
        );
    }

    if (button.dataset.action === "rooms") {
        showToast(
            `Room management for "${hostel.name}" will be built on the Rooms page.`
        );
    }
});

// --------------------------------------------------
// 7. OTHER FORM CONTROLS
// --------------------------------------------------

const resetFormBtn = document.getElementById("resetFormBtn");

if (resetFormBtn) {
    resetFormBtn.addEventListener("click", resetHostelForm);
}

const addHostelBtn = document.getElementById("addHostelBtn");

if (addHostelBtn) {
    addHostelBtn.addEventListener("click", () => {
        document.getElementById("hostelFormSection").scrollIntoView({
            behavior: "smooth",
            block: "start",
        });

        hostelNameInput.focus({ preventScroll: true });
    });
}

hostelSearchInput.addEventListener("input", renderHostels);
// --------------------------------------------------
// 8. SIDEBAR AND SUPPORT
// --------------------------------------------------

// Support button
const supportBtn = document.getElementById("supportBtn");

if (supportBtn) {
    supportBtn.addEventListener("click", () => {
        showToast("Connect this button to your support contact when ready.");
    });
}
// --------------------------------------------------
// 9. INITIALIZE THE PAGE
// --------------------------------------------------

renderHostels();
