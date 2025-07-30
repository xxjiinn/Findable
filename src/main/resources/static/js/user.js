// 사용자 정보 가져오기
async function fetchUserId() {
    const response = await fetch('/api/user/me', {
        credentials: 'include'   // HttpOnly 쿠키를 자동으로 전송해줌.
    });
    if (!response.ok) {
        throw new Error('유저 정보를 불러오는데 실패했습니다.');
    }
    const user = await response.json();
    return user.id;  // userId 반환
}

// 게시물 생성
document.getElementById('post-form')?.addEventListener('submit', async function(event) {
    event.preventDefault();

    const title = document.getElementById('post-title').value;
    const content = document.getElementById('post-content').value;

    try {
        const userId = await fetchUserId();
        const response = await fetch(`/api/post/${userId}`, {
            method: 'POST',
            credentials: 'include',     // 쿠키 전송
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ title, content }),
        });
        if (response.ok) {
            fetchPosts();
            document.getElementById('post-form').reset();
        } else if (response.status === 401) {
            alert('인증이 필요합니다. 다시 로그인해주세요.');
            window.location.href = '/login.html';
        } else {
            alert('게시물 생성에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error creating post:', error);
        alert(error.message);
    }
});

// 게시물 가져오기
async function fetchPosts() {
    try {
        const response = await fetch('/api/post', { credentials: 'include' });
        if (!response.ok) {
            if (response.status === 401) {
                alert('인증에 실패했습니다. 다시 로그인해주세요.');
                window.location.href = '/login.html';
                return;
            }
            throw new Error('게시물 가져오기 실패');
        }
        const posts = await response.json();
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
    } catch (error) {
        console.error('Error fetching posts:', error);
    }
}

// 공지사항 생성
document.getElementById('notice-form')?.addEventListener('submit', async function(event) {
    event.preventDefault();

    const title = document.getElementById('notice-title').value;
    const content = document.getElementById('notice-content').value;

    try {
        const response = await fetch('/api/notice/1', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ title, content }),
        });
        if (response.ok) {
            fetchNotices();
            document.getElementById('notice-form').reset();
        } else if (response.status === 401) {
            alert('인증이 필요합니다. 다시 로그인해주세요.');
            window.location.href = '/login.html';
        } else {
            alert('공지사항 생성에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error creating notice:', error);
    }
});

// 공지사항 가져오기
async function fetchNotices() {
    try {
        const response = await fetch('/api/notice', { credentials: 'include' });
        if (!response.ok) {
            if (response.status === 401) {
                alert('인증에 실패했습니다. 다시 로그인해주세요.');
                window.location.href = '/login.html';
                return;
            }
            throw new Error('공지사항 가져오기 실패');
        }
        const notices = await response.json();
        const noticeList = document.getElementById('notice-list');
        noticeList.innerHTML = '';
        notices.forEach(notice => {
            const li = document.createElement('li');
            li.textContent = `${notice.title} - ${notice.content}`;
            noticeList.appendChild(li);
        });
    } catch (error) {
        console.error('Error fetching notices:', error);
    }
}

// FAQ 생성
document.getElementById('faq-form')?.addEventListener('submit', async function(event) {
    event.preventDefault();

    const question = document.getElementById('faq-question').value;
    const answer = document.getElementById('faq-answer').value;

    try {
        const response = await fetch('/api/faq/1', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ question, answer }),
        });
        if (response.ok) {
            fetchFaqs();
            document.getElementById('faq-form').reset();
        } else if (response.status === 401) {
            alert('인증이 필요합니다. 다시 로그인해주세요.');
            window.location.href = '/login.html';
        } else {
            alert('FAQ 생성에 실패했습니다.');
        }
    } catch (error) {
        console.error('Error creating FAQ:', error);
    }
});

// FAQ 가져오기
async function fetchFaqs() {
    try {
        const response = await fetch('/api/faq', { credentials: 'include' });
        if (!response.ok) {
            if (response.status === 401) {
                alert('인증에 실패했습니다. 다시 로그인해주세요.');
                window.location.href = '/login.html';
                return;
            }
            throw new Error('FAQ 가져오기 실패');
        }
        const faqs = await response.json();
        const faqList = document.getElementById('faq-list');
        faqList.innerHTML = '';
        faqs.forEach(faq => {
            const li = document.createElement('li');
            li.textContent = `${faq.question} - ${faq.answer}`;
            faqList.appendChild(li);
        });
    } catch (error) {
        console.error('Error fetching FAQs:', error);
    }
}

// 페이지 로딩 시 인증 확인 후 데이터 가져오기
window.addEventListener('DOMContentLoaded', async () => {
    try {
        // 인증된 유저인지 확인
        await fetchUserId();
        // 인증 완료 후 데이터 로딩
        fetchPosts();
        fetchNotices();
        fetchFaqs();
    } catch (error) {
        alert('인증이 필요합니다. 다시 로그인해주세요.');
        window.location.href = '/login.html';
    }
});
