// 사용자 정보(아이디) 가져오기
async function fetchUserId() {
    const res = await fetch('/api/user/me', { credentials: 'include' });
    if (!res.ok) throw new Error('인증이 필요합니다.');
    const user = await res.json();
    return user.id;
}

document.getElementById("createPostForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const title   = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();
    const url     = document.getElementById("url").value.trim();
    const message = document.getElementById("message");
    message.textContent = "";

    try {
        // 인증 확인 및 userId 획득
        const userId = await fetchUserId();

        // 게시물 생성 API 호출
        const response = await fetch(`/api/post/${userId}`, {
            method: "POST",
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title, content, url })
        });

        if (response.ok) {
            message.textContent = "게시물이 성공적으로 생성되었습니다!";
            message.classList.add("success");
            setTimeout(() => window.location.href = "/posts.html", 1000);
        } else if (response.status === 401) {
            // 인증 만료 시 로그인으로
            alert("로그인이 필요합니다.");
            window.location.href = "/login.html";
        } else {
            const err = await response.json().catch(() => null);
            message.textContent = err?.message || "게시물 생성에 실패했습니다.";
        }
    } catch (err) {
        alert(err.message || "네트워크 오류가 발생했습니다.");
        window.location.href = "/login.html";
    }
});
