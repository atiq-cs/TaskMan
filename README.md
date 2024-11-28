### Task Manager
Manage tasks and trigger notifications

**Supported platforms**

Platforms that runs kotlin

*However, for the GUI Notification it requires 'libnotify' which most Linux distros have.*
 

Trigger a notification after 5 seconds, no message passed along,
```bash
gradle run --args="00:00:05"
```

Trigger a notification after 10 minutes, message "Ergonomic switch" passed along,
```bash
gradle run --args="00:00:10 'Ergonomic switch'"
```

Screenshot looks like following,

![image](https://github.com/user-attachments/assets/b1d34de6-4649-4bc9-a09e-a17dfcbf0415)



On the terminal, it updates elapsed time every 5s,
```
> Task :app:run
Elapsed 00:00:05
<==========---> 83% EXECUTING [7s]
> :app:run
```

**Command Line Examples**

Run from another dir,
```bash
gradle run --project-dir ~/Code/kotlin/TaskMan --args="00:10:00 'Charge my device'"
```
