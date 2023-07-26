//
// Utility class for debug printouts.
//
// Programação Concorrente (CC3037), DCC/FCUP
// Eduardo R. B. Marques
//
public class D {
  public static void print(Object o) {
    D.print("%s - %s", o.getClass().getSimpleName(), o.toString());
  }

  public static void print(String message, Object... args) {
    if (DEBUG) {
      synchronized (LOCK) {
        System.out.printf("[%s] ", Thread.currentThread().getName());
        System.out.printf(message, args);
        System.out.println();
      }
    }
  }

  public static void enable() {
    DEBUG = true;
  }
  public static void disable() {
    DEBUG = true;
  }
  public static void enableDeadlockDetection() {
    new DeadlockDetector().start();
  }
  private static boolean DEBUG = System.getProperty("debug") != null;
  private static final Object LOCK = new Object();
  private D() { }
  
}
