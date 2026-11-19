
/* =========================================================
   BOOKING ROOM
   File: booking-room.js

   Java endpoints expected:
   GET /adminViewHostels
   GET /adminViewRooms

   This script loads real hostel and room data.
   It does not submit bookings or process payments yet.
========================================================= */

(() => {
    "use strict";

    // -----------------------------------------------------
    // 1. CONFIGURATION
    // -----------------------------------------------------

    // Empty string means the Java backend uses the same origin.
    // If necessary, change this to "http://localhost:8080".
    const API_BASE = "";
    const HOSTELS_ENDPOINT = `${API_BASE}/adminViewHostels`;
    
    let currentStep = 1;
    let hostels = [];
    let selectedHostel = null;
    let toastTimer;
    
    const $ = (id) => document.getElementById(id);

    const stepPanels = [
        ...document.querySelectorAll(".booking-step-panel")
    ];

    const stepIndicators = [
        ...document.querySelectorAll("[data-step-indicator]")
    ];

    // -----------------------------------------------------
    // 2. HELPER FUNCTIONS
    // -----------------------------------------------------

    function money(amount) {
        return new Intl.NumberFormat("en-KE", {
            style: "currency",
            currency: "KES",
            maximumFractionDigits: 2
        }).format(Number(amount) || 0);
    }

    function showToast(message) {
        const toast = $("bookingToast");

        if (!toast) {
            alert(message);
            return;
        }

        toast.textContent = message;
        toast.classList.add("show");

        clearTimeout(toastTimer);

        toastTimer = setTimeout(() => {
            toast.classList.remove("show");
        }, 4200);
    }

    async function fetchJson(url) {
        const response = await fetch(url, {
            method: "GET",
            headers: {
                Accept: "application/json"
            },
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error(
                `Request failed: ${response.status} ${url}`
            );
        }

        return response.json();
    }

    function roomHostelId(room) {
        return Number(room.hostelId ?? room.hostel_id);
    }

    function roomId(room) {
        return Number(room.id ?? room.roomId ?? room.room_id);
    }

    function roomNumber(room) {
        return String(room.roomNumber ?? room.room_number ?? "");
    }

    function roomType(room) {
        return String(room.roomType ?? room.room_type ?? "");
    }

    function roomFee(room) {
        return Number(
            room.accommodationFee ?? room.accommodation_fee ?? 0
        );
    }

    function roomStatus(room) {
        return String(room.status ?? "").toUpperCase();
    }

    // Only rooms marked AVAILABLE are displayed.
    // This does not replace checking actual booking occupancy.
    function getAvailableRooms(hostelId) {

    return bookingAvailableRooms.filter(room =>
        roomHostelId(room) === Number(hostelId) &&
        roomStatus(room) === "AVAILABLE" &&
        Number(room.remainingBeds) > 0
    );
    }

  
// -----------------------------------------------------
// 3. LOAD HOSTELS FROM JAVA
// -----------------------------------------------------

async function loadBookingData() {

    const hostelContainer = $("categoryScroll");

    if (hostelContainer) {
        hostelContainer.textContent = "Loading available hostels...";
    }

    try {

        // Fetch hostel details and remaining application capacity.
        const hostelData = await fetchJson(HOSTELS_ENDPOINT);

        if (!Array.isArray(hostelData)) {
            throw new Error(
                "The Java endpoint must return a JSON array."
            );
        }

        // Store hostel information.
        hostels = hostelData;

        // Display hostel cards with application availability.
        renderHostels();

        $("availabilityMessage").textContent = hostels.length
            ? "Select a hostel to continue your application."
            : "No hostels were returned by the server.";

        console.log(
            "Hostels and application availability:",
            hostels
        );

    } catch (error) {

        console.error("Booking data loading failed:", error);

        if (hostelContainer) {
            hostelContainer.textContent =
                "Could not load hostels. Check the Java endpoint.";
        }

        $("availabilityMessage").textContent =
            "Unable to load hostel information.";

        showToast(
            "Could not load hostel information from Java."
        );
    }
}


    
// -----------------------------------------------------
// 4. DISPLAY HOSTEL CARDS
// -----------------------------------------------------

function renderHostels(searchText = "") {
    const container = $("categoryScroll");

    if (!container) return;

    const query = searchText.trim().toLowerCase();

    const filteredHostels = hostels.filter(hostel => {
        const searchableText = [
            hostel.name,
            hostel.location,
            hostel.description
        ].filter(Boolean).join(" ").toLowerCase();

        return searchableText.includes(query);
    });

    container.replaceChildren();

    if (filteredHostels.length === 0) {
        const message = document.createElement("p");
        message.className = "empty-state";
        message.textContent = hostels.length
            ? "No hostels match your search."
            : "No hostels are available.";

        container.append(message);
        return;
    }

    filteredHostels.forEach(hostel => {

        // Get application availability from the database response.
        const availableApplications =
            Number(hostel.availableApplications) || 0;

        // Room arrangement is descriptive only.
        const occupantsPerRoom = hostel.occupantsPerRoom;

        const card = document.createElement("button");
        card.type = "button";
        card.className = "category-card";
        card.dataset.hostelId = String(hostel.id);

        const isSelected =
            selectedHostel &&
            Number(selectedHostel.id) === Number(hostel.id);

        card.classList.toggle("selected", Boolean(isSelected));
        card.setAttribute("aria-pressed", String(Boolean(isSelected)));

        const name = document.createElement("strong");
        name.textContent = hostel.name || "Unnamed hostel";

        const location = document.createElement("span");
        location.className = "hostel-card-location";
        location.textContent =
            hostel.location || "Location not provided";

        // Display room arrangement.
        const roomDescription = document.createElement("span");
        roomDescription.className = "hostel-card-description";
        roomDescription.textContent = occupantsPerRoom
            ? `${occupantsPerRoom} occupants per room`
            : "Room arrangement not provided";

        // Display remaining application spaces.
        const count = document.createElement("span");
        count.className = "hostel-card-count";
        count.textContent =
            `${availableApplications} application${availableApplications === 1 ? "" : "s"} remaining`;

        card.append(name, location, roomDescription, count);

        if (hostel.description) {
            const description = document.createElement("small");
            description.textContent = hostel.description;
            card.append(description);
        }

        // Prevent selection when application capacity is depleted.
        card.disabled = availableApplications <= 0;

        card.addEventListener("click", () => {
            selectHostel(hostel.id);
        });

        container.append(card);
    });

    if (window.lucide) {
        window.lucide.createIcons();
    }
}
   
// -----------------------------------------------------
// 5. SELECT HOSTEL
// -----------------------------------------------------

function selectHostel(hostelId) {
    selectedHostel = hostels.find(
        hostel => Number(hostel.id) === Number(hostelId)
    ) || null;

    // No room is selected by the applicant.
    selectedRoom = null;

    // Store the hostel ID, not its display name.
    $("hostelSelect").value = selectedHostel
        ? String(selectedHostel.id)
        : "";

    // Clear old room-related information.
    $("roomTypeDisplay").value = "";
    $("accommodationFeeDisplay").value = "";

    // No room dropdown population is required.
    renderHostels($("roomSearch").value);

    if (!selectedHostel) {
        $("availabilityMessage").textContent =
            "Please select a hostel.";
        return;
    }

    const availableApplications =
        Number(selectedHostel.availableApplications) || 0;

    if (availableApplications <= 0) {
        $("availabilityMessage").textContent =
            `Application capacity at ${selectedHostel.name} is full.`;
        return;
    }

    $("availabilityMessage").textContent =
        `${availableApplications} application spaces remaining at ${selectedHostel.name}.`;
}


    // -----------------------------------------------------
    // 7. VALIDATE EACH BOOKING STEP
    // -----------------------------------------------------

    function validateStep(step) {
        
if (step === 1) {
    if (!$("hostelSelect").value || !selectedHostel) {
        showToast("Please select a hostel.");
        return false;
    }

    const availableApplications =
        Number(selectedHostel.availableApplications) || 0;

    if (availableApplications <= 0) {
        showToast("This hostel has no remaining application spaces.");
        return false;
    }

    for (const id of ["checkIn", "stayMonths"]) {
        const field = $(id);

        if (!field.value || !field.checkValidity()) {
            field.reportValidity();
            field.focus();
            return false;
        }
    }

    // Compare the selected date against the local date.
    const now = new Date();

    const today = new Date(
        now.getTime() - now.getTimezoneOffset() * 60000
    ).toISOString().slice(0, 10);

    if ($("checkIn").value < today) {
        $("checkIn").setCustomValidity(
            "Choose today or a future date."
        );

        $("checkIn").reportValidity();
        $("checkIn").setCustomValidity("");

        return false;
    }
}


        if (step === 2) {
            for (const id of [
                "fullName",
                "email",
                "phone",
                "occupants"
            ]) {
                const field = $(id);

                if (!field || !field.checkValidity()) {
                    field?.reportValidity();
                    field?.focus();
                    return false;
                }
            }
        }

        if (step === 3 && !$("confirmDetails").checked) {
            $("confirmDetails").reportValidity();

            showToast(
                "Please confirm your booking details."
            );

            return false;
        }

        return true;
    }

    // -----------------------------------------------------
    // 8. POPULATE THE REVIEW SUMMARY
    // -----------------------------------------------------

    function fillReview() {
        const months = Number($("stayMonths").value || 1);
        const fee = selectedRoom ? roomFee(selectedRoom) : 0;

        const date = $("checkIn").value
            ? new Date(`${$("checkIn").value}T00:00:00`)
            : null;

        $("reviewHostel").textContent =
            selectedHostel?.name || "—";

        $("reviewCategory").textContent =
            selectedRoom ? roomType(selectedRoom) : "—";

        $("reviewRoom").textContent =
            selectedRoom ? roomNumber(selectedRoom) : "—";

        $("reviewDate").textContent = date
            ? date.toLocaleDateString("en-KE", {
                day: "numeric",
                month: "short",
                year: "numeric"
            })
            : "—";

        $("reviewDuration").textContent =
            `${months} ${months === 1 ? "month" : "months"}`;

        const occupants = $("occupants").value;

        $("reviewOccupants").textContent = occupants
            ? `${occupants} ${occupants === "1" ? "occupant" : "occupants"}`
            : "—";

        $("reviewName").textContent =
            $("fullName").value.trim() || "—";

        $("reviewContact").textContent = [
            $("email").value.trim(),
            $("phone").value.trim()
        ].filter(Boolean).join(" · ") || "—";

        $("reviewTotal").textContent = money(fee * months);

        $("reviewPriceNote").textContent =
            `${money(fee)} per month × ${months} ${months === 1 ? "month" : "months"}. Estimate only; no payment has been made.`;
    }

    
    // -----------------------------------------------------
// 9. NAVIGATE BETWEEN THE FOUR STEPS
// -----------------------------------------------------
function goToStep(step) {
    currentStep = step;

    // Show only the panel for the current step.
    stepPanels.forEach(panel => {
        panel.hidden = Number(panel.dataset.step) !== step;
    });

    // Update the progress indicators.
    stepIndicators.forEach(indicator => {
        const number = Number(indicator.dataset.stepIndicator);

        indicator.classList.toggle("active", number === step);
        indicator.classList.toggle("done", number < step);

        const numberElement = indicator.querySelector(".booking-step-number");

        if (numberElement) {
            numberElement.innerHTML = number < step
                ? '<i data-lucide="check" size="15"></i>'
                : String(number);
        }
    });

    // Update the step title.
    const titles = {
        1: "Choose a room",
        2: "Your details",
        3: "Review booking",
        4: "Payment"
    };

    if ($("progressTitle")) {
        $("progressTitle").textContent =
            `Step ${step} of 4 · ${titles[step]}`;
    }

    // Step 3 is 75%; Step 4 remains 75% until payment succeeds.
    const paymentCompleted =
        $("paymentStatus")?.dataset.paymentCompleted === "true";

    const percentage = paymentCompleted
        ? 100
        : Math.min((step / 4) * 100, 75);

    if ($("progressPercent")) {
        $("progressPercent").textContent =
            `${Math.round(percentage)}% complete`;
    }

    if ($("progressFill")) {
        $("progressFill").style.width = `${percentage}%`;
    }

    // Disable Back on Step 1.
    $("backBtn").disabled = step === 1;

    // Hide Continue/Make payment navigation on Step 4.
    $("nextBtn").hidden = step === 4;

    // Update the navigation button text.
    $("nextBtn").innerHTML = step === 3
        ? '<i data-lucide="credit-card" size="16"></i> Make payment'
        : 'Continue <i data-lucide="arrow-right" size="16"></i>';

    // IMPORTANT: Keep this condition.
    // Refresh the booking summary when entering Step 3.
    if (step === 3) {
        fillReview();
    }

    // IMPORTANT: Keep this condition.
    // Display the reviewed total on the payment screen.
    if (step === 4 && $("paymentTotal") && $("reviewTotal")) {
        $("paymentTotal").textContent =
            $("reviewTotal").textContent;
    }

    // Refresh Lucide icons.
    if (window.lucide) {
        window.lucide.createIcons();
    }

    // Scroll to the top of the current step.
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
    }   

    
// -----------------------------------------------------
// 10. KEEP REVIEW DATA UPDATED
// -----------------------------------------------------

function wireReviewUpdates() {

    // Keep the review summary synchronized with user input.
    [
        "stayMonths",
        "checkIn",
        "fullName",
        "email",
        "phone",
        "occupants"
    ].forEach(id => {

        $(id)?.addEventListener("input", () => {
            if (currentStep === 3) {
                fillReview();
            }
        });

        $(id)?.addEventListener("change", () => {
            if (currentStep === 3) {
                fillReview();
            }
        });
    });

    // Filter the displayed hostels as the user searches.
    $("roomSearch")?.addEventListener("input", () => {
        renderHostels($("roomSearch").value);
    });
}

    

    // -----------------------------------------------------
    // 11. INITIALIZE THE PAGE
    // -----------------------------------------------------

    function init() {
        const form = $("bookingForm");

        if (!form) {
            console.error("Could not find #bookingForm.");
            return;
        }

        // Avoid a full page reload when the form is submitted.
        form.addEventListener("submit", event => {
            event.preventDefault();
        });

        // Set the earliest selectable move-in date to today.
        const now = new Date();

        const today = new Date(
            now.getTime() - now.getTimezoneOffset() * 60000
        ).toISOString().slice(0, 10);

        $("checkIn").min = today;

        wireReviewUpdates();

         
    // Navigate between booking steps.
    $("nextBtn").addEventListener("click", async () => {
    if (!validateStep(currentStep)) return;

    // Step 1 -> Step 2 -> Step 3
    if (currentStep < 3) {
        goToStep(currentStep + 1);
        return;
    }

        // Step 3 -> Submit booking -> Step 4
        if (currentStep === 3) {

        if (!selectedRoom) {
            showToast("Please select a room.");
            return;
        }

        $("nextBtn").disabled = true;

        try {
            await submitBooking();
        } finally {
            $("nextBtn").disabled = false;
        }
        }
        });

        // Navigate backwards between booking steps.
        $("backBtn").addEventListener("click", () => {
            if (currentStep > 1) {
                goToStep(currentStep - 1);
            }
        });

        // Collapse or expand the sidebar.
        $("collapseBtn")?.addEventListener("click", () => {
            document
                .querySelector(".booking-layout > .sidebar")
                ?.classList.toggle("collapsed");
        });

        // Support button placeholder.
        $("supportBtn")?.addEventListener("click", () => {
            showToast(
                "Support contact has not been connected yet."
            );
        });

        // Start at Step 1 and load booking information.
        goToStep(1);
        loadBookingData();

        // Initialize Lucide icons.
        if (window.lucide) {
            window.lucide.createIcons();
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            init,
            { once: true }
        );
    } else {
        init();
    }
})();

