package toolbox;

import rendering.DisplayManager;

public class WaveClock {

	private static float time = 0;
	
	private static final int SPEED = 3;	
	
	public static void updateWaveClock() {
		time += (DisplayManager.getFrameTimeSeconds() * SPEED);
		time %= 1000;
	}
	
	public static float getTime() {
		return time;
	}
	
}
