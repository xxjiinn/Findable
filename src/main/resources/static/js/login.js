// login.js
document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/api/user/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            // 로그인 성공 시 home 페이지로 이동
            window.location.href = "home.html";
        } else if (response.status === 401) {
            alert("Incorrect password. Please try again.");
        } else if (response.status === 404) {
            alert("User not found. Please check your email.");
        } else {
            alert("An error occurred. Please try again later.");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred. Please try again later.");
    }
});
