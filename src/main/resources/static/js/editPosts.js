// 사용자 인증 확인
async function fetchUserId() {
    const res = await fetch('/api/user/me', { credentials: 'include' });
    if (!res.ok) throw new Error('인증 필요');
    const { id } = await res.json();
    return id;
}

document.addEventListener("DOMContentLoaded", () => {
    const postList         = document.getElementById("editablePostList");
    const backToPostsBtn   = document.getElementById("backToPosts");
    const editModal        = document.getElementById("editModal");
    const closeModalBtn    = document.querySelector(".close");
    const editForm         = document.getElementById("editForm");
    const editTitleInput   = document.getElementById("editTitle");
    const editContentInput = document.getElementById("editContent");
    let currentPostId = null;

    // 게시물 가져오기 및 렌더링
    async function fetchEditablePosts() {
        try {
            await fetchUserId();
            const res = await fetch('/api/post/myPosts', {
                method: 'GET',
                credentials: 'include'
            });
            if (res.status === 401) throw new Error();
            const posts = await res.json();
            renderEditablePosts(posts);
        } catch {
            alert('인증이 필요합니다.');
            window.location.href = '/login.html';
        }
    }

    function renderEditablePosts(posts) {
        postList.innerHTML = '';
        if (posts.length === 0) {
            postList.innerHTML = '<p>수정 가능한 게시물이 없습니다.</p>';
            return;
        }
        posts.forEach(post => {
            const li = document.createElement('li');
            li.innerHTML = `
                <div>
                    <h3>${post.title}</h3>
                    <p>${post.content}</p>
                    <a href="${post.url||'#'}" target="_blank">URL 이동</a>
                </div>
                <div class="button-container">
                    <button class="editButton" data-id="${post.id}" data-title="${post.title}" data-content="${post.content}">수정</button>
                    <button class="deleteButton" data-id="${post.id}">삭제</button>
                </div>`;
            postList.appendChild(li);
        });
        document.querySelectorAll('.editButton').forEach(btn => btn.addEventListener('click', openEditModal));
        document.querySelectorAll('.deleteButton').forEach(btn => btn.addEventListener('click', handleDelete));
    }

    function openEditModal(e) {
        currentPostId = e.target.dataset.id;
        editTitleInput.value   = e.target.dataset.title;
        editContentInput.value = e.target.dataset.content;
        editModal.style.display = 'block';
    }
    function closeEditModal() {
        editModal.style.display = 'none';
        currentPostId = null;
    }

    // 수정 저장
    async function saveChanges(e) {
        e.preventDefault();
        const newTitle   = editTitleInput.value.trim();
        const newContent = editContentInput.value.trim();
        if (!newTitle || !newContent) {
            alert('제목과 내용을 입력해야 합니다.');
            return;
        }
        try {
            await fetchUserId();
            const res = await fetch(`/api/post/${currentPostId}`, {
                method: 'PATCH',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title: newTitle, content: newContent })
            });
            if (res.ok) {
                alert('게시물이 성공적으로 수정되었습니다.');
                closeEditModal();
                fetchEditablePosts();
            } else {
                alert('게시물 수정에 실패했습니다.');
            }
        } catch {
            alert('인증이 필요합니다.');
            window.location.href = '/login.html';
        }
    }

    // 삭제 처리
    async function handleDelete(e) {
        const postId = e.target.dataset.id;
        if (!confirm('정말 삭제하시겠습니까?')) return;
        try {
            await fetchUserId();
            const res = await fetch(`/api/post/${postId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            if (res.ok) {
                alert('게시물이 삭제되었습니다.');
                fetchEditablePosts();
            } else {
                alert('삭제에 실패했습니다.');
            }
        } catch {
            alert('인증이 필요합니다.');
            window.location.href = '/login.html';
        }
    }

    closeModalBtn.addEventListener('click', closeEditModal);
    backToPostsBtn.addEventListener('click', () => window.location.href = '/posts.html');
    editForm.addEventListener('submit', saveChanges);

    fetchEditablePosts();
});
