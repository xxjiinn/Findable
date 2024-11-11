// signup.js

document.getElementById("signup-form").addEventListener("submit", async function (event) {
    event.preventDefault(); // 폼의 기본 제출 동작 방지

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("/api/user/signup", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name, email, password })
        });

        if (response.ok) {
            alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
            location.href = "/login.html"; // 회원가입 성공 시 로그인 페이지로 이동
        } else if (response.status === 409) {
            alert("이미 사용 중인 이메일 또는 이름입니다.");
        } else {
            alert("회원가입 중 오류가 발생했습니다.");
        }
    } catch (error) {
        console.error("회원가입 요청 실패:", error);
        alert("회원가입 요청 중 오류가 발생했습니다.");
    }
});
