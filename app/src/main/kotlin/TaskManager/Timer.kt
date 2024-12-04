package TaskManager

// Timer: Delayed Notify
import kotlinx.coroutines.*
import java.io.IOException
// Date time libraries
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class Timer {
  private var time = LocalTime.of(0, 0, 0)
  private var message = ""
  private var totalSeconds = 0L

  // secondary constructor to initialize private properties
  constructor(
    timeStr: String,
    msg: String,
  ) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    try {
      time = LocalTime.parse(timeStr, formatter)
    } catch (e: DateTimeParseException) {
      throw IllegalArgumentException("Invalid time format ${e.message}!")
    }

    message = msg
    totalSeconds = time.toSecondOfDay().toLong()
  }

  // executes before secondary constructor
  private var remainingSeconds = 0L

  /**
   * Run the program with async / kotlin coroutine support
   */
  fun runAsync() = runBlocking {
    remainingSeconds = totalSeconds

    // To print 00:00:00 first time
    time = LocalTime.ofSecondOfDay(0)

    // Launch a coroutine to repeatedly print a message every few seconds
    val job = async {
      repeatTask()
    }

    // Let it run for a specified amount of time, Seconds converted from time
    delay(totalSeconds*1000L)
    // Cancel the task after specified time above
    job.cancelAndJoin()

    time = LocalTime.ofSecondOfDay(totalSeconds)
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // 24-hour format
    val formattedTime = time.format(formatter)
    print("\rElapsed $formattedTime")

    val title = "Task Timer"
    sendNotification(title, message+" (" + time + ")")
  }

  private fun sendNotification(title: String, message: String) {
    try {
      // Construct and execute the notify-send command
      val processBuilder = ProcessBuilder(
        "notify-send", title, message
      )
      processBuilder.start()
    } catch (e: IOException) {
      e.printStackTrace()
      println("Failed to send notification.")
    }
  }

  /**
   * Repeat task on specified [interval] in seconds
   */
  private suspend fun repeatTask(interval: Int = 5) = coroutineScope {
    while (isActive) { // cancellable computation loop
      /**
       * Format the time, so it's fixed length to avoid prints like 01:20
       * instead of 00:01:20, otherwise linefeed is not enough to clear up
       * current line for printing like a timer.
       * 
       * Print time in custom format.
       */
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // 24-hour format
      val formattedTime = time.format(formatter)
      print("\rElapsed $formattedTime")

      if (remainingSeconds >= interval) {
        remainingSeconds -= interval
        val elapsedSeconds = totalSeconds - remainingSeconds
        time = LocalTime.ofSecondOfDay(elapsedSeconds)

        // Pause for i seconds
        delay(interval * 1000L) 
      }
      else {
        delay(remainingSeconds * 1000L)
        remainingSeconds = 0
      }
    }
  }

  // Test Helpers
  fun getMessage(): String {
    return message
  }

  fun getSeconds(): Long {
    return totalSeconds
  }  
}
