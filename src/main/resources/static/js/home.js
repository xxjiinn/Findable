// 인증 상태 확인
window.onload = async function () {
    const welcomeMessage = document.getElementById("welcomeMessage");
    const messageElement = document.getElementById("message");

    try {
        // 사용자 정보 가져오기 (Access Token 사용)
        const accessToken = sessionStorage.getItem("accessToken");
        if (!accessToken) {
            throw new Error("Access Token이 없습니다.");
        }

        const response = await fetch("/api/user/me", {
            method: "GET",
            headers: {
                Authorization: accessToken,
            },
        });

        if (response.ok) {
            const user = await response.json();
            welcomeMessage.textContent = `환영합니다, ${user.name}님!`;
        } else if (response.status === 401) {
            // Access Token 만료 시 새로 발급
            await refreshAccessToken();
        } else {
            // 인증 실패 시 로그인 페이지로 이동
            window.location.href = "/login.html";
        }
    } catch (error) {
        messageElement.textContent = "인증에 실패했습니다. 다시 로그인해주세요.";
        setTimeout(() => {
            window.location.href = "/login.html";
        }, 1500);
    }
};

// 로그아웃 기능
document.getElementById("logoutBtn").addEventListener("click", async function () {
    const messageElement = document.getElementById("message");

    try {
        const response = await fetch("/api/auth/logout", {
            method: "POST",
            credentials: "include", // 쿠키 포함
        });

        if (response.ok) {
            // Session Storage에서 Access Token 제거
            sessionStorage.removeItem("accessToken");

            // 로그아웃 후 로그인 페이지로 이동
            window.location.href = "/login.html";
        } else {
            messageElement.textContent = "로그아웃에 실패했습니다. 다시 시도해주세요.";
        }
    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다.";
    }
});

// 게시물 페이지로 이동
document.getElementById("goToPostsBtn").addEventListener("click", function () {
    window.location.href = "/posts.html"; // 게시물 페이지로 이동
});

// Access Token 갱신
async function refreshAccessToken() {
    try {
        const response = await fetch("/api/auth/refresh", {
            method: "POST",
            credentials: "include", // HttpOnly 쿠키 포함
        });

        if (response.ok) {
            const data = await response.json();
            sessionStorage.setItem("accessToken", `Bearer ${data.accessToken}`);
            location.reload(); // 페이지 새로고침으로 재인증 시도
        } else {
            throw new Error("Access Token 갱신 실패");
        }
    } catch (error) {
        window.location.href = "/login.html";
    }
}
