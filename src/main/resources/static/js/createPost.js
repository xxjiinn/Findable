document.getElementById("createPostForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();
    const url = document.getElementById("url").value.trim();

    const messageElement = document.getElementById("message");
    messageElement.textContent = "";

    const accessToken = sessionStorage.getItem("accessToken");
    if (!accessToken) {
        messageElement.textContent = "로그인이 필요합니다.";
        window.location.href = "/login.html";
        return;
    }

    try {
        const response = await fetch("/api/post/createPost", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: accessToken, // JWT만 전달
            },
            body: JSON.stringify({ title, content, url }), // userId 제거
        });

        if (response.ok) {
            messageElement.textContent = "게시물이 성공적으로 생성되었습니다!";
            messageElement.classList.add("success");
            setTimeout(() => {
                window.location.href = "/posts.html";
            }, 1000);
        } else if (response.status === 400) {
            const errorData = await response.json();
            messageElement.textContent = errorData.message || "입력 데이터를 확인하세요.";
        } else {
            messageElement.textContent = "게시물 생성에 실패했습니다.";
            console.error("⚠️ Error creating post:", await response.json());
        }
    } catch (error) {
        messageElement.textContent = "네트워크 오류가 발생했습니다. 다시 시도해주세요.";
        console.error("⚠️ Network error:", error);
    }
});
