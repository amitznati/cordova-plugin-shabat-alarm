
module.exports = {
    coolMethod: function (arg0, success, error) {
        cordova.exec(success, error, 'ShabatAlarms', 'coolMethod', [arg0]);
    }
}
