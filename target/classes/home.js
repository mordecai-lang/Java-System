
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
