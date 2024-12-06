document.getElementById("createNoticeForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();
    const category = document.getElementById("category").value;

    const messageElement = document.getElementById("message");
    messageElement.textContent = "";

    const accessToken = sessionStorage.getItem("accessToken");
    if (!accessToken) {
        messageElement.textContent = "로그인이 필요합니다.";
        window.location.href = "/login.html";
        return;
    }

    try {
        const response = await fetch("/api/notice/createNotice", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: accessToken,
            },
            body: JSON.stringify({ title, content, category }),
        });

        if (response.ok) {
            messageElement.textContent = "공지사항이 성공적으로 생성되었습니다!";
            messageElement.classList.add("success");
            setTimeout(() => {
                window.location.href = "/notices.html";
            }, 1000);
        } else if (response.status === 400) {
            const errorData = await response.json();
            messageElement.textContent = errorData.message || "입력 데이터를 확인하세요.";
        } else {
            messageElement.textContent = "공지사항 생성에 실패했습니다.";
            console.error("Error creating notice:", await response.json());
        }
    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
        console.error("Network error:", error);
    }
});
