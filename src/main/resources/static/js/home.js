// [수정 내용]
// LocalStorage 사용을 제거하고, API 호출 시 쿠키를 자동으로 전송하도록 수정합니다.

document.addEventListener("DOMContentLoaded", function () {
    console.log("⚡ Home page loaded.");

    // API 요청 시 쿠키를 자동으로 전송하기 때문에 별도의 토큰 검증 로직이 필요 없음
    fetch("/api/user/me", {
        method: "GET",
        credentials: "include", // 쿠키 자동 전송
    })
        .then(response => {
            if (!response.ok) {
                console.warn("⚠️ Unauthorized user. Redirecting to login.");
                window.location.href = "/login.html";
                return;
            }
            return response.json();
        })
        .then(user => {
            if (user) {
                console.log("✅ User info:", user);
            }
        })
        .catch(error => {
            console.error("Error fetching user info:", error);
        });


    // 로그아웃 버튼 핸들링
    const logoutButton = document.getElementById("logout-btn");
    logoutButton.addEventListener("click", async function () {
        try {
            const response = await fetch("/api/auth/logout", {
                method: "POST",
                credentials: "include", // 쿠키 포함
            });

            if (!response.ok) throw new Error("Failed to logout.");

            alert("Logout successful.");
            window.location.href = "/login.html";
        } catch (error) {
            console.error("Logout failed:", error);
        }
    });
});
