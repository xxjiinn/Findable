document.addEventListener("DOMContentLoaded", () => {
    const faqList = document.getElementById("faqList");
    const categoryFilter = document.getElementById("categoryFilter");
    const createFaqButton = document.getElementById("createFaqButton");
    const manageFaqButton = document.getElementById("manageFaqButton");
    const homeButton = document.getElementById("homeButton"); // 홈으로 이동 버튼
    const errorMessage = document.getElementById("errorMessage");

    async function fetchFaqs(category = "") {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const endpoint = category
                ? `/api/faq/category?category=${encodeURIComponent(category)}`
                : "/api/faq";

            const response = await fetch(endpoint, {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const faqs = await response.json();
                renderFaqs(faqs);
                errorMessage.textContent = ""; // 에러 메시지 초기화
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || "FAQ를 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching FAQs:", error);
            errorMessage.textContent = error.message || "FAQ 로드 중 문제가 발생했습니다.";
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
                <h3>${faq.question}</h3>
                <p>${faq.answer}</p>
                <small>카테고리: ${faq.category}</small>
            `;

            faqList.appendChild(listItem);
        });
    }

    // 카테고리 변경 이벤트 처리
    categoryFilter.addEventListener("change", () => {
        const selectedCategory = categoryFilter.value;
        fetchFaqs(selectedCategory); // 선택된 카테고리로 FAQ 다시 가져오기
    });

    // FAQ 작성 페이지 이동
    createFaqButton.addEventListener("click", () => {
        window.location.href = "/createFaq.html";
    });

    // FAQ 관리 페이지 이동
    manageFaqButton.addEventListener("click", () => {
        window.location.href = "/editFaq.html";
    });

    // 홈으로 이동
    homeButton.addEventListener("click", () => {
        window.location.href = "/home.html"; // 홈페이지 경로로 이동
    });

    // 초기 FAQ 로드
    fetchFaqs();
});
