# mozilla-downloader
#### prototype for a downloader library having options to start, pause/resume, delete downloads alongwith methods like threading and scheduling downloads.
The library is intended to use `HttpURLConnection` in place of traditional `DownloadManager`. 
The `HttpURLConnection` will request the file from server and in case, the file is present in memory, the `range` property is used to download the file from
`present bytes - end`.
For cancelling downloads, `downloadtask.cancel(true)` is called.
For scheduling downloads, `AlarmManager` is used and for Notification Drawer, `NotificationManager` is used.

* The download class is extended to `AsyncTask`.
* `BatteryManager` is used to monitor the battery level. When the battery level goes beyond thresold, the download is paused. The battery level is checked in `doInBackground()`.
* For waking up alarms in doze mode, `setExactAndAllowWhileIdle()` will be used.
* For manipulating threads, `executeOnExecutor()` may be used.

##### Screenshots
[Download Cancelled](https://drive.google.com/open?id=1sqwbVVxcp9SYCpqIZB_Yja-CKt9snK5v)
[Download Started](https://drive.google.com/open?id=1f1g13j1YaKo_jKuF606jwc6ph8O3odo1)
