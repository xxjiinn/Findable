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
        `;
        postsContainer.appendChild(postElement);
    });
}

// 게시물 생성 요청을 위한 함수
async function createPost(title, content) {
    const response = await fetch('/api/post/createPost', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ title, content, userId: 1 }) // userId는 실제 로그인한 사용자 ID로 교체 필요
    });

    if (response.ok) {
        loadPosts(); // 새 게시물 생성 후 목록 다시 불러오기
        document.getElementById('title').value = '';
        document.getElementById('content').value = '';
    } else {
        alert('게시물 생성에 실패했습니다.');
    }
}

// 폼 제출 시 게시물 생성 요청 처리
document.getElementById('createPostForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;
    createPost(title, content);
});

// 페이지 로드 시 게시물 목록 불러오기
window.addEventListener('load', loadPosts);
