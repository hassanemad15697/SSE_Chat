document.getElementById('joinForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    // Store the username in localStorage
    localStorage.setItem('username', username);
    // Redirect to the chat page
    window.location.href = 'chat.html';
});