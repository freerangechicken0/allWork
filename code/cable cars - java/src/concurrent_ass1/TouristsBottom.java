package concurrent_ass1;

import java.util.concurrent.Semaphore;

public class TouristsBottom extends Thread {
	Semaphore atBottom;
	int dailyLimit;
	int total = 0;
	
	public void run() {
		while (total < dailyLimit) {
			try {
				Thread.sleep((long) (Math.random() * 2500));
				newTourist();
			} catch (InterruptedException e) {}
		}
		while (atBottom.availablePermits() > 0) {
			continue;
			//this is to keep the thread alive while the last tourists are waiting to go up
		}
	}
	
	public TouristsBottom(Semaphore atBottom, int dailyLimit) {
		this.atBottom = atBottom;
		this.dailyLimit = dailyLimit;
	}
	
	public synchronized void newTourist() {
		//sync is just for print statement to always line up, works fine when not sync'd
		System.out.println("A tourist arrives at the base station of the cable car.");
		atBottom.release();
		total++;
	}
}
