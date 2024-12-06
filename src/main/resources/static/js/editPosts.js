// editPosts.js

document.addEventListener("DOMContentLoaded", () => {
    const postList = document.getElementById("editablePostList");
    const backToPostsButton = document.getElementById("backToPosts");
    const editModal = document.getElementById("editModal");
    const closeModalButton = document.querySelector(".close");
    const editForm = document.getElementById("editForm");
    const editTitleInput = document.getElementById("editTitle");
    const editContentInput = document.getElementById("editContent");

    let currentPostId = null;

    async function fetchEditablePosts() {
        try {
            const accessToken = sessionStorage.getItem("accessToken");
            if (!accessToken) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login.html";
                return;
            }

            const response = await fetch("/api/post/posts", {
                method: "GET",
                headers: {
                    Authorization: accessToken,
                },
            });

            if (response.ok) {
                const posts = await response.json();
                renderEditablePosts(posts);
            } else {
                alert("게시물을 불러오는 데 실패했습니다.");
            }
        } catch (error) {
            console.error("Error fetching posts:", error);
        }
    }

    function renderEditablePosts(posts) {
        postList.innerHTML = "";

        if (posts.length === 0) {
            postList.innerHTML = "<p>게시물이 없습니다.</p>";
            return;
        }

        posts.forEach(post => {
            const listItem = document.createElement("li");

            listItem.innerHTML = `
            <div>
                <h3>${post.title}</h3>
                <p>${post.content}</p>
                <a href="${post.url || "#"}" target="_blank">URL 이동</a>
            </div>
            <div class="button-container">
                <button class="editButton" data-id="${post.id}" data-title="${post.title}" data-content="${post.content}">수정</button>
                <button class="deleteButton" data-id="${post.id}">삭제</button>
            </div>
        `;
            postList.appendChild(listItem);
        });

        document.querySelectorAll(".editButton").forEach(button => {
            button.addEventListener("click", openEditModal);
        });

        document.querySelectorAll(".deleteButton").forEach(button => {
            button.addEventListener("click", handleDelete);
        });
    }


    function openEditModal(event) {
        currentPostId = event.target.dataset.id;
        editTitleInput.value = event.target.dataset.title;
        editContentInput.value = event.target.dataset.content;
        editModal.style.display = "block";
    }

    function closeEditModal() {
        editModal.style.display = "none";
        currentPostId = null;
    }

    async function saveChanges(event) {
        event.preventDefault();

        const newTitle = editTitleInput.value.trim();
        const newContent = editContentInput.value.trim();

        if (!newTitle || !newContent) {
            alert("제목과 내용을 입력해야 합니다.");
            return;
        }

        try {
            const response = await fetch(`/api/post/${currentPostId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: sessionStorage.getItem("accessToken"),
                },
                body: JSON.stringify({ title: newTitle, content: newContent }),
            });

            if (response.ok) {
                alert("게시물이 성공적으로 수정되었습니다.");
                closeEditModal();
                fetchEditablePosts();
            } else {
                alert("게시물 수정에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error editing post:", error);
        }
    }

    async function handleDelete(event) {
        const postId = event.target.dataset.id;

        const confirmDelete = confirm("정말 삭제하시겠습니까?");
        if (!confirmDelete) return;

        try {
            const response = await fetch(`/api/post/${postId}`, {
                method: "DELETE",
                headers: {
                    Authorization: sessionStorage.getItem("accessToken"),
                },
            });

            if (response.ok) {
                alert("게시물이 삭제되었습니다.");
                fetchEditablePosts();
            } else {
                alert("게시물 삭제에 실패했습니다.");
            }
        } catch (error) {
            console.error("Error deleting post:", error);
        }
    }

    closeModalButton.addEventListener("click", closeEditModal);

    if (backToPostsButton) {
        backToPostsButton.addEventListener("click", () => {
            window.location.href = "/posts.html";
        });
    }

    editForm.addEventListener("submit", saveChanges);

    fetchEditablePosts();
});
