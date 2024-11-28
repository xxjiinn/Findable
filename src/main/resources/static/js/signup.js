document.getElementById('signup-form').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    console.log('🟢 Sending JSON:', { name, email, password });

    // 회원가입 요청을 서버로 보냄
    fetch('/api/user/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, email, password }),
    })
        .then(response => {
            console.log('Server response: ', response.status);
            if (response.status === 201) {
                alert('User registered successfully');
                window.location.href = '/login.html';  // 회원가입 후 로그인 페이지로 이동
            } else {
                alert('Failed to sign up');
            }
        })
        .catch(error => console.error('❌ Error signing up: ', error));
});
