document.addEventListener("DOMContentLoaded", () => {
    const createFaqForm = document.getElementById("createFaqForm");
    const backToFaqButton = document.getElementById("backToFaqButton");
    const messageElement = document.getElementById("message");

    // FAQ 작성 처리
    createFaqForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const question = document.getElementById("question").value.trim();
        const answer = document.getElementById("answer").value.trim();
        const category = document.getElementById("category").value;

        messageElement.textContent = "";

        const accessToken = sessionStorage.getItem("accessToken");
        if (!accessToken) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login.html";
            return;
        }

        try {
            const response = await fetch("/api/faq/createFaq", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: accessToken,
                },
                body: JSON.stringify({ question, answer, category }),
            });

            if (response.ok) {
                messageElement.textContent = "FAQ가 성공적으로 생성되었습니다!";
                messageElement.classList.add("success");
                setTimeout(() => {
                    window.location.href = "/faq.html";
                }, 1000);
            } else if (response.status === 400) {
                const errorData = await response.json();
                messageElement.textContent = errorData.message || "입력 데이터를 확인하세요.";
            } else {
                throw new Error("FAQ 생성에 실패했습니다.");
            }
        } catch (error) {
            messageElement.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
            console.error("⚠️ Network error:", error);
        }
    });

    // FAQ 목록 페이지로 이동
    backToFaqButton.addEventListener("click", () => {
        window.location.href = "/faq.html";
    });
});
