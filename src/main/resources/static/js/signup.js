const API_BASE_URL = "http://localhost:8080/api/user";

document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch(`${API_BASE_URL}/createUser`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password })
        });

        if (response.ok) {
            alert("Signup successful! Redirecting to login page.");
            window.location.href = "login.html";
        } else if (response.status === 400) {
            alert("Signup failed: Email is already in use.");
        } else if (response.status === 500) {
            alert("Server error. Please try again later.");
        } else {
            alert("An unknown error occurred. Please try again.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred. Please check your internet connection and try again.");
    }
});