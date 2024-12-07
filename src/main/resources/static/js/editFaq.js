document.addEventListener("DOMContentLoaded", () => {
    const faqList = document.getElementById("editableFaqList");
    const editModal = document.getElementById("editModal");
    const closeModalButton = document.querySelector(".close");
    const editForm = document.getElementById("editForm");
    const editQuestion = document.getElementById("editQuestion");
    const editAnswer = document.getElementById("editAnswer");
    const editCategory = document.getElementById("editCategory");
    const backToFaqButton = document.getElementById("backToFaqButton");
    let currentFaqId = null;

    // 모달 초기 상태 설정
    editModal.style.display = "none";

    async function fetchFaqs() {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const response = await fetch("/api/faq", {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const faqs = await response.json();
                renderFaqs(faqs);
            } else {
                throw new Error("FAQ를 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching FAQs:", error);
        }
    }

    function renderFaqs(faqs) {
        faqList.innerHTML = "";

        if (faqs.length === 0) {
            faqList.innerHTML = "<p>등록된 FAQ가 없습니다.</p>";
            return;
        }

        faqs.forEach((faq) => {
            const listItem = document.createElement("li");

            listItem.innerHTML = `
                <div>
                    <h3>${faq.question}</h3>
                    <p>${faq.answer}</p>
                    <small>카테고리: ${faq.category}</small>
                </div>
                <div class="button-container">
                    <button class="editButton" data-id="${faq.id}" data-question="${faq.question}" data-answer="${faq.answer}" data-category="${faq.category}">수정</button>
                    <button class="deleteButton" data-id="${faq.id}">삭제</button>
                </div>
            `;

            listItem.querySelector(".editButton").addEventListener("click", openEditModal);
            listItem.querySelector(".deleteButton").addEventListener("click", handleDelete);

            faqList.appendChild(listItem);
        });
    }

    function openEditModal(event) {
        currentFaqId = event.target.dataset.id;
        editQuestion.value = event.target.dataset.question;
        editAnswer.value = event.target.dataset.answer;
        editCategory.value = event.target.dataset.category;
        editModal.style.display = "block";
    }

    function closeEditModal() {
        editModal.style.display = "none";
        currentFaqId = null;
    }

    async function saveChanges(event) {
        event.preventDefault();

        const updatedFaq = {
            question: editQuestion.value.trim(),
            answer: editAnswer.value.trim(),
            category: editCategory.value,
        };

        if (!updatedFaq.question || !updatedFaq.answer) {
            alert("모든 필드를 입력하세요.");
            return;
        }

        try {
            const response = await fetch(`/api/faq/${currentFaqId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: sessionStorage.getItem("accessToken"),
                },
                body: JSON.stringify(updatedFaq),
            });

            if (response.ok) {
                alert("FAQ가 수정되었습니다.");
                closeEditModal();
                fetchFaqs();
            } else {
                throw new Error("FAQ 수정에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error updating FAQ:", error);
        }
    }

    async function handleDelete(event) {
        const faqId = event.target.dataset.id;

        if (!confirm("정말 삭제하시겠습니까?")) return;

        try {
            const response = await fetch(`/api/faq/${faqId}`, {
                method: "DELETE",
                headers: {
                    Authorization: sessionStorage.getItem("accessToken"),
                },
            });

            if (response.status === 403) { // 권한 없음
                alert("이 FAQ를 삭제할 권한이 없습니다.");
                return;
            }

            if (response.ok) {
                alert("FAQ가 삭제되었습니다.");
                fetchFaqs();
            } else {
                throw new Error("FAQ 삭제에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error deleting FAQ:", error);
            alert(error.message || "FAQ 삭제 중 문제가 발생했습니다.");
        }
    }

    closeModalButton.addEventListener("click", closeEditModal);
    editForm.addEventListener("submit", saveChanges);
    backToFaqButton.addEventListener("click", () => {
        window.location.href = "/faq.html";
    });

    fetchFaqs();
});
