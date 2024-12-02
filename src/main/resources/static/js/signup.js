document.getElementById('signup-form').addEventListener('submit', async function (event) {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/api/user/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password }),
        });

        if (response.status === 201) {
            alert('User registered successfully');
            window.location.href = '/login.html';
        } else if (response.status === 400) {
            const errorData = await response.json();
            alert(errorData.message || 'Invalid input. Please check your details.');
        } else {
            alert('Failed to sign up. Try again later.');
        }
    } catch (error) {
        console.error('Error signing up:', error);
        alert('An unexpected error occurred. Please try again.');
    }
});
