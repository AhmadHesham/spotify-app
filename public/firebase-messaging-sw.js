importScripts('https://www.gstatic.com/firebasejs/8.4.3/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.4.3/firebase-messaging.js');
firebase.initializeApp({
  apiKey: "AIzaSyBOHwXYFskyv3Ck_4y1q-0Xj07eaCoOynA",
  authDomain: "spotify-server-4e6f2.firebaseapp.com",
  databaseURL: "https://spotify-server-4e6f2-default-rtdb.europe-west1.firebasedatabase.app",
  projectId: "spotify-server-4e6f2",
  storageBucket: "spotify-server-4e6f2.appspot.com",
  messagingSenderId: "144439452035",
  appId: "1:144439452035:web:3072f27a06c33c05a856b6"
});

const messaging = firebase.messaging();
messaging.onBackgroundMessage((payload) => {
  console.log('[firebase-messaging-sw.js] Received background message ', payload);
});


// // See: https://github.com/microsoft/TypeScript/issues/14877
// /** @type {ServiceWorkerGlobalScope} */
// let self;

// function initInSw() {
//   console.log('In init');
//   // [START messaging_init_in_sw]
//   // Give the service worker access to Firebase Messaging.
//   // Note that you can only use Firebase Messaging here. Other Firebase libraries
//   // are not available in the service worker.
//   importScripts('https://www.gstatic.com/firebasejs/8.4.3/firebase-app.js');
//   importScripts('https://www.gstatic.com/firebasejs/8.4.3/firebase-messaging.js');

//   // Initialize the Firebase app in the service worker by passing in
//   // your app's Firebase config object.
//   // https://firebase.google.com/docs/web/setup#config-object
//   firebase.initializeApp({
//       apiKey: "AIzaSyBOHwXYFskyv3Ck_4y1q-0Xj07eaCoOynA",
//       authDomain: "spotify-server-4e6f2.firebaseapp.com",
//       databaseURL: "https://spotify-server-4e6f2-default-rtdb.europe-west1.firebasedatabase.app",
//       projectId: "spotify-server-4e6f2",
//       storageBucket: "spotify-server-4e6f2.appspot.com",
//       messagingSenderId: "144439452035",
//       appId: "1:144439452035:web:3072f27a06c33c05a856b6"
//   });

//   // Retrieve an instance of Firebase Messaging so that it can handle background
//   // messages.
//   const messaging = firebase.messaging();
//   // [END messaging_init_in_sw]
// }

// function onBackgroundMessage() {
//   const messaging = firebase.messaging();
//   // [START messaging_on_background_message]
//   messaging.onBackgroundMessage((payload) => {
//     console.log('[firebase-messaging-sw.js] Received background message ', payload);
//     // Customize notification here
//     const notificationTitle = 'Background Message Title';
//     const notificationOptions = {
//       body: 'Background Message body.',
//       icon: '/firebase-logo.png'
//     };

//     self.registration.showNotification(notificationTitle,
//       notificationOptions);
//   });
//   // [END messaging_on_background_message]
// }
