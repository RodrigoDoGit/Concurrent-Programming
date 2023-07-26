import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<E> { // implements IBQueue<E> {

  private final E[] elements;
  private int size;
  private int head;

  @SuppressWarnings("unchecked")
  public BlockingQueue(int capacity) {
    if (capacity <= 0) 
      throw new IllegalArgumentException("Invalid capacity: " + capacity);
    elements = (E[]) new Object[capacity];
    size = 0;
    head = 0;
  }
  
  public synchronized int size() {
    return size;
  }

  public synchronized void add(E elem) throws InterruptedException {
    while (size == elements.length) {
      wait(); // queue is full
    }
    elements[(head + size) % elements.length] = elem;
    size++;
    notifyAll(); // notify() also works in this case
  }

  public synchronized E remove() throws InterruptedException {
    while (size == 0) {
      wait(); // queue is empty
    }
    E elem = elements[head];
    head = (head + 1) % elements.length;
    size--;
    notifyAll(); // notify() also works in this case
    return elem;
  }
}
