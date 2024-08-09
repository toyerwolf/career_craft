document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');

    loginForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.accessToken && data.refreshToken) {
                    // Сохраните токены в localStorage или куки
                    localStorage.setItem('accessToken', data.accessToken);
                    localStorage.setItem('refreshToken', data.refreshToken);
                    localStorage.setItem("customerId",data.customerId)
                    // Перенаправление на главную страницу или другую страницу
                    window.location.href = '/';
                } else {
                    // Обработка ошибки
                    alert('Login failed. Please check your credentials and try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while trying to log in.');
            });
    });
});