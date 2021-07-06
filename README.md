### Background worker

This is PoC to showcase what permissions application has to request in order keep background work going.

[Android defines](https://developer.android.com/guide/background) **immediate**, **deferred** and **exact** tasks.

For the **immediate** and _repeating with small delay_, the best thing is foreground service since it keeps
application alive and threading (scheduling) is up to developer.

One of the best tools for **deferred** scheduling is WorkManager since it hides all the complexity and
has wide Android version support.

[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) could handle **exact** scheduling (almost, because of flex time), but Android also provides
AlarmManager which gives more options to specify time and wake-up behavior.

### Required user interaction (grant extra/dangerous permissions)
However, aforementioned APIs are affected by a various Android's battery optimizations. Regular application
could only ask user to whitelist application in AutoStart manager and PowerSaver.

There are croud-sourced information about what activities should we launch in order to navigate
user to a corresponding Settings screen:
 - https://github.com/Tommys-code/PermissionHelper
 - https://stackoverflow.com/questions/44383983/how-to-programmatically-enable-auto-start-and-floating-window-permissions
 - https://github.com/dirkam/backgroundable-android 

**P.S.** privileged apps have access to the com.android.server.DeviceIdleController which also allows to
whitelist application from power saving
http://androidxref.com/9.0.0_r3/xref/frameworks/base/services/core/java/com/android/server/DeviceIdleController.java

### Testing
Application writes work output into logs
![image](https://user-images.githubusercontent.com/1898060/124553870-f9c8f480-de3d-11eb-8d56-1e61c14fcd7d.png)

Granting permissions on Xiaomi
https://user-images.githubusercontent.com/1898060/124554356-8bd0fd00-de3e-11eb-856e-b19a8019af02.mp4



