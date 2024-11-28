/*
 * Author: Atiq Rahman
 */

package TaskManager

// Timer: Delayed Notify
import kotlinx.coroutines.*
import java.io.IOException
// Date time libraries
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class Timer(timeStr: String, msg: String) {
  private var time = LocalTime.of(0, 0, 0)
  // constructor for first param
  init {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    try {
      time = LocalTime.parse(timeStr, formatter)
    } catch (e: DateTimeParseException) {
      throw IllegalArgumentException("Invalid time format ${e.message}!")
    }
  }

  // constructor for second param
  private val purposeMsg = msg
  private var remainingSeconds = 0L
  private var totalSeconds = time.toSecondOfDay().toLong()


  fun runAsync() = runBlocking {
    remainingSeconds = totalSeconds

    // To print 00:00:00 first time
    time = LocalTime.ofSecondOfDay(0)

    // Launch a coroutine to repeatedly print a message every 5 Seconds
    val job = async {
      repeatTaskEvery5Seconds()
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
    sendNotification(title, purposeMsg+" (" + time + ")")
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

  private suspend fun repeatTaskEvery5Seconds() = coroutineScope {
    while (isActive) { // Ensures the coroutine checks for cancellation
      // Format the time, so it's fixed length to avoid prints like 01:20
      //  instead of 00:01:20, otherwise linefeed is not enough to clear up
      //  current line for printing like a timer

      // Define the custom format
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss") // 24-hour format
      val formattedTime = time.format(formatter)
      print("\rElapsed $formattedTime")

      if (remainingSeconds >= 5) {
        remainingSeconds -= 5
        val elapsedSeconds = totalSeconds - remainingSeconds
        time = LocalTime.ofSecondOfDay(elapsedSeconds)
        delay(5000L) // Pause for 5 seconds
      }
      else {
        delay(remainingSeconds * 1000L)
        remainingSeconds = 0
      }
    }
  }

  // Test Helpers
  fun getMessage(): String {
    return purposeMsg
  }

  fun getSeconds(): Long {
    return totalSeconds
  }  
}


fun main(args: Array<String>) {
  // Expecting one or two args
  if (args.size < 1) {
    println("Error: time argument is mandatory!")
    return
  }
  else if (args.size > 2) {
    println("Error: too many arguments!")
    return
  }

  var msg = "Timer expires"
  if (args.size==2)
    msg = args[1]

  // Timer constructor is responsible for parsing and processing the CL
  // arguments
  Timer(args[0], msg).runAsync()
}
