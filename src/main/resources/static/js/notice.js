document.addEventListener("DOMContentLoaded", () => {
    const noticeList = document.getElementById("noticeList");
    const categoryFilter = document.getElementById("categoryFilter");
    const createNoticeButton = document.getElementById("createNoticeButton");
    const editNoticeButton = document.getElementById("editNoticeButton");
    const goHomeButton = document.getElementById("goHomeButton");

    // 공지사항 가져오기
    async function fetchNotices(category = "") {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const endpoint = category
                ? `/api/notice?category=${category}`
                : `/api/notice`;
            const response = await fetch(endpoint, {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const notices = await response.json();
                renderNotices(notices);
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || "공지사항을 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching notices:", error);
            noticeList.innerHTML = `<p id="errorMessage">${error.message}</p>`;
        }
    }

    // 공지사항 렌더링
    function renderNotices(notices) {
        noticeList.innerHTML = "";

        if (notices.length === 0) {
            noticeList.innerHTML = "<p>등록된 공지사항이 없습니다.</p>";
            return;
        }

        notices.forEach((notice) => {
            const listItem = document.createElement("li");

            listItem.innerHTML = `
                <h3>${notice.title}</h3>
                <p>${notice.content}</p>
                <small>카테고리: ${notice.category} | 조회수: ${notice.views}</small>
            `;

            noticeList.appendChild(listItem);
        });
    }

    // 카테고리 필터링 이벤트
    categoryFilter.addEventListener("change", () => {
        const selectedCategory = categoryFilter.value;
        fetchNotices(selectedCategory);
    });

    // 공지사항 작성 페이지 이동
    createNoticeButton.addEventListener("click", () => {
        window.location.href = "/createNotice.html";
    });

    // 공지사항 편집 페이지 이동
    editNoticeButton.addEventListener("click", () => {
        window.location.href = "/editNotice.html";
    });

    // 홈페이지로 이동
    goHomeButton.addEventListener("click", () => {
        window.location.href = "/home.html";
    });

    // 초기 공지사항 불러오기
    fetchNotices();
});
