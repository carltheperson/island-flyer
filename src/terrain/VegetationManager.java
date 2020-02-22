package terrain;

import java.util.ArrayList;

import entities.PalmTree;

public class VegetationManager {
	
	private HeightsGenerator heightsGenerator;
	
	private static final int MIN_SPAWN_HEIGHT = 15;
	
	public VegetationManager(HeightsGenerator heightsGenerator) {
		this.heightsGenerator = heightsGenerator;
	}
	
	public PalmTree getPalmTree(float x, float z) {
		
		ArrayList<IslandPoint> points = heightsGenerator.getIslandPoints((int)Math.abs(x), (int)Math.abs(z));
		if (points.size() == 0) {
			return null;
		}
		
		if (1-Math.abs(heightsGenerator.getNoiseForIsland((int)x, (int)z)) > 0.995f && heightsGenerator.generateHeight((int)x, (int)z) > MIN_SPAWN_HEIGHT) {
			return new PalmTree(x, heightsGenerator.generateHeight((int)x, (int)z), z, getRandomNumber(0, 360, x,z), getRandomNumber(1f, 2f, x,z));
		}
		
		return null;
	}
	
	private float getRandomNumber(float min, float max, float x, float z) {
		return Math.abs(heightsGenerator.getNoise((int)x, (int)z)) * max + min;
	}

}
