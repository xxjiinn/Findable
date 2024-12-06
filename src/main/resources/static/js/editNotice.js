document.addEventListener("DOMContentLoaded", () => {
    const noticeList = document.getElementById("editableNoticeList");
    const editModal = document.getElementById("editModal");
    const closeModalButton = document.querySelector(".close");
    const editForm = document.getElementById("editForm");
    const editTitle = document.getElementById("editTitle");
    const editContent = document.getElementById("editContent");
    const editCategory = document.getElementById("editCategory");
    let currentNoticeId = null;

    // 모달 초기화
    editModal.style.display = "none"; // 초기 상태에서 숨김 처리

    // 공지사항 목록 가져오기
    async function fetchNotices() {
        try {
            let accessToken = sessionStorage.getItem("accessToken");

            // 토큰 형식 확인 및 공백 제거
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }
            accessToken = accessToken.trim(); // 공백 제거

            const response = await fetch("/api/notice", {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            // console.log(sessionStorage.getItem("accessToken"));

            if (response.ok) {
                const notices = await response.json();
                renderNotices(notices);
            } else {
                throw new Error("공지사항을 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching notices:", error);
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
                <div>
                    <h3>${notice.title}</h3>
                    <p>${notice.content}</p>
                    <small>카테고리: ${notice.category} | 조회수: ${notice.views}</small>
                </div>
                <div class="button-container">
                    <button class="editButton" data-id="${notice.id}" data-title="${notice.title}" data-content="${notice.content}" data-category="${notice.category}">수정</button>
                    <button class="deleteButton" data-id="${notice.id}">삭제</button>
                </div>
            `;

            listItem.querySelector(".editButton").addEventListener("click", openEditModal);
            listItem.querySelector(".deleteButton").addEventListener("click", handleDelete);

            noticeList.appendChild(listItem);
        });
    }

    // 모달 열기
    function openEditModal(event) {
        const button = event.target;
        currentNoticeId = button.dataset.id;
        editTitle.value = button.dataset.title;
        editContent.value = button.dataset.content;
        editCategory.value = button.dataset.category;
        editModal.style.display = "flex"; // 모달 보이기
    }

    // 모달 닫기
    function closeEditModal() {
        editModal.style.display = "none";
        currentNoticeId = null;
    }

    // 수정 내용 저장
    async function saveChanges(event) {
        event.preventDefault();

        const updatedNotice = {
            title: editTitle.value.trim(),
            content: editContent.value.trim(),
            category: editCategory.value,
        };

        if (!updatedNotice.title || !updatedNotice.content) {
            alert("모든 필드를 입력하세요.");
            return;
        }

        try {
            const response = await fetch(`/api/notice/${currentNoticeId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: sessionStorage.getItem("accessToken"),
                },
                body: JSON.stringify(updatedNotice),
            });

            if (response.ok) {
                alert("공지사항이 수정되었습니다.");
                closeEditModal();
                fetchNotices();
            } else {
                throw new Error("공지사항 수정에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error updating notice:", error);
        }
    }

    // 공지사항 삭제
    async function handleDelete(event) {
        const noticeId = event.target.dataset.id;

        if (!confirm("정말 삭제하시겠습니까?")) return;

        try {
            const response = await fetch(`/api/notice/${noticeId}`, {
                method: "DELETE",
                headers: {
                    Authorization: sessionStorage.getItem("accessToken"),
                },
            });

            if (response.ok) {
                alert("공지사항이 삭제되었습니다.");
                fetchNotices();
            } else {
                throw new Error("공지사항 삭제에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error deleting notice:", error);
        }
    }

    // 이벤트 리스너 연결
    closeModalButton.addEventListener("click", closeEditModal);
    editForm.addEventListener("submit", saveChanges);

    // 초기 데이터 가져오기
    fetchNotices();
});
