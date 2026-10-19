
"use strict";

const API_BASE = "http://localhost:8080";

// These routes must match the contexts registered in your Java Main class.
const HOSTELS_URL = `${API_BASE}/adminViewHostels`;
const ADD_ROOM_URL = `${API_BASE}/adminAddRooms`;
const ROOMS_URL = `${API_BASE}/adminViewRooms`;

const $ = (id) => document.getElementById(id);

const roomForm = $("roomForm");
let toastTimer;

// Show a notification using your existing toast element.
function showToast(message) {
    const toast = $("toast");

    if (!toast) {
        alert(message);
        return;
    }

    toast.textContent = message;
    toast.classList.add("show");

    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => {
        toast.classList.remove("show");
    }, 2800);
}

// Escape database values before placing them in HTML.
function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, (char) => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);
}

// Display status labels consistently.
function displayStatus(status) {
    const labels = {
        AVAILABLE: "Available",
        MAINTENANCE: "Maintenance",
        CLOSED: "Closed"
    };

    return labels[status] || status;
}

// Load registered hostels into the Rooms page dropdown.
async function loadHostelDropdown() {
    const hostelSelect = $("hostelName");

    try {
        const response = await fetch(
            "http://localhost:8080/adminViewHostels"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const hostels = await response.json();

        hostelSelect.replaceChildren(
            new Option("Select a hostel", "")
        );

        hostels.forEach((hostel) => {
            hostelSelect.add(
                new Option(hostel.name, String(hostel.id))
            );
        });

    } catch (error) {
        console.error("Error loading hostel dropdown:", error);
        showToast("Failed to load hostels. Please try again.");
    }
}

// Run when the Rooms page loads.
loadHostelDropdown();


// 2. Submit a new room to the Java handler
roomForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const hostelId = $("hostelName").value;
    const roomNumber = $("roomNumber").value.trim();
    const totalBeds = $("beds").value;
    const roomType = $("roomType").value;
    const accommodationFee = $("accommodationFee").value;
    const roomStatus = $("roomStatus").value || "AVAILABLE";

    // Browser validation handles required fields, but check here too.
    if (
        !hostelId ||
        !roomNumber ||
        !totalBeds ||
        !roomType ||
        !accommodationFee
    ) {
        showToast("Please complete all required fields.");
        return;
    }

    if (
        !Number.isInteger(Number(totalBeds)) ||
        Number(totalBeds) < 1 ||
        !Number.isFinite(Number(accommodationFee)) ||
        Number(accommodationFee) < 0
    ) {
        showToast("Enter a valid number of beds and accommodation fee.");
        return;
    }

    // Names here must match data.get(...) in handleAddRooms().
    const formData = new URLSearchParams({
        hostelId: hostelId,
        roomNumber: roomNumber,
        beds: totalBeds,
        roomType: roomType,
        accommodationFee: accommodationFee,
        roomStatus: roomStatus
    });

    const saveButton = $("saveRoomBtn");
    saveButton.disabled = true;

    try {
        const response = await fetch(ADD_ROOM_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
            },
            body: formData.toString()
        });

        const message = await response.text();

        if (!response.ok) {
            showToast(message || "Could not add the room.");
            return;
        }

        showToast(message || "Room added successfully!");

        clearRoomForm();

        // Refresh the list only if the rooms GET endpoint is implemented.
        await loadRooms();
    } catch (error) {
        console.error("Error adding room:", error);
        showToast("Could not connect to the server.");
    } finally {
        saveButton.disabled = false;
    }
});



 // --------------------------------------------------
 // 3. DISPLAY ROOMS IN THE TABLE
 // --------------------------------------------------

let rooms = [];

