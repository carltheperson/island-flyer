package terrain;

import models.RawModel;


public class Terrain {
	private float x;
	private float z;
	private RawModel model;
	
	public Terrain(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}
	
	public void setModel(RawModel model) {
		this.model = model;
	}


	

}
