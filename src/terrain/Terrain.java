package terrain;

import org.lwjgl.util.vector.Vector3f;

import entities.Plane;
import models.ChunkModel;
import models.RawModel;
import rendering.Loader;


public class Terrain {
	
	private static final int VERTEX_COUNT = 10;
	
	public static Plane player;
	
	private float x;
	private float z;
	private RawModel model;
	private float size;
	private int distance;
	
	private float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
	private int count = VERTEX_COUNT * VERTEX_COUNT;
	private float[] vertices = new float[count * 3];
	private float[] normals = new float[count * 3];
	private int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
	private HeightsGenerator generator;
	
	private ChunkModel chunkModel = new ChunkModel();
	
	public Terrain(int x, int z, int size, HeightsGenerator generator) {
		this.x = x;
		this.z = z;
		this.size = size;
		this.generator = generator;
	}
	
	public void init(Loader loader) {
		updateTerrainData();
		model = loader.loadToVAO(vertices, normals, indices, chunkModel);
	}
	
	public void updateChunkData(Loader loader) {
		updateTerrainData();
		loader.updatePositionsAndNormals(vertices, normals, chunkModel);
	}
	
	public void calculateDistance() {
		distance = (int) Math.sqrt(((x - player.getPosition().x)*(x - player.getPosition().x)) + ((z - player.getPosition().z)*(z - player.getPosition().z)));
	}
	
	public int getDistance() {
		return distance;
	}
	
	public void updateTerrainData() {
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				float terrainX = (float)j / ((float)VERTEX_COUNT - 1) * size;
				float terrainZ = (float)i/((float)VERTEX_COUNT - 1) * size;
				
				vertices[vertexPointer*3] = terrainX; // x
				vertices[vertexPointer*3+2] = terrainZ; // z


				float height = getHeight((int)(terrainX + x), (int)(terrainZ + z), generator);
				
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height; // y

				Vector3f normal = calculateNormal((int)(terrainX + x), (int)(terrainZ + z), (int) height);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;

				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

	}
	
	
	private Vector3f calculateNormal(int x, int z, int terrainHeight) {

		float heightL = getHeight(x-1, z, generator);
		float heightR = getHeight(x+1, z, generator);
		float heightD = getHeight(x, z-1, generator);
		float heightU = getHeight(x, z+1, generator);

		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	
	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}
	
	protected void setX(float x) {
		this.x = x;
	}

	protected void setZ(float z) {
		this.z = z;
	}
	
	

	public RawModel getModel() {
		return model;
	}


	

}
