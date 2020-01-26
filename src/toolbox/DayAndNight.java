package toolbox;

import org.lwjgl.util.vector.Vector3f;

import entities.Light;
import rendering.DisplayManager;

public class DayAndNight {
	private static float time = 0;
	private static final int SPEED = 25;
	
	public static float getBlendFactor() {
		
		float blendFactor = 0;
		
		float nightStart = 550;
		float nightEnd = 1250;
		float dayStart = 2400;
		

		if (time > nightStart && time < nightEnd) {
			blendFactor = (time - nightStart) / (nightEnd - nightStart);
		} else if (time > nightEnd && time < dayStart) {
			blendFactor = 1 - (time - nightEnd) / (dayStart - nightEnd);
		}
		return blendFactor;
	}
	
	public static void updateDayNight() {
		time += (DisplayManager.getFrameTimeSeconds() * SPEED);
		time %= 2400;
	}
	
	public static void updateSun(Light light) {
		float brightness = 0.7f * (1 - DayAndNight.getBlendFactor()) + 0.3f; // for sun
		
		light.setColour(new Vector3f(brightness, brightness, brightness));
		
		DayAndNight.updateDayNight();
	}

}