// Fetch rooms saved in MySQL through your Java backend.
async function loadRooms() {
    try {
        const response = await fetch(
            "http://localhost:8080/adminViewRooms"
        );

        if (!response.ok) {
            throw new Error("Failed to load rooms");
        }

        rooms = await response.json();

        renderRooms();
    } catch (error) {
        console.error("Error loading rooms:", error);

        $("roomsTableBody").innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    Could not load rooms from the server.
                </td>
            </tr>
        `;

        if ($("roomCountLabel")) {
            $("roomCountLabel").textContent = "Rooms unavailable";
        }
    }
}

// Display rooms in the table.
function renderRooms() {
    const searchTerm =
        $("roomSearch")?.value.trim().toLowerCase() || "";

    const selectedStatus =
        $("statusFilter")?.value || "All";

    const filteredRooms = rooms.filter(room => {
        const status = displayStatus(room.status);

        const searchableText = `
            ${room.hostelName || ""}
            ${room.roomNumber || ""}
            ${room.totalBeds ?? ""}
            ${room.roomType || ""}
            ${room.accommodationFee ?? ""}
            ${status}
        `.toLowerCase();

        const matchesSearch =
            searchableText.includes(searchTerm);

        const matchesStatus =
            selectedStatus === "All" ||
            status.toLowerCase() === selectedStatus.toLowerCase();

        return matchesSearch && matchesStatus;
    });

    if (filteredRooms.length === 0) {
        $("roomsTableBody").innerHTML = `
            <tr>
                <td colspan="7" class="empty-state">
                    No rooms found. Try another search or add a room.
                </td>
            </tr>
        `;
    } else {
        $("roomsTableBody").innerHTML = filteredRooms
            .map((room, index) => `
                <tr>
                    <td>${index + 1}</td>

                    <td>
                        <strong>${escapeHtml(room.hostelName || "—")}</strong>
                    </td>

                    <td>${escapeHtml(room.roomNumber || "")}</td>

                    <td>${Number(room.totalBeds || 0)}</td>

                    <td>${escapeHtml(room.roomType || "")}</td>

                    <td>
                        KES ${Number(room.accommodationFee || 0).toLocaleString("en-KE", {
                            minimumFractionDigits: 2,
                            maximumFractionDigits: 2
                        })}
                    </td>

                    <td>
                    <span class="status-badge ${String(room.status || "").toLowerCase()}">
                        ${escapeHtml(displayStatus(room.status))}
                    </span>
                    </td>
                </tr>
            `)
            .join("");
    }

    // Update the number of displayed rooms.
    if ($("roomCountLabel")) {
        $("roomCountLabel").textContent =
            `Showing ${filteredRooms.length} of ${rooms.length} rooms`;
    }

    // Update dashboard statistics using all loaded rooms.
    if ($("totalRooms")) {
        $("totalRooms").textContent = rooms.length;
    }

    if ($("availableRooms")) {
        $("availableRooms").textContent =
            rooms.filter(room => room.status === "AVAILABLE").length;
    }

    if ($("totalCapacity")) {
        $("totalCapacity").textContent =
            rooms.reduce(
                (sum, room) => sum + Number(room.totalBeds || 0),
                0
            );
    }

    if (window.lucide) {
        window.lucide.createIcons();
    }
}

// Reload the displayed rows whenever search or status changes.
$("roomSearch")?.addEventListener("input", renderRooms);
$("statusFilter")?.addEventListener("change", renderRooms);

// Load the actual database records when the page opens.
loadRooms();


// --------------------------------------------------
// 4. Clear the form
// --------------------------------------------------

function clearRoomForm() {
    roomForm.reset();
    $("roomId").value = "";

    // Explicitly restore the default status.
    $("roomStatus").value = "AVAILABLE";

    if (window.lucide) {
        window.lucide.createIcons();
    }
}

// --------------------------------------------------
// 5. Existing page controls
// --------------------------------------------------

$("cancelRoomEditBtn")?.addEventListener("click", clearRoomForm);

$("showRoomFormBtn")?.addEventListener("click", () => {
    clearRoomForm();

    $("roomFormSection")?.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });

    $("hostelName").focus();
});

$("roomSearch")?.addEventListener("input", loadRooms);
$("statusFilter")?.addEventListener("change", loadRooms);

$("collapseBtn")?.addEventListener("click", () => {
    $("sidebar")?.classList.toggle("collapsed");
});

// Initialise the page.
document.addEventListener("DOMContentLoaded", async () => {
    await loadHostels();
    await loadRooms();

    if (window.lucide) {
        window.lucide.createIcons();
    }
});
