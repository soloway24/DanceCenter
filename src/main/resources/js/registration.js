let usernameInput = document.getElementById("usernameInput");
let passwordInput = document.getElementById("passwordInput");
let repeatPasswordInput = document.getElementById("repeatPasswordInput");
let registerForm = document.getElementById("registerForm");

registerForm.onsubmit = function (e) {
    let username = usernameInput.value;
    if (!/\S/.test(username)) {
        alert("Username should not be blank.");
        e.preventDefault();
    }
    if(passwordInput.value !== repeatPasswordInput.value){
        alert("Passwords do not match.");
        e.preventDefault();
    }
}
