document.getElementById("signupForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const messageElement = document.getElementById("message");
    messageElement.textContent = "";

    if (password.length < 8) {
        messageElement.textContent = "비밀번호는 최소 8자 이상이어야 합니다.";
        return;
    }

    try {
        const response = await fetch("/api/user/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name, email, password }),
        });

        if (response.status === 201) {
            messageElement.textContent = "회원가입이 완료되었습니다!";
            messageElement.classList.add("success");
        } else {
            const errorData = await response.json();
            messageElement.textContent = errorData.message || "회원가입에 실패했습니다.";
        }
    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
    }
});
