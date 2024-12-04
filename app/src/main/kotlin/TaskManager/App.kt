/**
 * Author: Atiq Rahman
 */

package TaskManager


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

  /**
   * Timer constructor is responsible for parsing and processing the CL
   * arguments
   * Create instance of class Timer and call runAsync()
   */
  Timer(args[0], if (args.size==2) args[1] else "Timer expires").runAsync()
}
