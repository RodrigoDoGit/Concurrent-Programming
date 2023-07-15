import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue2<E> { // implements IBQueue<E> {
  private final E[] elements;
  private int size;
  private int head;
  private final ReentrantLock qlock;
  private final Condition notEmpty, notFull;

  @SuppressWarnings("unchecked")
  public BlockingQueue2(int capacity) {
    if (capacity <= 0) 
      throw new IllegalArgumentException("Invalid capacity: " + capacity);
    elements = (E[]) new Object[capacity];
    size = 0;
    head = 0;
    qlock = new ReentrantLock();
    notEmpty = qlock.newCondition();
    notFull = qlock.newCondition();
  }

  public int size() {
    try {
      qlock.lock();
      return size;
    }
    finally {
      qlock.unlock();
    }
  }
  
  public void add(E elem) throws InterruptedException {
    qlock.lock();
    try {
      while (size == elements.length) {
        notFull.await();
      }
      elements[(head + size) % elements.length] = elem;
      size++;
      notEmpty.signal();
    }
    finally {
      qlock.unlock();
    }
  }

  public E remove() throws InterruptedException {
    qlock.lock();
    try {
      while (size == 0) {
        notEmpty.await(); 
      }
      E elem = elements[head];
      head = (head + 1) % elements.length;
      size--;
      notFull.signal();
      return elem;
    }
    finally {
      qlock.unlock();
    }
  }
}
