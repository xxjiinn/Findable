document.addEventListener("DOMContentLoaded", () => {
    const postList = document.getElementById("postList");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const logoutButton = document.getElementById("logoutButton");

    // Fetch and display posts
    async function fetchPosts(query = "") {
        try {
            const token = document.cookie
                .split("; ")
                .find(row => row.startsWith("accessToken="))
                ?.split("=")[1];

            if (!token) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const response = await fetch(`/api/post/posts${query ? `?query=${query}` : ""}`, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const posts = await response.json();
                renderPosts(posts);
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
        document.cookie = "accessToken=; Max-Age=0; path=/;"; // Clear cookie
        document.cookie = "refreshToken=; Max-Age=0; path=/;"; // Clear cookie
        window.location.href = "/login.html";
    });

    // Initial fetch
    fetchPosts();
});
