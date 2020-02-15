
package terrain;

import java.util.ArrayList;
import java.util.Random;

import toolbox.Maths;

public class HeightsGenerator {

	private static final float AMPLITUDE = 85f;
	private static final int OCTAVES = 5;
	private static final float ROUGHNESS = 0.25f;

	private static final int ISLAND_CHECK_FREQUENCY = 20;
	private static final float MAX_ISLAND_RADIUS = 150;
	private static final float ISLAND_SQUARE_SIZE = 1000;

	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;

	public HeightsGenerator() {
		this.seed = random.nextInt(1000000000);
	}

	public HeightsGenerator(int gridX, int gridZ, int vertexCount, int seed) {
		this.seed = seed;
		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);

	}

	public float generateHeight(int x, int z) {
		x = Math.abs(x);
		z = Math.abs(z);
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
		}

		ArrayList<IslandPoint> points = getIslandPoints(x, z);
		if (points.size() == 0) {
			return 0;
		}

		total += 100;

		float biggestHeight = 0f;
		for (int i = 0; i < points.size(); i++) {

			float height = total * getDistanceFactor(points.get(i), x, z);
			if (height > biggestHeight) {
				biggestHeight = height;
			}
		}

		total = biggestHeight;
		float blendSmoother = 0;
		int blendSmooths = 0;

		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				if (i != j) {

					float finalHeight = Maths.getSmoothIslandBlendFactor(points.get(i).getX(), points.get(i).getZ(),
							points.get(j).getX(), points.get(j).getZ(), MAX_ISLAND_RADIUS, x, z, total);

					if (finalHeight != 0) {
						blendSmoother += finalHeight;
						blendSmooths += 1;
					}
				}
			}
		}

		if (blendSmooths != 0) {
			total += (blendSmoother) / blendSmooths;
		}

		if (total == 0) {
			return 0;
		}

		d = (float) Math.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(0.1f, i) * 50f;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp * 0.25f;
		}

		return total;
	}

	private float getDistanceFactor(IslandPoint point, int x, int z) {

		float distance = (float) Math.sqrt(Math.pow((x - point.getX()), 2) + Math.pow((z - point.getZ()), 2));
		float distanceFactor = (float) (distance / (point.getRadius()));

		// Makes islands less pointy
		float pointiness = 0.4f;
		float pointingStartingPoint = 0.8f * point.getRadius();
		float slope = Maths.getSlope(0, pointiness, pointingStartingPoint, pointingStartingPoint / point.getRadius());
		float distanceFactor2 = pointiness + distance * slope;
		if (distanceFactor2 > distanceFactor) {
			return (1 - distanceFactor2);
		}

		return (1 - distanceFactor);
	}

	private float getInterpolatedNoise(float x, float z) {
		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);
	}

	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (float) (1f - Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothNoise(int x, int z) {
		float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
				+ getNoise(x + 1, z + 1)) / 16f;
		float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}

	private float getNoise(int x, int z) {
		random.setSeed(x * 49632 + z * 325176 + seed);
		return random.nextFloat() * 2f - 1f;
	}

	private float getNoiseForIsland(int x, int z) {
		random.setSeed((long) (x * 48132 + z * 825236 + seed * 1.3));
		return random.nextFloat() * 2f - 1f;
	}

	private boolean isIslandPoint(int x, int z) {

		int squareNumberX = (int) ((x - x % ISLAND_SQUARE_SIZE) / ISLAND_SQUARE_SIZE);
		int squareNumberZ = (int) ((z - z % ISLAND_SQUARE_SIZE) / ISLAND_SQUARE_SIZE);

		// Checking if it's inside an island square
		if (getNoise(squareNumberX, squareNumberZ) > -0.75) {

			float distanceToMiddle = (float) Math
					.sqrt(Math.pow(((ISLAND_SQUARE_SIZE / 2) - (x % ISLAND_SQUARE_SIZE)), 2)
							+ Math.pow(((ISLAND_SQUARE_SIZE / 2) - (z % ISLAND_SQUARE_SIZE)), 2));

			float distanceFactor = distanceToMiddle / (ISLAND_SQUARE_SIZE / 2);
			// Checking if it's island point
			if (getNoiseForIsland(x, z) > (0.92f + distanceFactor * 0.15f) * 0.999f) {
				return true;
			}
		}

		return false;
	}

	private ArrayList<IslandPoint> getIslandPoints(int x, int z) {
		ArrayList<IslandPoint> points = new ArrayList<IslandPoint>();

		int first_pointX = (int) ((ISLAND_CHECK_FREQUENCY - (x % ISLAND_CHECK_FREQUENCY)) + x - MAX_ISLAND_RADIUS);
		int first_pointZ = (int) ((ISLAND_CHECK_FREQUENCY - (z % ISLAND_CHECK_FREQUENCY)) + z - MAX_ISLAND_RADIUS);

		for (int iX = 0; iX < ((MAX_ISLAND_RADIUS * 2) / ISLAND_CHECK_FREQUENCY); iX++) {
			for (int iZ = 0; iZ < ((MAX_ISLAND_RADIUS * 2) / ISLAND_CHECK_FREQUENCY); iZ++) {

				int pointX = first_pointX + iX * ISLAND_CHECK_FREQUENCY;
				int pointZ = first_pointZ + iZ * ISLAND_CHECK_FREQUENCY;

				if (isIslandPoint(pointX, pointZ)) {
					points.add(new IslandPoint(pointX, pointZ, ((getNoise(pointX, pointZ) + 1) / 2) * 90 + 60));
				}
			}
		}

		return points;
	}

	public void setOffsets(float[] vertices, int x, int z, float[] waterOffsets) {
		for (int i = 0; i < vertices.length / 3; i++) {
			waterOffsets[i] = (getNoise((int) vertices[i * 3] + x, (int) vertices[i * 3 + 2] + z) + 1) / 2;
		}

	}

}
