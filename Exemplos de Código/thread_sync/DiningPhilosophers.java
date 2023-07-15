//
// Dining philosophers example
//
// Programação Concorrente (CC3037), DCC/FCUP
// Eduardo R. B. Marques
//
public class DiningPhilosophers {
  /**
   * Test program.
   */
  public static void main(String[] args) {
    int numberOfPhilosophers = args.length > 0 ?
       Integer.parseInt(args[0]) : 3;
    D.enable();
    D.enableDeadlockDetection();
    Philosopher[] philosophers = create(numberOfPhilosophers);
    D.print("starting philosophers");
    for(Philosopher p : philosophers) {
      p.start();
    }    
  }



  /**
   * Create a number of philosophers in the classic 
   * circular table fashion.
   * @param n Number of philosophers.
   * @return An array of inactive philosopher threads (must be started).
  */
  static Philosopher[] create(int n) {
    Fork[] forks = new Fork[n];
    for (int i = 0; i < n; i++) {
      forks[i] = new Fork(i);
    }
    Philosopher[] philosophers = new Philosopher[n];
    for (int i = 0; i < n; i++) {
      Fork left = forks[i];
      Fork right = forks[(i+1) % n];
      philosophers[i] = new Philosopher(i, left, right);
    }
    return philosophers;
  }

  /**
   * Fork resource.
   */
  static class Fork { 
    final int id; 

    Fork(int id) {
      this.id = id;
    }

    @Override
    public String toString() {
      return "F" + id;
    }
  }

  /**
   * Philosopher thread.
   */
  static class Philosopher extends Thread {
    /** Left fork. */
    private final Fork leftFork;
    /** Right fork. */
    private final Fork rightFork;
    /** Flag indicating if philosopher has eaten. */
    private boolean hasEaten;

    /**
     * Constructor.
     * @param id Id for philosopher.
     * @param left Left fork.
     * @param right Right fork.
     */
    public Philosopher(int id, Fork left, Fork right) {
      super("P" + id);
      leftFork = left;
      rightFork = right;
      hasEaten = false;
    }

    @Override
    public void run() {
      D.print("started");
      synchronized(leftFork) {
        D.print("grabbed left fork " + leftFork);
        pause(1000);
        synchronized(rightFork) {
          D.print("grabbed right fork " + rightFork);
          hasEaten = true;
        }
        D.print("dropped right fork " + rightFork);
      }
      D.print("dropped left fork " + leftFork);
    }

    void pause(int ms) {
      try {
        Thread.sleep(ms);
      }
      catch(InterruptedException e) {
        throw new RuntimeException("Unexpected interrupt!");
      }
    }
  }
}
