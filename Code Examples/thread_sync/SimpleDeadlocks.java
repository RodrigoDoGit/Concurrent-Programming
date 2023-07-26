import java.util.concurrent.locks.ReentrantLock;

public class SimpleDeadlocks {
  public static void main(String[] args) {
    int example = args.length == 0 ? 1 : Integer.parseInt(args[0]);
    D.enable();
    D.enableDeadlockDetection();
    switch (example) {
      case 1: deadlock1(); break;
      case 2: deadlock2(); break;
      case 3: deadlock3(); break;
      default: System.out.println("Invalid choice");
    }
  }
  static void deadlock1() {
    ReentrantLock rl = new ReentrantLock();
    new Thread(() -> { 
       D.print("start");
       rl.lock(); 
       D.print("done");
    }).start();
    new Thread(() -> { 
       D.print("start");
       rl.lock(); 
       rl.unlock(); 
       D.print("done");
    }).start();
  }

  static void deadlock2() {
    Thread[] t = new Thread[2];
    t[0] = new Thread(
      () -> {
        try { 
          D.print("waiting for thread " + t[1].getName());
          t[1].join();
        } catch (InterruptedException e) { }
      }
    );
    t[1] = new Thread(
      () -> {
        try { 
          D.print("waiting for thread " + t[0].getName());
          t[0].join();
        } catch (InterruptedException e) { }
      }
    );
    t[0].start();
    t[1].start();
  }

  static void deadlock3() {
    Object a = new Object();
    Object b = new Object();
    Thread t1 = new Thread( () -> {
      D.print("acquiring a");
      synchronized(a) {
        pause(1000);
        D.print("acquiring b");
        synchronized(b) {
          D.print("critical section");
        }
      }
    });
    Thread t2 = new Thread( () -> {
      D.print("acquiring b");
      synchronized(b) {
        pause(1000);
        D.print("acquiring a");
        synchronized(a) {
          D.print("critical section");
        }
      }
    });

    t1.start(); t2.start();
  }


  // Just a simple wrapper for calling
  // Thread.sleep()
  static void pause(int ms) {
    try {
      Thread.sleep(ms);
    }
    catch(InterruptedException e) {
      throw new RuntimeException("Unexpected interrupt!");
    }
  }
}

