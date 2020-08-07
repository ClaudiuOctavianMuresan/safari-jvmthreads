package badqueue;

public class BadQueue<E> {
  private E[] data = (E[])(new Object[10]);
  private int count = 0;

  public void put(E e) throws InterruptedException {
    synchronized (this) {
      // "Busy waiting" - generally bad (wastes CPU)
      while (count >= 10)
//        Thread.sleep(1); // sleeping??? Still hold key!!!
        this.wait(); // wait delays without CPU use, ALSO RELEASES AND RECLAIMS THE LOCK
      // wait "delays" resting head on the "this" as a pillow :)
      // when notified: move from waiting for notify to waiting for key..
      // then to "runnable" and eventually to running (when OS decides)
      // wakeup order of multiple waiters is RANDOM
      data[count++] = e; // Transactionally sensitive!!
//      this.notify(); // This can deadlock in multi-producer, multi-consumer situations
      this.notifyAll();
    }
  }

  public E take() throws InterruptedException {
    synchronized (this) {
      while (count <= 0)
        this.wait();
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
//      this.notify(); // shakes the pillow
      this.notifyAll(); // Architecturally awful -- scalability problem!!!
      return rv;
    }
  }

}
