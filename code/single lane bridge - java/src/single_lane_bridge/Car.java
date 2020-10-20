package single_lane_bridge;

public class Car extends Thread {
	String direction;
	Bridge bridge;
	
	public Car(String direction, Bridge bridge) {
		this.direction = direction;
		this.bridge = bridge;
	}
	
	public void run() {
		//enter and leave synchronized for print statements
		enter();
		
		//travels across the bridge
		try { Thread.sleep(2000); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		
		leave();
	}
	
	private synchronized void enter() {
		//try enter the bridge
		bridge.enter(direction);
		System.out.println(getName() + ", coming from the " + direction + ", is entering the bridge.");
	
	}
	
	private synchronized void leave() {
		//leaves the bridge
		System.out.println(getName() + ", coming from the " + direction + ", is leaving the bridge.");
		bridge.leave(direction);
	}
}
