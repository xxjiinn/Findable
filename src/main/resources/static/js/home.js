// 인증 상태 확인 및 환영 메시지 표시
window.addEventListener('DOMContentLoaded', async () => {
    const welcomeMessage = document.getElementById('welcomeMessage');
    const messageElement = document.getElementById('message');

    try {
        // 1) 쿠키 기반 인증 확인
        const response = await fetch('/api/user/me', {
            method: 'GET',
            credentials: 'include'
        });
        if (!response.ok) throw new Error();

        // 2) 사용자 정보로 환영 메시지 설정
        const user = await response.json();
        welcomeMessage.textContent = `환영합니다, ${user.name}님!`;

        // 3) 인증 후 공지사항 로드
        fetchNotices();
    } catch (error) {
        messageElement.textContent = '인증에 실패했습니다. 다시 로그인해주세요.';
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1500);
    }
});

// 공지사항 가져오기
async function fetchNotices() {
    const noticeList = document.getElementById('noticeList');
    const noticeErrorMessage = document.getElementById('noticeErrorMessage');
    noticeErrorMessage.textContent = '';

    try {
        const response = await fetch('/api/notice', {
            method: 'GET',
            credentials: 'include'
        });
        if (!response.ok) throw new Error();

        const notices = await response.json();
        noticeList.innerHTML = '';

        if (notices.length === 0) {
            noticeList.innerHTML = '<p>등록된 공지사항이 없습니다.</p>';
            return;
        }

        notices.forEach(notice => {
            const li = document.createElement('li');
            li.innerHTML = `
                <div>
                    <h4>${notice.title}</h4>
                    <p>${notice.content}</p>
                    <small>조회수: ${notice.views}</small>
                </div>
            `;
            noticeList.appendChild(li);
        });
    } catch (error) {
        console.error('공지사항 로드 오류:', error);
        noticeErrorMessage.textContent = '공지사항을 불러오는 데 실패했습니다.';
    }
}

// 로그아웃
document.getElementById('logoutBtn')?.addEventListener('click', async () => {
    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });
        if (response.ok) {
            window.location.href = '/login.html';
        } else {
            document.getElementById('message').textContent = '로그아웃에 실패했습니다. 다시 시도해주세요.';
        }
    } catch (error) {
        document.getElementById('message').textContent = '네트워크 오류가 발생했습니다.';
    }
});

// 페이지 내 버튼 이동
document.getElementById('goToPostsBtn')?.addEventListener('click', () => {
    window.location.href = '/posts.html';
});
document.getElementById('goToNoticesBtn')?.addEventListener('click', () => {
    window.location.href = '/notices.html';
});
document.getElementById('goToFaqBtn')?.addEventListener('click', () => {
    window.location.href = '/faq.html';
});
