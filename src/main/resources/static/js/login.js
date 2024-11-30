document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('login-form').addEventListener('submit', function (event) {
        event.preventDefault();

        const email = document.querySelector('input[name="email"]').value;
        const password = document.querySelector('input[name="password"]').value;

        fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        })
            .then((response) =>
                response.ok
                    ? response.json()
                    : Promise.reject('Invalid login credentials')
            )
            .then((data) => {
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                alert('Login successful');
                window.location.href = '/home.html';
            })
            .catch((error) => alert(error));
    });
});
