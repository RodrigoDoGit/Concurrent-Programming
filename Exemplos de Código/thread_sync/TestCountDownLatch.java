public class TestCountDownLatch {

  public static void main(String[] args) {
    int impl = args.length >= 1 ? 
      Integer.parseInt(args[0]) : 0;
    int n = args.length >= 2 ? 
      Integer.parseInt(args[1]) : 3;

    D.enable();
    D.enableDeadlockDetection();

    // Create the count down latch
    CountDownLatch cdl;
    switch (impl) {
      case 1: cdl = new CountDownLatch_Bug1(n); break;
      case 2: cdl = new CountDownLatch_Bug2(n); break;
      case 3: cdl = new CountDownLatch_Bug3(n); break;
      case 4: cdl = new CountDownLatch_Bug4(n); break;
      default:
              cdl = new CountDownLatch(n); break;
    }

    D.print("class = %s, n = %d", cdl.getClass().getName(), n);

    // Spawn n threads that will wait on the latch
    // and other n threads that trigger the count down
    for (int i = 0; i < n; i++) {
      Thread aw = new Thread(() -> { 
        try {
          cdl.await(); 
        } 
        catch(InterruptedException e) {
          throw new RuntimeException("Unexpected interrupt");
        }
      });
      aw.setName("AW" + i);
      aw.start(); 
      Thread cd = new Thread(() -> {
        cdl.countDown();
      });
      cd.setName("CD" + i);
      cd.start();
    }
  }
}
