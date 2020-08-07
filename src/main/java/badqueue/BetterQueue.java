package badqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BetterQueue<E> {
  private E[] data = (E[]) (new Object[10]);
  private int count = 0;

  ReentrantLock rl = new ReentrantLock();
  Condition notFull = rl.newCondition();
  Condition notEmpty = rl.newCondition();

  public void put(E e) throws InterruptedException {
    rl.lock();
    try {
      while (count >= 10)
        notFull.await();
      data[count++] = e;
      notEmpty.signal();
    } finally {
      rl.unlock();
    }
  }

  public E take() throws InterruptedException {
    rl.lock();
    try {
      while (count <= 0)
        notEmpty.await(); // previously had wrong rendezvous point here
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      notFull.signal(); // previously had wrong rendezvous here
      return rv;
    } finally {
      rl.unlock();
    }
  }

  public static void main(String[] args) {
    final int COUNT = 1_000;
    final BetterQueue<int[]> queue = new BetterQueue<>();

    // producer
    new Thread(()->{
      System.out.println("Producer starting...");
      for (int i = 0; i < COUNT; i++) {
        try {
          int[] data = {i, 0}; // "transactionally sensitive"
          if (i < 100) {
            Thread.sleep(1); // leave the data "wrong" for a bit
            // also ensures queue is mostly empty for first 100 counts
          }
          // now transationally "good" (except in one case for testing the test:)
          if (i != 500) {
            data[1] = i;
          }
          queue.put(data);
          data = null; // MUST NOT REUSE data!!!
        } catch (InterruptedException ie) {
          // being asked to shutdown (won't happen!)...
          System.out.println("Shutdown producer requested (surprising!)");
          break; // skip out, incomplete
        }
      }
      System.out.println("Producer finished...");
    }).start();
    // consumer
    new Thread(()->{
      System.out.println("Consumer starting...");
      for (int i = 0; i < COUNT; i++) {
        try {
          int [] data = queue.take();
          // for last 100 counts, force the queue to be mostly full
          if (i > 900) Thread.sleep(1);
          if (data[0] != i || data[0] != data[1]) {
            System.out.println("**** ERROR DETECTED at index: "
                + i + " values: " + data[0] + ", " + data[1]);
            System.out.println("If it's at position 500, that's good :)");
          }
        } catch (InterruptedException ie) {
          // requested to shutdown (should not happen!)
          System.out.println("Consumer shutdown requested");
          break; // skip out, incomplete.
        }
      }
      System.out.println("Consumer finished...");
    }).start();
    System.out.println("Kicked off...");
  }
}
