javascript
/**
 * Load the five most recent bookings for the admin page.
 */

async function loadRecentBookings() {
    const bookingsBody = document.getElementById("recentBookingsBody");

    if (!bookingsBody) {
        console.warn("Recent bookings table was not found on this page.");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/usersRecentBookings"
        );

        if (!response.ok) {
            throw new Error(`HTTP error: ${response.status}`);
        }

        const bookings = await response.json();

        // Display at most five bookings.
        const recentBookings = bookings.slice(0, 5);

        if (recentBookings.length === 0) {
            bookingsBody.innerHTML = `
                <p class="admin-booking-message">
                    No bookings found.
                </p>
            `;
            return;
        }

        bookingsBody.innerHTML = recentBookings.map(booking => `
            <div class="admin-booking-row">
                <span>${escapeHtml(booking.roomNumber)}</span>
                <span>${escapeHtml(booking.hostelName)}</span>
                <span>${escapeHtml(booking.checkInDate)}</span>
                <span>${escapeHtml(booking.checkOutDate)}</span>
                <span>
                    <span class="admin-booking-status ${getAdminBookingStatusClass(booking.bookingStatus)}">
                        ${escapeHtml(booking.bookingStatus)}
                    </span>
                </span>
            </div>
        `).join("");

    } catch (error) {
        console.error("Error loading recent bookings:", error);

        bookingsBody.innerHTML = `
            <p class="admin-booking-message">
                Could not load recent bookings.
            </p>
        `;
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


// Return only approved status class names for the admin table.
function getAdminBookingStatusClass(status) {
    const allowedStatuses = {
        "CONFIRMED": "confirmed",
        "PENDING": "pending",
        "CANCELLED": "cancelled",
        "COMPLETED": "completed"
    };

    return allowedStatuses[String(status ?? "").toUpperCase()]
        || "pending";
}


// Run when the page loads.
loadRecentBookings();
