document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('login-form').addEventListener('submit', function(event) {
        event.preventDefault();
        console.log("Login form submitted");  // 로그인 시도가 발생하면 콘솔에 출력

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        })
            .then(response => {
                console.log(response.status);  // 응답 상태 코드를 출력
                if (response.status === 200) {
                    alert('Login successful');
                    window.location.href = '/home.html';  // 로그인 성공 시 홈페이지로 이동
                } else {
                    alert('Invalid login credentials');
                }
            })
            .catch(error => console.error('Error logging in:', error));
    });
});
