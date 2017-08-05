const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


const LOREM_IPSUM = 'Nullam hendrerit lobortis egestas. Donec dignissim posuere nulla.' +
    'Suspendisse posuere tristique nisl in tincidunt. ' +
    'Integer et purus ut odio interdum consectetur. Ut tincidunt in lectus at ultrices.';

// Send a Heads-Up Notification to all the invited voters whenever
// a poll is created
exports.messageVoters = functions.database.ref("/polls/{id}/voters")
    .onCreate(event => {
        const votersRef = event.data.ref;

        const payload = {
            data: {
                title: "Lorem Ipsum",
                body: LOREM_IPSUM
            }
        };

        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        };

        votersRef.once('value')
            .then(dataSnapshot => {

                dataSnapshot.forEach(child => {
                    const voter = child.val();
                    console.log("username: " + voter.username);
                    admin.messaging().sendToDevice(voter.token, payload, options);
                });
            })
    });