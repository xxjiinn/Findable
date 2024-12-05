// 인증 상태 확인
window.onload = async function () {
    const welcomeMessage = document.getElementById("welcomeMessage");
    const messageElement = document.getElementById("message");
    console.log("home.js is loaded");

    try {
        // 사용자 정보 가져오기 (Access Token 사용)
        const response = await fetch("/api/user/me", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${getCookie("accessToken")}`,
            },
        });

        if (response.ok) {
            const user = await response.json();
            welcomeMessage.textContent = `환영합니다, ${user.name}님!`;
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
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                refreshToken: getCookie("refreshToken"),
                accessToken: getCookie("accessToken"),
            }),
        });

        if (response.ok) {
            // 쿠키 삭제
            removeCookie("accessToken");
            removeCookie("refreshToken");

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

// 쿠키 값 가져오기
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(";").shift();
}

// 쿠키 제거
function removeCookie(name) {
    document.cookie = `${name}=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;`;
}
