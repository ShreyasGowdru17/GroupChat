'use strict';

import { initializeApp } from 'https://www.gstatic.com/firebasejs/9.0.0/firebase-app.js';
import {
  getMessaging,
  getToken,
  onMessage,
} from 'https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging.js';


const firebaseConfig ={
                           apiKey: "AIzaSyBocVSSFcidxYzbDJRSizThCm63eSB0enU",
                           authDomain: "web-app-91b66.firebaseapp.com",
                           projectId: "web-app-91b66",
                           storageBucket: "web-app-91b66.firebasestorage.app",
                           messagingSenderId: "961624681832",
                           appId: "1:961624681832:web:0e61115d93a27f4aaafadd"
                      };

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

async function initializeFCM() {
  try {
    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      console.warn("Notification permission not granted.");
      return;
    }

    const currentToken = await getToken(messaging, {
      vapidKey:"BDBCCjtCrtOKjaVBeP0PrGTOXXVoUV-g8dRciJHvgTe-G6ZiQYPszgXBky3-SDcWG5CWh52RRGbwXVb132LJUf4"
    });

    if (currentToken) {
      console.log("FCM Token:", currentToken);
    } else {
      console.warn("Failed to get FCM token");
    }
  } catch (err) {
    console.error("Error initializing FCM:", err);
  }
}

function showNotification(title, body) {
  const sound = new Audio("/Notification.mp3");
  sound.play().catch(console.error);

  if (Notification.permission === 'granted') {
    new Notification(title, {
      body: body,
      icon: '/icon-192x192.png',
      badge: '/badge-72x72.png'
    });
  }
}
function showToast(title, body) {
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML = `
    <div style="
      font-weight: 600;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 8px;
    ">
    <span>${title}</span>
    </div>

  `;

  Object.assign(toast.style, {
    position: 'fixed',
    bottom: '20px',
    right: '20px',
    background:'green',
    color: '#fff',
    padding: '14px 18px',
    borderRadius: '10px',
    boxShadow: '0 6px 16px rgba(0, 0, 0, 0.25)',
    fontFamily:'Poppins',
    fontSize: '16px',
    maxWidth: '520px',
    zIndex: '9999',
    opacity: '0',
    transition: 'opacity 0.4s ease-in-out',
  });

  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '1';
  }, 100);


  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 400);
  }, 4000);
}

onMessage(messaging, (payload) => {
  console.log("Foreground message:", payload);
  const { title, body } = payload.notification;
  showNotification(title, body);
  showToast(title,body);
});


initializeFCM();

let fcmToken=null;

var usernamePage=document.querySelector("#username-page");
var chatPage=document.querySelector("#chat-page");
var userNameForm=document.querySelector("#usernameForm");
var messageForm=document.querySelector("#messageForm");
var messageInput=document.querySelector("#message");
var messageArea=document.querySelector("#messageArea");
var connectingElement=document.querySelector(".connecting");

var stompClient=null;
var username=null;
var colors=[
     '#2196F3', '#32c787', '#00BCD4', '#ff5652',
     '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];



function playNotificationSound() {
    try {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        oscillator.frequency.value = 800; // Creates an 800Hz beep
        oscillator.type = 'sine';

        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.5);
    } catch (e) {
        console.log('Could not play sound');
    }
}
function connect(event){
    username=document.querySelector('#name').value.trim();
    if(username){
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket=new SockJS('/ws');
        stompClient=Stomp.over(socket);

        stompClient.connect({},onConnected,onError);
    }
    event.preventDefault();
}

function onConnected(){
 stompClient.subscribe("/topic/public",onMessageReceived);

 stompClient.send('/app/chat.addUser',{},JSON.stringify({sender:username,content:fcmToken,type:'JOIN'}));

 connectingElement.classList.add('hidden');

 updateOnlineUsers();
}

function onError(){
    connectingElement.textContent='Could not connect to Websocket server.Please refresh your browser and try again';
    connectingElement.style.color='red';
}

function onMessageReceived(payload){
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
        if(message.sender !== username){
                            playNotificationSound();

                            if (document.hidden) {
                                new Notification(message.sender, {
                                    body: message.content,
                                    icon: '/icon-192x192.png'
                                });
                            } else {
                                showToast(`${message.sender}: ${message.content}`);
                       }
         }
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
        if(message.sender !== username){
                    playNotificationSound();

                    if (document.hidden) {
                        new Notification(message.sender, {
                            body: message.content,
                            icon: '/icon-192x192.png'
                        });
                    } else {
                        showToast(`${message.sender}: ${message.content}`);
                    }
        }
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);


        if(message.sender !== username){
            playNotificationSound();

            if (document.hidden) {
                new Notification(message.sender, {
                    body: message.content,
                    icon: '/icon-192x192.png'
                });
            } else {
                showToast(`${message.sender}: ${message.content}`);
            }
        }

    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

}

function sendMessage(event){

    var messageContent=messageInput.value.trim();
    if(messageContent && stompClient){
        var chatMessage={
            sender:username,
            content:messageContent,
            type:'CHAT'
        }
        stompClient.send('/app/chat.sendMessage',{},JSON.stringify(chatMessage));
        messageInput.value='';
    }
    event.preventDefault();

}
function getAvatarColor(sender) {
    const index = Math.floor(Math.random() * colors.length);
    return colors[index];
}

function addUserToOnlineList(user){
    if(onlineUsersElement && user!==username){
        var userElement=document.createElement('div');
        userElement.classList.add('online-user');
        userElement.setAttribute('data-username',user);
        userElement.innerHTML=`
            <span class="online-indicator"></span>
            <span class="username">${user}</span>
        `;
        onlineUsersElement.appendChild(userElement);
    }
}

function removeUserFromOnlineList(user){
    if(onlineUsersElement){
        var userElement=onlineUsersElement.querySelector(`[data-username="${user}"]`);
        if(userElement){
            userElement.remove();
        }
    }
}

function updateOnlineUsers(){
}

document.addEventListener('visibilitychange',function(){
    if(document.hidden){
        console.log('Page is now hidden');
    }else{
        console.log('Page is now visible');
        if(fcmToken && stompClient && stompClient.connected){
            stompClient.send('/app/chat.updateToken',{},JSON.stringify({
                sender:username,
                content:fcmToken,
                type:'TOKEN_UPDATE'
            }));
        }
    }
});

window.addEventListener('load',initializeFCM);


usernameForm.addEventListener('submit',connect,true);
messageForm.addEventListener('submit',sendMessage,true);

