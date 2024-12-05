document.addEventListener("DOMContentLoaded", () => {
    const postList = document.getElementById("postList");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const logoutButton = document.getElementById("logoutButton");

    // Fetch and display posts
    async function fetchPosts(query = "") {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const response = await fetch(`/api/post/posts${query ? `?query=${query}` : ""}`, {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const posts = await response.json();
                renderPosts(posts);
            } else if (response.status === 401) {
                // Access Token 만료 시 갱신
                await refreshAccessToken();
                fetchPosts(query); // 재시도
            } else {
                alert("게시물을 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching posts:", error);
        }
    }

    // Render posts in the list
    function renderPosts(posts) {
        postList.innerHTML = "";

        if (posts.length === 0) {
            postList.innerHTML = "<p>게시물이 없습니다.</p>";
            return;
        }

        posts.forEach(post => {
            const listItem = document.createElement("li");
            listItem.innerHTML = `
                <h3>${post.title}</h3>
                <p>${post.content}</p>
                <a href="${post.url || "#"}">자세히 보기</a>
            `;
            postList.appendChild(listItem);
        });
    }

    // Search functionality
    searchButton.addEventListener("click", () => {
        const query = searchInput.value.trim();
        fetchPosts(query);
    });

    // Logout functionality
    logoutButton.addEventListener("click", () => {
        fetch("/api/auth/logout", {
            method: "POST",
            credentials: "include", // HttpOnly 쿠키 포함
        }).then(() => {
            sessionStorage.removeItem("accessToken");
            window.location.href = "/login.html";
        }).catch(error => {
            console.error("Error during logout:", error);
        });
    });

    // Access Token 갱신
    async function refreshAccessToken() {
        try {
            const response = await fetch("/api/auth/refresh", {
                method: "POST",
                credentials: "include", // HttpOnly 쿠키 포함
            });

            if (response.ok) {
                const data = await response.json();
                sessionStorage.setItem("accessToken", `Bearer ${data.accessToken}`);
            } else {
                throw new Error("Access Token 갱신 실패");
            }
        } catch (error) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login.html";
        }
    }

    // Initial fetch
    fetchPosts();
});
