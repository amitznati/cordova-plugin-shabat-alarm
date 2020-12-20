
module.exports = {
    // coolMethod: function (arg0, success, error) {
    //     cordova.exec(success, error, 'WakeupPlugin', 'coolMethod', [arg0]);
    // },
    setAlarm: function (arg0, success, error) {
        cordova.exec(success, error, 'WakeupPlugin', 'setAlarm', [arg0]);
    }
}
