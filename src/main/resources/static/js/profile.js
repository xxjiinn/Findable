document.addEventListener("DOMContentLoaded", () => {
    const profileName = document.getElementById("profile-name");
    const profileEmail = document.getElementById("profile-email");
    const profileJoined = document.getElementById("profile-joined");
    const profileRole = document.getElementById("profile-role");
    const logoutButton = document.getElementById("logoutButton");

    async function fetchProfile() {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("You need to login first.");
                window.location.href = "/login.html";
                return;
            }

            const response = await fetch("/api/user/profile", {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const data = await response.json();
                profileName.textContent = data.name;
                profileEmail.textContent = data.email;
                profileJoined.textContent = new Date(data.joinedAt).toLocaleDateString();
                profileRole.textContent = data.role;
            } else if (response.status === 401) {
                alert("Session expired. Please login again.");
                window.location.href = "/login.html";
            } else {
                alert("Failed to load profile.");
            }
        } catch (error) {
            console.error("Error fetching profile:", error);
            alert("Server error. Please try again later.");
        }
    }

    fetchProfile();

    logoutButton.addEventListener("click", () => {
        sessionStorage.removeItem("accessToken");
        alert("Logged out successfully.");
        window.location.href = "/login.html";
    });
});
