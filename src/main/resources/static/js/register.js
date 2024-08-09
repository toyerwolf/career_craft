document.addEventListener('DOMContentLoaded', function () {
    const registerForm = document.getElementById('register-form');

    registerForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const name = document.getElementById('name').value;
        const surname = document.getElementById('surname').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const address = document.getElementById('address').value;

        fetch('/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                name: name,
                surname: surname,
                email: email,
                password: password,
                address: address
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Регистрация успешна
                    alert('Registration successful. Please check your email for confirmation.');
                    window.location.href = '/login'; // Перенаправление на страницу логина
                } else {
                    // Обработка ошибки
                    alert('Registration failed. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while trying to register.');
            });
    });
});