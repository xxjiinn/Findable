document.addEventListener("DOMContentLoaded", () => {
    const postList = document.getElementById("postList");
    const searchInput = document.getElementById("searchInput");
    const searchButton = document.getElementById("searchButton");
    const createPostButton = document.getElementById("createPostButton");
    const editPostButton = document.getElementById("editPostButton");
    const homeButton = document.getElementById("homeButton"); // Added Home Button Reference

    // Fetch and display posts
    async function fetchPosts(query = "") {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const endpoint = query
                ? `/api/post/search?query=${encodeURIComponent(query)}`
                : `/api/post/posts`;
            const response = await fetch(endpoint, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${accessToken}`, // Fixed the Bearer token format
                },
            });

            if (response.ok) {
                const posts = await response.json();
                renderPosts(posts);
            } else if (response.status === 401) {
                await refreshAccessToken();
                fetchPosts(query); // Retry
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

        posts.forEach((post) => {
            const listItem = document.createElement("li");

            listItem.innerHTML = `
                <div>
                    <h3>${post.title}</h3>
                    <p>${post.content}</p>
                    <a href="${post.url || "#"}" target="_blank">URL 이동</a>
                </div>
            `;

            postList.appendChild(listItem);
        });
    }

    // Access Token 갱신
    async function refreshAccessToken() {
        try {
            const response = await fetch("/api/auth/refresh", {
                method: "POST",
                credentials: "include",
            });

            if (response.ok) {
                const data = await response.json();
                sessionStorage.setItem("accessToken", `Bearer ${data.accessToken}`); // Fixed the Bearer token format
            } else {
                throw new Error("Access Token 갱신 실패");
            }
        } catch (error) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login.html";
        }
    }

    // Event listeners
    if (searchButton) {
        searchButton.addEventListener("click", () => {
            const query = searchInput.value.trim();
            fetchPosts(query);
        });
    }

    if (createPostButton) {
        createPostButton.addEventListener("click", () => {
            window.location.href = "/createPost.html";
        });
    }

    if (editPostButton) {
        editPostButton.addEventListener("click", () => {
            window.location.href = "/editPosts.html";
        });
    }

    // Added Event Listener for Home Button
    if (homeButton) {
        homeButton.addEventListener("click", () => {
            window.location.href = "/index.html"; // Redirect to the homepage
        });
    }

    // Initial fetch
    fetchPosts();
});
