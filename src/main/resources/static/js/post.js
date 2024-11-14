let editingPostId = null; // 수정할 게시물 ID를 저장

// 게시물 목록을 불러오기 위한 함수
async function loadPosts() {
    const response = await fetch('/api/post');
    const posts = await response.json();

    const postsContainer = document.getElementById('posts');
    postsContainer.innerHTML = ''; // 기존 게시물 목록 초기화

    posts.forEach(post => {
        const postElement = document.createElement('div');
        postElement.classList.add('post-item');
        postElement.innerHTML = `
            <h3>${post.title}</h3>
            <p>${post.content}</p>
            <p><a href="${post.url}" target="_blank">${post.url}</a></p>
            <button onclick="openModal(${post.id}, '${post.title}', '${post.content}', '${post.url}')">수정</button>
            <button onclick="deletePost(${post.id})">삭제</button>
        `;
        postsContainer.appendChild(postElement);
    });
}

// 게시물 생성 요청을 위한 함수
async function createPost(title, content, url) {
    const response = await fetch('/api/post/createPost', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title, content, url, userId: 1 }) // url 추가
    });

    if (response.ok) {
        loadPosts();
        document.getElementById('title').value = '';
        document.getElementById('content').value = '';
        document.getElementById('url').value = '';
    } else {
        alert('⚠️게시물 생성에 실패했습니다.');
    }
}

// 모달 열기 함수
function openModal(id, currentTitle, currentContent, currentUrl) {
    editingPostId = id;
    document.getElementById('editTitle').value = currentTitle;
    document.getElementById('editContent').value = currentContent;
    document.getElementById('editUrl').value = currentUrl;

    document.getElementById('editModal').style.display = 'flex';
}

// 모달 닫기 함수
function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

// 수정된 게시물 제출 함수
async function submitEditPost() {
    const newTitle = document.getElementById('editTitle').value;
    const newContent = document.getElementById('editContent').value;
    const newUrl = document.getElementById('editUrl').value;

    const response = await fetch(`/api/post/${editingPostId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title: newTitle, content: newContent, url: newUrl })
    });

    if (response.ok) {
        alert('✅ 게시물이 수정되었습니다.');
        closeModal(); // 모달 닫기
        loadPosts();  // 목록 새로고침
    } else {
        alert('⚠️ 게시물 수정에 실패했습니다.');
    }
}

// 게시물 삭제 함수
async function deletePost(id) {
    const confirmDelete = confirm("정말로 삭제하시겠습니까?");
    if (confirmDelete) {
        const response = await fetch(`/api/post/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert('✅ 게시물이 삭제되었습니다.');
            loadPosts(); // 목록 새로고침
        } else {
            alert('⚠️ 게시물 삭제에 실패했습니다.');
        }
    }
}

// 폼 제출 시 이벤트에 url을 추가
document.getElementById('createPostForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    const url = document.getElementById('url').value; // url 가져오기
    createPost(title, content, url);
});

// 페이지 로드 시 게시물 목록 불러오기
window.addEventListener('load', loadPosts);
