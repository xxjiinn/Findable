document.addEventListener("DOMContentLoaded", async () => {
    const noticeDetails = document.getElementById("noticeDetails");
    const errorMessage = document.getElementById("errorMessage");

    // URL에서 공지사항 ID 추출
    const urlParams = new URLSearchParams(window.location.search);
    const noticeId = urlParams.get("id");

    if (!noticeId) {
        errorMessage.textContent = "공지사항 ID가 제공되지 않았습니다.";
        return;
    }

    const accessToken = sessionStorage.getItem("accessToken");
    if (!accessToken) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login.html";
        return;
    }

    try {
        const response = await fetch(`/api/notice/${noticeId}`, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${accessToken}`,
            },
        });

        if (response.ok) {
            const notice = await response.json();
            renderNoticeDetails(notice);
        } else if (response.status === 404) {
            errorMessage.textContent = "공지사항을 찾을 수 없습니다.";
        } else {
            errorMessage.textContent = "공지사항을 불러오는 데 실패했습니다.";
        }
    } catch (error) {
        console.error("⚠️ Error fetching notice:", error);
        errorMessage.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
    }

    function renderNoticeDetails(notice) {
        noticeDetails.innerHTML = `
            <div class="notice-header">
                <h2>${notice.title}</h2>
                <p class="meta">
                    <strong>카테고리:</strong> ${notice.category} | 
                    <strong>조회수:</strong> ${notice.views}
                </p>
            </div>
            <div class="notice-content">
                <p>${notice.content}</p>
            </div>
        `;
    }
});
