package single_lane_bridge;

public class CreateCars extends Thread {
	int number;
	Bridge bridge;
	
	public CreateCars(int number, Bridge bridge) {
		this.number = number;
		this.bridge = bridge;
	}
	
	public void run() {
		Car[] cars = new Car[number];
		
		for (int i = 0; i < number; i++) {
			//wait a random length to separate cars
			try { Thread.sleep((long) (Math.random() * 1000)); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
			//pick direction for car
			String dir;
			if (Math.random() > 0.5) { dir = "west"; }
			else { dir = "east"; }
			
			//create and start car
			cars[i] = new Car(dir, bridge);
			cars[i].setName("Car " + i);
			cars[i].start();
		}
		
		//wait for all cars to finish and wake up any stuck waiting
		for (Car car : cars) {
			while (car.isAlive()) {
				synchronized(car) {
					car.notify();
				}
				try { Thread.sleep(1000); } 
				catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		bridge.stopLights();
		System.out.println("DONE");
	}
}
