package runnables;

public class Stopper {
  static volatile boolean stop = false;
  public static void main(String[] args) throws Throwable {
    new Thread(() -> {
      System.out.println("Worker thread started...");
      while (!stop)
        ;
      System.out.println("Worker thread exiting...");
    }).start();
    System.out.println("Worker lauched");
    Thread.sleep(1_000);
    System.out.println("Changing stop to true");
    stop = true;
    System.out.println("Flag set, main exiting...");
  }
}
