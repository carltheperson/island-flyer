
package terrain;

import java.util.ArrayList;
import java.util.Random;

import toolbox.Maths;

public class HeightsGenerator {

	private static final float AMPLITUDE = 85f;
	private static final int OCTAVES = 5;
	private static final float ROUGHNESS = 0.25f;

	private static final int ISLAND_CHECK_FREQUENCY = 20;
	private static final float ISLAND_RADIUS = 160;

	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;

	public HeightsGenerator() {
		this.seed = random.nextInt(1000000000);
	}

	// only works with POSITIVE gridX and gridZ values!
	public HeightsGenerator(int gridX, int gridZ, int vertexCount, int seed) {
		this.seed = seed;
		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);

	}

	public float generateHeight(int x, int z) {
		float total = 0;
		float d = (float) Math.pow(2, OCTAVES - 1);
		for (int i = 0; i < OCTAVES; i++) {
			float freq = (float) (Math.pow(2, i) / d);
			float amp = (float) Math.pow(ROUGHNESS, i) * AMPLITUDE;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
		}

		ArrayList<Integer[]> points = getIslandPoints(x, z);

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

					float finalHeight = Maths.getSmoothIslandBlendFactor(points.get(i)[0], points.get(i)[1],
							points.get(j)[0], points.get(j)[1], ISLAND_RADIUS, x, z, total);

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

		return total;
	}

	private float getDistanceFactor(Integer[] point, int x, int z) {
		float distance = (float) Math.sqrt(Math.pow((x - point[0]), 2) + Math.pow((z - point[1]), 2));
		float distanceFactor = (float) (distance / ISLAND_RADIUS);
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

	private ArrayList<Integer[]> getIslandPoints(int x, int z) {
		ArrayList<Integer[]> points = new ArrayList<Integer[]>();

		int first_pointX = (int) ((ISLAND_CHECK_FREQUENCY - (x % ISLAND_CHECK_FREQUENCY)) + x - ISLAND_RADIUS);
		int first_pointZ = (int) ((ISLAND_CHECK_FREQUENCY - (z % ISLAND_CHECK_FREQUENCY)) + z - ISLAND_RADIUS);

		for (int iX = 0; iX < Math.floor(((ISLAND_RADIUS * 2) / ISLAND_CHECK_FREQUENCY)); iX++) {
			for (int iZ = 0; iZ < Math.floor(((ISLAND_RADIUS * 2) / ISLAND_CHECK_FREQUENCY)); iZ++) {

				int pointX = first_pointX + iX * ISLAND_CHECK_FREQUENCY;
				int pointZ = first_pointZ + iZ * ISLAND_CHECK_FREQUENCY;


				// Checking if it's island point
				if (getNoiseForIsland(pointX, pointZ) > 0.995) {
					points.add(new Integer[] { pointX, pointZ });
				}
			}
		}

		return points;
	}

}
