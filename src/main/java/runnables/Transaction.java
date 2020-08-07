package runnables;

public class Transaction {
  public static long counter = 0;

  public static void main(String[] args) throws Throwable {
    Runnable r = () -> {
      for (int i = 0; i < 10_000; i++) {
//        counter ++;
        synchronized (Transaction.class) {
          long tmp = counter;
          tmp = tmp + 1;
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          counter = tmp;
        }
      }
    };
    Thread t1 = new Thread(r);
    t1.start();
    Thread t2 = new Thread(r);
    t2.start();
    t1.join();
    t2.join();
    System.out.println("counter is now " + counter);
  }
}
