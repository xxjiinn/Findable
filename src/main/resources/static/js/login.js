const API_BASE_URL = "http://localhost:8080/api/user";
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch(`${API_BASE_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            alert("Login successful! Redirecting to home page.");
            window.location.href = "home.html";
        } else if (response.status === 401) {
            alert("Login failed: Incorrect password.");
        } else if (response.status === 404) {
            alert("Login failed: User not found.");
        } else {
            alert("An unknown error occurred. Please try again.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred. Please check your internet connection and try again.");
    }
});