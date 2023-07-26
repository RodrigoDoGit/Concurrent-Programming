import scala.concurrent.stm.japi.STM;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestAccount {

  public static void main(String[] args) throws Exception {
    concurrentTest();
    sequentialTest();
  }

  private static void concurrentTest() throws Exception {
    Account a = new Account("a", 100);
    Account b = new Account("b", 100);

    System.out.println("== INITIAL STATE ==");
    System.out.println(a);
    System.out.println(b);

    Thread t1 = new Thread(() -> {
      boolean done = Account.transfer(a, b, 100);
      System.out.printf("Result for t1: %s%n", done);
    });

    Thread t2 = new Thread(() -> {
      int v = STM.atomic(() -> a.balance() + b.balance());
      System.out.printf("Result for t2: %s%n", v);
    });
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println("== FINAL STATE ==");
    System.out.println(a);
    System.out.println(b);
  }

  private static void sequentialTest() {
    Account a = new Account("a", 100);
    Account b = new Account("b", 100);
    
    System.out.println("== INITIALLY ==");
    System.out.println(a);
    System.out.println(b);
    
    // Transfer ok
    Account.transfer(a, b, 100);
    System.out.println("== AFTER TRANSFER 1 OF 100 FROM a to b ==");
    System.out.println(a);
    System.out.println(b);

    // Transfer will not be done
    Account.transfer(a, b, 100);

    System.out.println("== AFTER TRANSFER 2 OF 100 FROM a to b ==");
    System.out.println(a);
    System.out.println(b);
  }

}
