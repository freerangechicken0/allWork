package concurrent_ass1;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CableCar extends Thread {
	Semaphore capacity;
	Semaphore atBottom;
	Semaphore atTop;
	Semaphore toLeave;
	int dailyLimit;
	int total = 0;
	
	//anything to do with capacity doesn't need synchronized or anything else because CableCar is the only thing that uses it.
	//atTop doesn't need it either because while TouristsTop looks at it, CableCar is the only thing the acquire/releases it.
	//toLeave and atBottom are used elsewhere so they use tryAcquire.

	public void run() {
		while (total < dailyLimit) {
			try {
				//picking up from bottom and dropping at top
				while (true) {
					if ((capacity.availablePermits() > 0) && ((atTop.availablePermits() - 10 + capacity.availablePermits()) > 0)) {
						if (atBottom.tryAcquire(1, 1000, TimeUnit.MILLISECONDS)) {
							capacity.acquire();
						}
						else {break;}
					}
					else {break;}
				}
				System.out.println("The cable car leaves with " + (10 - capacity.availablePermits()) + " passengers to the summit of the mountain");
				
				Thread.sleep(5000);
				
				//fine to use acquire because of the checks above, it will only need to acquire if there is enough to acquire.
				atTop.acquire(10 - capacity.availablePermits());
				capacity.release(10 - capacity.availablePermits());
				
				
				//picking up from top and dropping at bottom
				while (true) {
					if (capacity.availablePermits() > 0) {
						if (toLeave.tryAcquire(1, 1000, TimeUnit.MILLISECONDS)) {
							atTop.release();
							capacity.acquire();
						}
						else {break;}
					}
					else {break;}
				}
				System.out.println("The cable car leaves with " + (10 - capacity.availablePermits()) + " passengers to the foot of the mountain");
				
				Thread.sleep(5000);
				
				total += (10 - capacity.availablePermits());
				capacity.release(10 - capacity.availablePermits());
			} catch (InterruptedException e) {}
		}
	}
	
	public CableCar(Semaphore capacity, Semaphore atBottom, Semaphore atTop, Semaphore toLeave, int dailyLimit) {
		this.capacity = capacity;
		this.atBottom = atBottom;
		this.atTop = atTop;
		this.toLeave = toLeave;
		this.dailyLimit = dailyLimit;
	}
}
