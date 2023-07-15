
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Fibonacci extends RecursiveTask<Long>{
  final long n;
  
  Fibonacci(long n) { this.n = n; }
  
  @Override
  protected Long compute() {
     if (n <= 1L) return n;
     Fibonacci f1 = new Fibonacci(n - 1);
     Fibonacci f2 = new Fibonacci(n - 2);
     f1.fork(); f2.fork();
     return f2.join() + f1.join();
  }
  
  public static void main(String[] args) {
    try(Scanner in = new Scanner(System.in)) { 
      System.out.print("n ? ");
      long n = in.nextLong();
      ForkJoinPool fjp = new ForkJoinPool();
      long fib_n = fjp.invoke(new Fibonacci(n));
      System.out.printf("fib(%d) = %d%n", n, fib_n);
    }
  }
}
