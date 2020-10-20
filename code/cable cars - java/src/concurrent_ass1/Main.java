package concurrent_ass1;

import java.util.concurrent.Semaphore;

public class Main {

	public static void main(String[] args) {
		int dailyLimit = 500;
		Semaphore atBottom = new Semaphore(0);
		Semaphore atTop = new Semaphore(50);
		Semaphore toLeave = new Semaphore(0);
		Semaphore capacity = new Semaphore(10);
		
		TouristsBottom tb = new TouristsBottom(atBottom, dailyLimit);
		TouristsTop tt = new TouristsTop(atTop, toLeave, dailyLimit);
		CableCar cc = new CableCar(capacity, atBottom, atTop, toLeave, dailyLimit);
		
		tb.start();
		tt.start();
		cc.start();
		
		try {
			tb.join();
			tt.join();
			cc.join();
		}
		catch (InterruptedException e) {}
		System.out.println("DONE");
	}

}
