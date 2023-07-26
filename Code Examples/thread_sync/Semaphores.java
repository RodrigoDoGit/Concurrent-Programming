import java.util.LinkedList;
public class Semaphores {
  public static void test(Semaphore s) throws InterruptedException {
    Thread a = new Thread(() -> { s.acquire(); s.acquire(); });
    Thread b = new Thread(() -> { s.release(); s.release(); });
    a.start(); b.start();
    a.join(); b.join();
    D.print(s.value());
  }
  public static void main(String[] args) throws InterruptedException {
    D.enable();
    test(new Semaphore1(0));
    test(new Semaphore2(0));
    test(new Semaphore3(0));
  }
}

interface Semaphore {
  void acquire(); 
  void release();
  int value();
}

class Semaphore1 implements Semaphore {
  private int count;
  Semaphore1(int initialValue) {
    assert(initialValue >= 0);
    this.count = initialValue; 
  }
  @Override 
  public synchronized int value() { return count; }

  @Override
  public void acquire() {
    D.print("acquire >>");
    while (true) {
      synchronized (this) {
        if (count > 0) { 
          count--;
          D.print("<< acquire");
          break;
        } 
        D.print("waiting");
      }
    }
  }

  @Override 
  public void release() {
    D.print("release >>");
    synchronized (this) {
      count++;
      D.print("<< release");
    }
  }
}

class Semaphore2 implements Semaphore {
  private int count;
  Semaphore2(int initialValue) {
    assert(initialValue > 0);
    this.count = initialValue; 
  }

  @Override 
  public synchronized int value() { return count; }

  @Override
  public void acquire() {
    D.print("acquire >>");
    synchronized (this) {
      while (count == 0) { 
        try { 
           D.print("waiting");
           wait(); 
        } 
        catch(InterruptedException e) { 
          // Ignore (similarly to acquireUninterruptibly in java.util.concurrent.Semaphore)
        } 
      } 
      count--;
      D.print("<< acquire");
    }
  }
  @Override 
  public void release() {
    D.print("release >>");
    synchronized (this) {
      count++;
      notify();
      D.print("<< release");
    }
  }
}
class Semaphore3 implements Semaphore {
  private int count;
  private LinkedList<Thread> waiting = new LinkedList<>();
  Semaphore3(int initialValue) {
    assert(initialValue > 0);
    this.count = initialValue; 
  }

  @Override 
  public synchronized int value() { return count; }

  @Override
  public void acquire() {
    D.print("acquire >>");
    Thread self = Thread.currentThread();
    synchronized (this) {
      waiting.addLast(self);
      while (count == 0 || waiting.getFirst() != self) { 
        D.print("waiting");
        try { 
           wait(); 
        } 
        catch(InterruptedException e) { 
          // Ignore (similarly to acquireUninterruptibly in java.util.concurrent.Semaphore)
        } 
      } 
      waiting.removeFirst();
      count--;
      D.print("<< acquire");
    }
  }
  @Override 
  public void release() {
    D.print("release >>");
    synchronized (this) {
      count++;
      notifyAll();
      D.print("<< release");
    }
  }
}

