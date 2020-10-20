package single_lane_bridge;

public class TrafficLights extends Thread {
	String direction;
	int duration;
	
	public TrafficLights(int duration) {
		direction = "west";
		this.duration = duration;
	}
	public TrafficLights(String direction, int duration) {
		this.direction = direction;
		this.duration = duration;
	}
	
	public void run() {
		//endlessly loop changing directions
		while (true) {
			try { Thread.sleep(duration); } 
			catch (InterruptedException e) { return; }
			
			changeDirection();
		}
	}
	
	private synchronized void changeDirection() {
		if (direction == "west") { direction = "east"; }
		else { direction = "west"; }
		
		System.out.println("Traffic Lights change to allow traffic from the " + direction);
	}
	
	public synchronized String getDirection() {
		return direction;
	}
}
