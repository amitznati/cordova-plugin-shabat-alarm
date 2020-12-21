# cordova-plugin-shabat-alarm

Cordova plugin for setting one-time alarm that will active the alarm sound for selected period of time in seconds.


**Supported Platforms**

Android >= 6.0.0

# Install

    cordova plugin add https://github.com/amitznati/cordova-plugin-shabat-alarm.git


# Usage

**setAlarm**

    const options = {
	    time: {hour: 6, minute: 40}, // alarm time
	    seconds: 10, // alarm period
	    key: 1 // int, alarm key, reference for cancel
    }
    window.WakeupPlugin.setAlarm(
	    options, 
	    console.log, // success callback 
	    console.log // filed callback
     ); 
  
  
**cancelAlarm**

    const options = {
	    key: 1 // int, alarm key, reference from setAlarm
    }
    window.WakeupPlugin.cancelAlarm(
	    options, 
	    console.log, // success callback 
	    console.log // filed callback
     );
