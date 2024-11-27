// 사용자 정보 가져오기
function fetchUserId() {
    return fetch('/api/user/me', {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}` // JWT 토큰 전송
        }
    })
        .then(response => response.json())
        .then(user => user.id)  // userId 반환
        .catch(error => console.error('Error fetching user ID:', error));
}

// 게시물 생성
document.getElementById('post-form')?.addEventListener('submit', function(event) {
    event.preventDefault();

    const title = document.getElementById('post-title').value;
    const content = document.getElementById('post-content').value;

    fetchUserId().then(userId => {
        fetch(`/api/post/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}` // JWT 토큰 추가
            },
            body: JSON.stringify({ title, content }),
        })
            .then(response => {
                if (response.status === 201) {
                    fetchPosts();
                    document.getElementById('post-form').reset();
                } else {
                    alert('Failed to create post.');
                }
            })
            .catch(error => console.error('Error creating post:', error));
    });
});


// 게시물 가져오기
function fetchPosts() {
    fetch('/api/post')
        .then(response => response.json())
        .then(posts => {
            const postList = document.getElementById('post-list');
            postList.innerHTML = '';
            posts.forEach(post => {
                const li = document.createElement('li');
                const title = document.createElement('h3');
                const content = document.createElement('p');

                title.textContent = `Title: ${post.title}`;
                content.textContent = `Content: ${post.content}`;

                li.appendChild(title);
                li.appendChild(content);
                postList.appendChild(li);
            });
        })
        .catch(error => console.error('Error fetching posts:', error));
}

// 공지사항 생성
document.getElementById('notice-form')?.addEventListener('submit', function(event) {
    event.preventDefault();

    const title = document.getElementById('notice-title').value;
    const content = document.getElementById('notice-content').value;

    fetch('/api/notice/1', {  // userId를 1로 임시 설정
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ title, content }),
    })
        .then(response => {
            if (response.status === 201) {
                fetchNotices();
                document.getElementById('notice-form').reset();
            } else {
                alert('Failed to create notice.');
            }
        })
        .catch(error => console.error('Error creating notice:', error));
});

// 공지사항 가져오기
function fetchNotices() {
    fetch('/api/notice')
        .then(response => response.json())
        .then(notices => {
            const noticeList = document.getElementById('notice-list');
            noticeList.innerHTML = '';
            notices.forEach(notice => {
                const li = document.createElement('li');
                li.textContent = `${notice.title} - ${notice.content}`;
                noticeList.appendChild(li);
            });
        })
        .catch(error => console.error('Error fetching notices:', error));
}

// FAQ 생성
document.getElementById('faq-form')?.addEventListener('submit', function(event) {
    event.preventDefault();

    const question = document.getElementById('faq-question').value;
    const answer = document.getElementById('faq-answer').value;

    fetch('/api/faq/1', {  // userId를 1로 임시 설정
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ question, answer }),
    })
        .then(response => {
            if (response.status === 201) {
                fetchFaqs();
                document.getElementById('faq-form').reset();
            } else {
                alert('Failed to create FAQ.');
            }
        })
        .catch(error => console.error('Error creating FAQ:', error));
});

// FAQ 가져오기
function fetchFaqs() {
    fetch('/api/faq')
        .then(response => response.json())
        .then(faqs => {
            const faqList = document.getElementById('faq-list');
            faqList.innerHTML = '';
            faqs.forEach(faq => {
                const li = document.createElement('li');
                li.textContent = `${faq.question} - ${faq.answer}`;
                faqList.appendChild(li);
            });
        })
        .catch(error => console.error('Error fetching FAQs:', error));
}

// 페이지 로딩 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', () => {
    fetchPosts();
    fetchNotices();
    fetchFaqs();
});
