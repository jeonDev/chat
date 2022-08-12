const eventSource = new EventSource("http://localhost:8080/sender/ssar/receiver/cos");

eventSource.onmessage = (event) => {
    console.log(1, event);
    const data = JSON.parse(event.data);
    console.log(2, data);
    let date = new Date(data.createAt);
    let now = date.getHours() + ":" + date.getMinutes() + " | " + date.getMonth() + "/" + date.getDate();

    initMessage(data.msg, now);
}

function getSendMsgBox(msg, time) {
    return `<div class="sent_msg">
    <p>${msg}</p>
    <span class="time_date">${time}</span>
    </div>`;
}

function initMessage(msg, time) {
    let chatBox = document.querySelector("#chat-box");
    let msgInput = document.querySelector("#chat-outgoing-msg");
    let chatOutGoingBox = document.createElement("div");

    chatOutGoingBox.className = "outgoing_msg";
    chatOutGoingBox.innerHTML = getSendMsgBox(msg, time);

    chatBox.append(chatOutGoingBox);
    msgInput.value = "";
}

async function addMessage() {
    let chatBox = document.querySelector("#chat-box");
    let msgInput = document.querySelector("#chat-outgoing-msg");
    let chatOutGoingBox = document.createElement("div");

    let date = new Date();
    let now = date.getHours() + ":" + date.getMinutes() + " | " + date.getMonth() + "/" + date.getDate();

    let chat = {
        sender : "ssar",
        receiver : "cos",
        msg: msgInput.value
    };

    let response = await fetch("http://localhost:8080/chat", {
        method: "post",
        body : JSON.stringify(chat),
        headers : {
            "Content-Type" : "application/json;charset=utf-8"
        }
    });

    console.log(response);

    let parseResponse = await response.json();

    console.log(parseResponse);

    chatOutGoingBox.className = "outgoing_msg";
    chatOutGoingBox.innerHTML = getSendMsgBox(msgInput.value, now);

    chatBox.append(chatOutGoingBox);
    msgInput.value = "";
}

document.querySelector("#chat-outgoing-button").addEventListener("click", () => {
    addMessage();
});

document.querySelector("#chat-outgoing-msg").addEventListener("keydown", (e) => {
    if(e.keyCode == 13) {
        addMessage();
    }
});
