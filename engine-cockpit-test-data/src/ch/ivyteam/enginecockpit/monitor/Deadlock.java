package ch.ivyteam.enginecockpit.monitor;

import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {

  private ReentrantLock lock = new ReentrantLock();
  private Deadlock other;

  public static void start() {
    var deadlock1 = new Deadlock();
    var deadlock2 = new Deadlock();
    deadlock1.other = deadlock2;
    deadlock2.other = deadlock1;
    new Thread(() -> deadlock1.monitor(0), "Deadlock-Monitor-1").start();
    new Thread(() -> deadlock2.monitor(0), "Deadlock-Monitor-2").start();
    new Thread(() -> deadlock1.reentrantLock(0), "Deadlock-ReentrantLock-1").start();
    new Thread(() -> deadlock2.reentrantLock(0), "Deadlock-ReentrantLock-2").start();
  }

  private synchronized void monitor(int count) {
    try {
      if (count == 0) {
        Thread.sleep(5000);
        other.monitor(++count);
      }
    } catch (InterruptedException e) {
    }
  }

  private void reentrantLock(int count) {
    lock.lock();
    try {
      if (count == 0) {
        Thread.sleep(5000);
        other.reentrantLock(++count);
      }
    } catch (InterruptedException ex) {
    } finally {
      lock.unlock();
    }
  }
}
