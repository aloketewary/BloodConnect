"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const functions = require("firebase-functions");
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);
exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(notifyEvent => {
    const user = notifyEvent.params.user_id;
    const notification = notifyEvent.params.notification_id;
    console.log('We have notification send to :', user);
    if (!notifyEvent.data.val()) {
        console.log('A Notification is about to delete', notification);
        return true;
    }
    const fromUser = admin.database().ref(`/notifications/${user}/${notification}`).once('value');
    return fromUser.then(fromUserResult => {
        const fromUserId = fromUserResult.val().from;
        console.log('You have new notification from', fromUserId);
        const userQuery = admin.database().ref(`/users/${fromUserId}/name`).once('value');
        const deviceToken = admin.database().ref(`/users/${user}/device_token`).once('value');
        return Promise.all([userQuery, deviceToken]).then(result => {
            const userName = result[0].val();
            const tokenId = result[1].val();
            const payload = {
                notification: {
                    title: 'New Friend Request',
                    body: `${userName} sent you a request`,
                    icon: 'default',
                    click_action: 'io.aloketewary.BloodConnect_TARGET_NOTIFICATION',
                },
                data: {
                    from_user_id: fromUserId
                }
            };
            return admin.messaging().sendToDevice(tokenId, payload).then(res => {
                console.log('This is notification feature');
                return 0;
            });
        });
    });
});
//# sourceMappingURL=index.js.map