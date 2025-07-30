// 인증 확인: 유효한 세션 쿠키가 있는지 검사
async function fetchUserId() {
    const res = await fetch('/api/user/me', { credentials: 'include' });
    if (!res.ok) {
        throw new Error('인증 필요');
    }
    const data = await res.json();
    return data.id;
}

// 게시물 가져오기
async function fetchPosts(query = "") {
    const postList = document.getElementById("postList");
    const errorMessage = document.getElementById("errorMessage");
    errorMessage.textContent = "";

    try {
        // 인증 확인
        await fetchUserId();

        // 게시물 API 호출
        const endpoint = query
            ? `/api/post/search?query=${encodeURIComponent(query)}`
            : `/api/post/posts`;
        const response = await fetch(endpoint, {
            method: "GET",
            credentials: 'include'
        });

        if (response.ok) {
            const posts = await response.json();
            renderPosts(posts);
        } else if (response.status === 401) {
            // 인증 실패 시 바로 로그인
            alert("인증이 필요합니다. 다시 로그인해주세요.");
            window.location.href = "/login.html";
        } else {
            errorMessage.textContent = "게시물을 불러오는 데 실패했습니다.";
        }
    } catch (err) {
        alert("인증이 필요합니다. 다시 로그인해주세요.");
        window.location.href = "/login.html";
    }
}

// 게시물 렌더링
function renderPosts(posts) {
    const postList = document.getElementById("postList");
    postList.innerHTML = "";

    if (posts.length === 0) {
        postList.innerHTML = "<p>게시물이 없습니다.</p>";
        return;
    }

    posts.forEach(post => {
        const li = document.createElement("li");
        li.innerHTML = `
            <div>
                <h3>${post.title}</h3>
                <p>${post.content}</p>
                <a href="${post.url || '#'}" target="_blank">URL 이동</a>
            </div>
        `;
        postList.appendChild(li);
    });
}

// 버튼 이벤트 바인딩
document.addEventListener("DOMContentLoaded", () => {
    const searchInput       = document.getElementById("searchInput");
    const searchButton      = document.getElementById("searchButton");
    const createPostButton  = document.getElementById("createPostButton");
    const editPostButton    = document.getElementById("editPostButton");
    const homeButton        = document.getElementById("homeButton");

    searchButton?.addEventListener("click", () => {
        fetchPosts(searchInput.value.trim());
    });
    createPostButton?.addEventListener("click", () => {
        window.location.href = "/createPost.html";
    });
    editPostButton?.addEventListener("click", () => {
        window.location.href = "/editPosts.html";
    });
    homeButton?.addEventListener("click", () => {
        window.location.href = "/home.html";
    });

    // 초기 인증 및 게시물 로딩
    fetchPosts();
});
