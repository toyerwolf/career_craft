document.getElementById('bookDemoButton').addEventListener('click', async function() {
    const urlParams = new URLSearchParams(window.location.search);
    const skillIdsParam = urlParams.get('skillIds') || '1'; // Значение по умолчанию для skillIds
    const skillIds = skillIdsParam.split(',').map(id => parseInt(id, 10));
    const token = localStorage.getItem('accessToken');

    if (skillIds.length > 0 && token) {
        try {
            const response = await fetch(`/questions/first?skillIds=${skillIds.join(',')}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const skillQuestionResponse = await response.json();
            displayQuestions(skillQuestionResponse);
        } catch (error) {
            console.error('Error fetching questions:', error);
        }
    } else {
        console.error('Missing parameters or token');
    }
});

function displayQuestions(response) {
    const container = document.getElementById('questions-container');
    container.innerHTML = ''; // Очищаем контейнер перед добавлением новых данных

    response.skillsQuestions.forEach(skillQuestion => {
        const skillElement = document.createElement('div');
        skillElement.className = 'skill-item';

        // Заголовок для навыка
        const skillTitle = document.createElement('h3');
        skillTitle.textContent = skillQuestion.skillName;
        skillElement.appendChild(skillTitle);

        // Вопросы и ответы
        skillQuestion.questions.forEach(question => {
            const questionElement = document.createElement('div');
            questionElement.className = 'question-item';
            questionElement.innerHTML = `<p><strong>Q${question.id}: ${question.text}</strong></p>`;

            // Варианты ответов
            question.answers.forEach(answer => {
                const answerElement = document.createElement('div');
                answerElement.className = 'answer-item';
                answerElement.innerHTML = `<button data-question-id="${question.id}" data-answer-order-value="${answer.orderValue}">${answer.text} (Score: ${answer.orderValue})</button>`;
                questionElement.appendChild(answerElement);
            });

            skillElement.appendChild(questionElement);
        });

        container.appendChild(skillElement);
    });

    container.style.display = 'block'; // Показать контейнер с вопросами

    // Добавляем обработчик кликов на кнопки ответов
    container.addEventListener('click', async function(event) {
        if (event.target.tagName === 'BUTTON') {
            const questionId = event.target.getAttribute('data-question-id');
            const answerOrderValue = event.target.getAttribute('data-answer-order-value');
            await submitAnswer(questionId, answerOrderValue);
        }
    });
}

async function submitAnswer(questionId, answerOrderValue) {
    const token = localStorage.getItem('accessToken');
    const customerId = localStorage.getItem('customerId'); // Получаем customerId из localStorage

    if (!token || !customerId) {
        console.error('Missing access token or customer ID');
        return;
    }

    try {
        const response = await fetch('/user-answers/answerWithReports', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ customerId, questionId, answerOrderValue })
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const result = await response.json();
        console.log('Answer submitted:', result);
        // Вы можете обновить интерфейс в зависимости от ответа сервера
    } catch (error) {
        console.error('Error submitting answer:', error);
    }
}