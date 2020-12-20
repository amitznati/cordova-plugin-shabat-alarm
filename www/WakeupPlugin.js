
module.exports = {
    cancelAlarm: function (arg0, success, error) {
        cordova.exec(success, error, 'WakeupPlugin', 'cancelAlarm', [arg0]);
    },
    setAlarm: function (arg0, success, error) {
        cordova.exec(success, error, 'WakeupPlugin', 'setAlarm', [arg0]);
    }
}
