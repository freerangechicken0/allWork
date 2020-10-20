package single_lane_bridge;

public class Main {
	
	public static void main(String[] args) {
		Bridge bridge = new Bridge();
		CreateCars cc = new CreateCars(100, bridge);
		cc.start();
	}

}
