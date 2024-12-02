document.addEventListener('DOMContentLoaded', function () {
    // 로그인 처리
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const email = document.querySelector('input[name="email"]').value;
            const password = document.querySelector('input[name="password"]').value;

            try {
                const response = await fetch('/api/user/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password }),
                });

                if (!response.ok) {
                    throw new Error('Invalid login credentials');
                }

                const data = await response.json();
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                alert('Login successful');
                window.location.href = '/home.html';
            } catch (error) {
                alert(error.message || 'Login failed');
            }
        });
    }

    // 로그아웃 처리
    const logoutButton = document.getElementById('logout-btn');
    if (logoutButton) {
        logoutButton.addEventListener('click', async function () {
            const accessToken = localStorage.getItem('accessToken');
            const refreshToken = localStorage.getItem('refreshToken');

            if (!accessToken || !refreshToken) {
                alert('No tokens found for logout.');
                return;
            }

            try {
                // 서버 로그아웃 요청
                const response = await fetch('/api/auth/logout', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ accessToken, refreshToken }),
                });

                if (!response.ok) {
                    throw new Error('Server logout failed.');
                }
                console.log('Server logout successful');

                // Google 로그아웃 요청
                const googleLogoutUrl = `https://accounts.google.com/o/oauth2/revoke?token=${accessToken}`;
                const googleResponse = await fetch(googleLogoutUrl);
                if (googleResponse.ok) {
                    console.log('Google logout successful');
                } else {
                    console.warn('Google logout failed');
                }

                // 토큰 삭제 및 페이지 이동
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                alert('Logout successful!');
                window.location.href = '/login.html';
            } catch (error) {
                console.error('Error during logout:', error);
                alert(error.message || 'An error occurred during logout.');
            }
        });
    }
});
