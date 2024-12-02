document.addEventListener('DOMContentLoaded', function () {
    const accessToken = localStorage.getItem('accessToken');
    const postList = document.getElementById('post-list');

    if (!accessToken) {
        alert('You must log in to view posts.');
        window.location.href = '/login.html';
        return;
    }

    // 게시물 로드 함수
    async function loadPosts(apiUrl) {
        try {
            const response = await fetch(apiUrl, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json',
                },
            });

            if (response.status === 401) {
                alert('Unauthorized access. Please log in.');
                window.location.href = '/login.html';
                return;
            } else if (response.status === 403) {
                alert('Forbidden: You do not have permission to view this content.');
                return;
            }

            const posts = await response.json();
            postList.innerHTML = '';
            posts.forEach(post => {
                const li = document.createElement('li');
                li.innerHTML = `<h3>${post.title}</h3><p>${post.content}</p>`;
                postList.appendChild(li);
            });
        } catch (error) {
            console.error('Error loading posts:', error);
        }
    }

    // 모든 게시물 조회
    document.getElementById('all-posts-btn').addEventListener('click', () => {
        loadPosts('/api/post');
    });

    // 내 게시물 조회
    document.getElementById('my-posts-btn').addEventListener('click', () => {
        const userId = localStorage.getItem('userId');
        loadPosts(`/api/post/myPosts/${userId}`);
    });

    // 초기 게시물 로드
    loadPosts('/api/post');
});


document.getElementById('post-form').addEventListener('submit', async function (event) {
    event.preventDefault();

    const title = document.getElementById('post-title').value;
    const content = document.getElementById('post-content').value;
    const userId = localStorage.getItem('userId');

    try {
        const response = await fetch('/api/post/createPost', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ title, content, userId }),
        });

        if (response.status === 401) {
            alert('Unauthorized: Please log in.');
            window.location.href = '/login.html';
            return;
        } else if (response.status === 403) {
            alert('Forbidden: You do not have permission to create posts.');
            return;
        } else if (!response.ok) {
            throw new Error('Failed to create post');
        }

        alert('Post created successfully');
        document.getElementById('post-form').reset();
        loadPosts('/api/post');
    } catch (error) {
        console.error('Error creating post:', error);
        alert('An error occurred while creating the post.');
    }
});
