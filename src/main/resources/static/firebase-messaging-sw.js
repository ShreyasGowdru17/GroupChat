importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

const firebaseConfig ={
     apiKey: "AIzaSyBocVSSFcidxYzbDJRSizThCm63eSB0enU",
     authDomain: "web-app-91b66.firebaseapp.com",
     projectId: "web-app-91b66",
     storageBucket: "web-app-91b66.firebasestorage.app",
     messagingSenderId: "961624681832",
      appId: "1:961624681832:web:0e61115d93a27f4aaafadd"
};

firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
  console.log('Background Message Received:', payload);

  const notificationTitle = payload?.notification?.title || 'New Message';
  const notificationOptions = {
    body: payload?.notification?.body || 'You have a new message',
    icon: '/icon-192x192.png',
    badge: '/badge-72x72.png',
    tag: 'chat-notification',
    data: payload?.data || {},
    actions: [
      {
        action: 'open',
        title: 'Open chat',
        icon: '/icon-192x192.png'
      },
      {
        action: 'close',
        title: 'Dismiss',
        icon: '/close-icon.png'
      }
    ]
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();

  const chatId = event.notification.data?.chatId;
  const targetUrl = chatId ? `/chat?chatId=${chatId}` : '/';

  event.waitUntil(clients.openWindow(targetUrl));
});
