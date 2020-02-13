package terrain;

public class IslandPoint {
	private int x;
	private int z;
	private float radius;
	
	public IslandPoint(int x, int z, float radius) {
		this.x = x;
		this.z = z;
		this.radius = radius;
	}

	protected int getX() {
		return x;
	}

	protected int getZ() {
		return z;
	}

	protected float getRadius() {
		return radius;
	}
	
	
}
