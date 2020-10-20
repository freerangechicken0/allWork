package concurrent_ass1;

import java.util.concurrent.Semaphore;

public class TouristsTop extends Thread {
	Semaphore atTop;
	Semaphore toLeave;
	int dailyLimit;
	int total = 0;
	
	public void run() {
		while (total < dailyLimit) {
			if ((atTop.availablePermits() + toLeave.availablePermits()) < 50) {
				try {
					Thread.sleep((long) (Math.random() * 3500));
					touristLeave();
				} catch (InterruptedException e) {}
			}
		}
		while (toLeave.availablePermits() > 0) {
			continue;
			//this is to keep the thread alive while the last tourists are waiting to leave
		}
	}
	
	public TouristsTop(Semaphore atTop, Semaphore toLeave, int dailyLimit) {
		this.atTop = atTop;
		this.toLeave = toLeave;
		this.dailyLimit = dailyLimit;
	}
	
	public synchronized void touristLeave() {
		//sync is just for print
		System.out.println("A tourist decides to leave the mountain and goes to the summit sation");
		toLeave.release();
		total++;
	}
}
