
console.log("home.js is working!");

// Get all sidebar navigation links
const navItems = document.querySelectorAll(".nav-item");

// Identify the current URL path
const currentPath = window.location.pathname.replace(/\/+$/, "") || "/";

// Highlight the link matching the current page
navItems.forEach(item => {
    const href = item.getAttribute("href");

    // Ignore placeholder links such as href="#"
    if (!href || href === "#") {
        return;
    }

    const linkPath = new URL(href, window.location.origin)
        .pathname
        .replace(/\/+$/, "") || "/";

    const isCurrentPage = linkPath === currentPath;

    item.classList.toggle("active", isCurrentPage);

    if (isCurrentPage) {
        item.setAttribute("aria-current", "page");
    } else {
        item.removeAttribute("aria-current");
    }
});

// Sidebar collapse
const sidebar = document.querySelector(".sidebar");
const collapseBtn = document.getElementById("collapseBtn");

if (sidebar && collapseBtn) {
    collapseBtn.addEventListener("click", function () {
        console.log("Collapse button clicked!");
        sidebar.classList.toggle("collapsed");
    });
}

/**
 * Load the users five most recent bookings from the Java backend.
 */

async function loadRecentBookings() {
    const bookingsBody = document.getElementById("recentBookingsBody");

    try {
        const response = await fetch(
            "http://localhost:8080/ActualUsersRecentBookings"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const bookings = await response.json();

        // Display at most five bookings.
        const recentBookings = bookings.slice(0, 5);

        if (recentBookings.length === 0) {
            bookingsBody.innerHTML =
                '<p class="booking-message">No bookings found.</p>';
            return;
        }

        bookingsBody.innerHTML = recentBookings.map(booking => `
            <div class="booking-row">
                <span>${escapeHtml(booking.hostelName)}</span>
                <span>${escapeHtml(booking.checkInDate)}</span>
                <span>${escapeHtml(booking.checkOutDate)}</span>
                <span>
                    <span class="status ${getStatusClass(booking.bookingStatus)}">
                        ${escapeHtml(booking.bookingStatus)}
                    </span>
                </span>
            </div>
        `).join("");

    } catch (error) {
        console.error("Error loading recent bookings:", error);

        bookingsBody.innerHTML =
            '<p class="booking-message">Could not load recent bookings.</p>';
    }
}



// Escape database values before inserting them into HTML.
function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}


// Only allow known status classes in the CSS class attribute.
function getStatusClass(status) {
    const allowedStatuses = {
        "CONFIRMED": "confirmed",
        "PENDING": "pending",
        "CANCELLED": "cancelled",
        "COMPLETED": "completed"
    };

    return allowedStatuses[String(status ?? "").toUpperCase()]
        || "pending";
}


// Run when the dashboard loads.
loadRecentBookings();

// -----------------------------------------------------
// LOAD MY ACTIVE BOOKINGS COUNT
// -----------------------------------------------------
async function loadMyBookingsCount() {
    try {
        const response = await fetch(
            "http://localhost:8080/ActualUsersRecentBookings"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const bookings = await response.json();

        // Count only PENDING and CONFIRMED bookings.
        const activeBookings = bookings.filter(booking =>
            booking.bookingStatus === "PENDING" ||
            booking.bookingStatus === "CONFIRMED"
        ).length;

        // Display the calculated figure.
        document.getElementById("myBookingsCount").textContent =
            activeBookings;

    } catch (error) {
        console.error("Unable to load active bookings:", error);
    }
}

// Load the figure when the page is ready.
loadMyBookingsCount();

// -----------------------------------------------------
// LOAD TOTAL AVAILABLE APPLICATIONS
// -----------------------------------------------------
async function loadAvailableApplicationsCount() {
    try {
        const response = await fetch(
            "http://localhost:8080/availableApplications"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const hostels = await response.json();

        // Add the available applications from every hostel.
        const totalAvailableApplications = hostels.reduce(
            (total, hostel) => {
                return total + (Number(hostel.availableApplications) || 0);
            },
            0
        );

        // Display the combined figure.
        document.getElementById("availableApplicationsCount").textContent =
            totalAvailableApplications;

    } catch (error) {
        console.error("Unable to load available applications:", error);
    }
}

// Load the figure when the page is ready.
loadAvailableApplicationsCount();

// -----------------------------------------------------
// LOAD TOTAL HOSTELS
// -----------------------------------------------------
async function loadTotalHostelsCount() {
    try {
        const response = await fetch(
            "http://localhost:8080/adminViewHostels"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const hostels = await response.json();

        // Count all returned hostel records.
        const totalHostels = hostels.length;

        // Display the total.
        document.getElementById("totalHostelsCount").textContent =
            totalHostels;

    } catch (error) {
        console.error("Unable to load total hostels:", error);
    }
}

// Load the figure.
loadTotalHostelsCount();

// -----------------------------------------------------
// LOAD PENDING PAYMENTS COUNT
// -----------------------------------------------------
async function loadPendingPaymentsCount() {
    try {
        const response = await fetch(
            "http://localhost:8080/ActualUsersRecentBookings"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const bookings = await response.json();

        // Count only PENDING bookings.
        const pendingPayments = bookings.filter(booking =>
            booking.bookingStatus === "PENDING"
        ).length;

        // Display the figure.
        document.getElementById("pendingPaymentsCount").textContent =
            pendingPayments;

    } catch (error) {
        console.error("Unable to load pending payments:", error);
    }
}

// Load the figure.
loadPendingPaymentsCount();