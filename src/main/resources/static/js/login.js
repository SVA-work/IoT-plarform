async function registerUser() {
    const login = document.getElementById('login').value;
    const telegramToken = document.getElementById('telegram_token').value;
    const password = document.getElementById('password').value;

    const userData = {
        login: login,
        telegramToken: telegramToken,
        password: password
    };

    try {
        const response = await fetch('http://localhost:8091/user/registration', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Ошибка регистрации');
        }

        const result = await response.json();
        console.log('Успешная регистрация:', result);
        localStorage.setItem('login', result.login);
        window.location.href = 'main_page.html';
    } catch (error) {
        alert('Ошибка регистрации: ' + error.message);
    }
}

async function signUpUser() {
    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    const userData = {
        login: login,
        password: password
    };

    try {
        const response = await fetch('http://localhost:8091/user/entry', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Ошибка входа: ' + response.status);
        }

        const result = await response.json();
        console.log('Успешный вход:', result);
        localStorage.setItem('login', result.login);
        window.location.href = 'main_page.html';
    } catch (error) {
        alert('Ошибка входа: ' + error.message);
    }
}