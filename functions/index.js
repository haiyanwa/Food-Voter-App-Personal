const functions = require('firebase-functions');
const admin     = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

let activityTracker          = 0;
// Update the date whenever a poll is created
exports.udateLastPollCreated = functions.database.ref("/polls/{id}")
    .onCreate(event => {
      activityTracker++;
      return admin.database().ref('activity_tracker').set(activityTracker);
    });

