
import scala.concurrent.stm.japi.STM;

public class TestBlockingQueue {
  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<Integer> a  = new BlockingQueue<>(); 
    BlockingQueue<Integer> b  = new BlockingQueue<>(); 
    int N = 100;
    
    Thread t1 = new Thread(() -> {
      for (int i = 0; i < N; i++) {
        int v = i;
        STM.atomic(() -> {
          a.add(v);
          STM.afterCommit(() ->
          System.out.println("a: added " + v));
        });
      }
    });
    
    Thread t2 = new Thread(() -> {
      for (int i = 0; i < N; i++) {
        STM.atomic(() -> {
          int v = a.remove();
          b.add(v);
         // dump(); 
          /*
          STM.afterCommit(() -> {
            System.out.println("a: removed " + v);
            System.out.println("b: added " + v + "\n---");
          }); */
        }); 
      }
    });
    
    Thread t3 = new Thread(() -> {
      for (int i = 0; i < N; i++) {
        STM.atomic(() -> {
          int v = b.remove();
          b.dump();
          STM.afterCommit(() -> System.out.println("b: removed " + v + "\n---"));
        });
      } 
    });
    
    t1.start();
    t2.start();
    t3.start();
    t1.join();
    t2.join();
    t3.join();
  }
}
