document.getElementById("signupForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const messageElement = document.getElementById("message");
    messageElement.textContent = "";

    // 비밀번호 길이 검증
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
            setTimeout(() => {
                window.location.href = "/login.html"; // 로그인 페이지로 리다이렉트
            }, 1000);
        } else if (response.status === 409) {
            // 중복 이메일 에러 처리
            const errorData = await response.json();
            messageElement.textContent = `⚠️ ${errorData.message || "이미 존재하는 이메일입니다."}`;
        } else if (response.status === 400) {
            // 클라이언트 측 요청 문제
            const errorData = await response.json();
            messageElement.textContent = `⚠️ ${errorData.message || "입력값을 확인해주세요."}`;
        } else if (response.status === 500) {
            // 서버 내부 오류 처리
            messageElement.textContent = "⚠️ 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        } else {
            // 그 외 예상치 못한 상태 코드 처리
            const errorData = await response.json();
            messageElement.textContent = errorData.message || "⚠️ 알 수 없는 오류가 발생했습니다.";
        }
    } catch (error) {
        // 네트워크 오류 처리
        console.error("Error during signup:", error);
        messageElement.textContent = "⚠️ 네트워크 오류가 발생했습니다. 다시 시도해주세요.";
    }
});
