
// chat.js
const username = localStorage.getItem('username'); // Retrieve the username from localStorage
const eventSource = new EventSource(`http://localhost:8088/user/${username}`);

document.getElementById('messageForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const message = document.getElementById('message').value;
    fetch('http://localhost:8088/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            from: username,
			to: 'General Group'
            message: message,
        }),
    });
});

eventSource.onerror = function (error) {
   console.log('connection state: ' + eventSource.readyState + ', error: ' + event);
};

eventSource.onmessage = function (event) {
	const data = JSON.parse(event.data);
    const chatBox = document.getElementById('chatBox');
    chatBox.innerHTML += `<p><strong>${data.from}:</strong> ${data.message}</p>`;
    chatBox.scrollTop = chatBox.scrollHeight;
}
;