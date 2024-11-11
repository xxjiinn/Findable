// login.js

document.getElementById("login-form").addEventListener("submit", async function (event) {
    event.preventDefault(); // 폼의 기본 제출 동작 방지

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("/api/user/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email, password }) // email 필드 전송
        });

        if (response.ok) {
            const result = await response.text();
            alert("로그인 성공");
            location.href = "/home.html"; // 성공 시 홈 페이지로 이동
        } else if (response.status === 401) {
            alert("잘못된 이메일 또는 비밀번호입니다.");
        } else {
            alert("로그인 중 오류가 발생했습니다.");
        }
    } catch (error) {
        console.error("로그인 요청 실패:", error);
        alert("로그인 요청 중 오류가 발생했습니다.");
    }
});
