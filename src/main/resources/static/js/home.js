// 인증 상태 확인
window.onload = async function () {
    const welcomeMessage = document.getElementById("welcomeMessage");
    const messageElement = document.getElementById("message");

    try {
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

            // 공지사항 가져오기
            fetchNotices();
        } else if (response.status === 401) {
            await refreshAccessToken();
        } else {
            window.location.href = "/login.html";
        }
    } catch (error) {
        messageElement.textContent = "인증에 실패했습니다. 다시 로그인해주세요.";
        setTimeout(() => {
            window.location.href = "/login.html";
        }, 1500);
    }
};

// 공지사항 가져오기
async function fetchNotices() {
    const noticeList = document.getElementById("noticeList");
    const noticeErrorMessage = document.getElementById("noticeErrorMessage");

    try {
        const accessToken = sessionStorage.getItem("accessToken");
        const response = await fetch("/api/notice", {
            method: "GET",
            headers: {
                Authorization: accessToken,
            },
        });

        if (response.ok) {
            const notices = await response.json();
            renderNotices(notices);
        } else {
            noticeErrorMessage.textContent = "공지사항을 불러오는 데 실패했습니다.";
        }
    } catch (error) {
        console.error("공지사항 로드 오류:", error);
        noticeErrorMessage.textContent = "네트워크 오류가 발생했습니다.";
    }
}

// 공지사항 렌더링
function renderNotices(notices) {
    const noticeList = document.getElementById("noticeList");
    noticeList.innerHTML = "";

    if (notices.length === 0) {
        noticeList.innerHTML = "<p>등록된 공지사항이 없습니다.</p>";
        return;
    }

    notices.forEach(notice => {
        const listItem = document.createElement("li");
        listItem.innerHTML = `
            <div>
                <h4>${notice.title}</h4>
                <p>${notice.content}</p>
                <small>조회수: ${notice.views}</small>
            </div>
        `;
        noticeList.appendChild(listItem);
    });
}

// 로그아웃
document.getElementById("logoutBtn").addEventListener("click", async function () {
    const messageElement = document.getElementById("message");

    try {
        const response = await fetch("/api/auth/logout", {
            method: "POST",
            credentials: "include",
        });

        if (response.ok) {
            sessionStorage.removeItem("accessToken");
            window.location.href = "/login.html";
        } else {
            messageElement.textContent = "로그아웃에 실패했습니다. 다시 시도해주세요.";
        }
    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다.";
    }
});

// 게시물 페이지 이동
document.getElementById("goToPostsBtn").addEventListener("click", function () {
    window.location.href = "/posts.html";
});

// 공지사항 페이지 이동
document.getElementById("goToNoticesBtn").addEventListener("click", function () {
    window.location.href = "/notices.html";
});

// FAQ 페이지 이동
document.getElementById("goToFaqBtn").addEventListener("click", function () {
    window.location.href = "/faq.html";
});


// Access Token 갱신
async function refreshAccessToken() {
    try {
        const response = await fetch("/api/auth/refresh", {
            method: "POST",
            credentials: "include",
        });

        if (response.ok) {
            const data = await response.json();
            sessionStorage.setItem("accessToken", `Bearer ${data.accessToken}`);
            location.reload();
        } else {
            throw new Error("Access Token 갱신 실패");
        }
    } catch (error) {
        window.location.href = "/login.html";
    }
}
