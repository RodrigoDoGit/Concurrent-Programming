import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Max { 

  static final int ARRAY_SIZE=1024;
  static final int THREADS = 4;

  static class MaxTask extends RecursiveTask<Integer> {
     final int[] arr; 
     final int start;
     final int end;
     static final int SEQ_THRESHOLD = 100;
     MaxTask(int[] arr, int start, int end) {
       this.arr = arr;
       this.start = start;
       this.end = end;
     }
     @Override
     public Integer compute() {
       if (end - start <= SEQ_THRESHOLD) {
         int r = Integer.MIN_VALUE;
         for (int i = start; i < end; i++) r = Math.max(r, arr[i]);
         return r;
       }
       int m = (start + end) / 2;
       MaxTask t1 = new MaxTask(arr, start, m); 
       MaxTask t2 = new MaxTask(arr, m, end); 
       t1.fork(); 
       t2.fork();
       return Math.max(t2.join(), t1.join());
     }
  }
  public static void main(String[] args) throws Exception  {
    int[] arr = new int[ARRAY_SIZE];     
    Random rng = new Random(0); 
    for (int i = 0; i < ARRAY_SIZE; i++) arr[i] = rng.nextInt(99);
    ForkJoinPool fjp = new ForkJoinPool(THREADS);
    int r = fjp.invoke(new MaxTask(arr, 0, arr.length));
    System.out.println(r);
  }
}
