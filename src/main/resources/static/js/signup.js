document.getElementById('signup-form').addEventListener('submit', function(event) {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('/api/user/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, email, password }),
    })
        .then(response => {
            if (response.status === 201) {
                alert('User registered successfully');
                window.location.href = '/login.html';
            } else if (response.status === 400) {
                alert('Invalid input. Please check your details.');
            } else {
                alert('Failed to sign up. Try again later.');
            }
        })
        .catch(error => console.error('Error signing up:', error));
});
