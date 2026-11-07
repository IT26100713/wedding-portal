document.addEventListener('DOMContentLoaded', function () {
    const flashMessages = document.querySelectorAll('.flash-message');
    flashMessages.forEach(function (message) {
        setTimeout(function () {
            message.style.opacity = '0';
            message.style.transition = 'opacity 0.4s ease';
            setTimeout(function () {
                message.remove();
            }, 400);
        }, 4000);
    });
});
