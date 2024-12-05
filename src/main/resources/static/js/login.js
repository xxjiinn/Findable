document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const messageElement = document.getElementById("message");
    messageElement.textContent = "";

    if (!email || !password) {
        messageElement.textContent = "모든 필드를 입력해주세요.";
        return;
    }

    try {
        const response = await fetch("/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
            const data = await response.json();

            // 추가된 디버깅 로깅
            console.log("Access Token:", data.accessToken);

            if (data.accessToken) {
                sessionStorage.setItem("accessToken", `Bearer ${data.accessToken}`);
            } else {
                console.error("Access Token is undefined");
            }

            messageElement.textContent = "로그인 성공!";
            messageElement.classList.add("success");

            setTimeout(() => {
                window.location.href = "/home.html";
            }, 1000);
        } else {
            const errorData = await response.json();
            messageElement.textContent = errorData.message || "로그인에 실패했습니다.";
        }

    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
    }
});
