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
        notFull.await();
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      notEmpty.signal();
      return rv;
    } finally {
      rl.unlock();
    }
  }
}
