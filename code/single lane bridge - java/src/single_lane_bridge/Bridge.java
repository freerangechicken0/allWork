package single_lane_bridge;

public class Bridge {
	static TrafficLights tl = new TrafficLights(5000);
	static volatile int eastCars = 5;
	static volatile int westCars = 5;

	public Bridge() {
		tl.start();
	}
	
	synchronized void enter(String direction) {
		while (true) {
			//if light is green
			while (direction.equals(tl.getDirection())) {
				//wait for other direction to be empty and for there to be room on bridge
				if ((direction == "west" && eastCars == 5 && westCars != 0) || 
					(direction == "east" && westCars == 5 && eastCars != 0)) {
					//take spot on bridge
					switch (direction) {
						case "west":
							westCars--;
							break;
						case "east":
							eastCars--;
							break;
					}
					notifyAll();
					return;
				}
				else {
					try { wait(); } 
					catch (InterruptedException e) { e.printStackTrace(); }
				}
			}
			//light red so wait then try again
			try { wait(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	synchronized void leave(String direction) {
		//leave
		switch (direction) {
			//release spot on bridge
			case "west":
				westCars++;
				break;
			case "east":
				eastCars++;
				break;
		}
		notifyAll();
	}
	
	public static void notifyCars() {
	}
	
	public void stopLights() {
		tl.interrupt();
	}
	
}
