package runnables;

class MyJob implements Runnable {
  int i = 0;
  @Override
  public void run() {
    System.out.println(Thread.currentThread().getName() + " starting...");
    for (; i < 10; i++) {
      System.out.println(Thread.currentThread().getName() + " i is " + i);
    }
    System.out.println(Thread.currentThread().getName() + " ending...");
  }
}

public class Ex1 {
  public static void main(String[] args) throws Throwable {
    Runnable myjob = new MyJob();
    Thread t1 = new Thread(myjob);
    Thread t2 = new Thread(myjob);

//    t1.setDaemon(true);
    t1.start();
    t2.start();

    System.out.println("Thread launced, main exiting...");
//    System.out.println("Thread launced, main exiting...");
//    Thread.sleep(10);
//    System.out.println("Thread launced, main exiting...");
//    System.out.println("Thread launced, main exiting...");
//    Thread.sleep(10);
//    System.out.println("Thread launced, main exiting...");
//    System.out.println("Thread launced, main exiting...");
    // By default, threads are "non-daemon" -- probably don't use daemon thread
    // JVM shuts down when there are ZERO NON-DAEMON threads left alive.
  }
}
