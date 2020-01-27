package terrain;

import org.lwjgl.util.vector.Vector3f;

import rendering.Loader;

public class TerrainManager {
	
	private static final float SIZE = 100;

	private float[] terrainVertices;
	
	private float[][] heights;
	
	private Terrain terrain = new Terrain(0, 0);
	
	public TerrainManager(Loader loader) {
		generateTerrain(loader);
	}
	
	private void generateTerrain(Loader loader){
		
		int terrainHeight = 0;

		int VERTEX_COUNT = 128;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				vertices[vertexPointer*3] = (float)j / ((float)VERTEX_COUNT - 1) * SIZE; // x
				
				float height = terrainHeight;
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height; // y
				
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE; // z
				
				
				Vector3f normal = calculateNormal(j, i, terrainHeight);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
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
		
		this.terrainVertices = vertices;
		
		this.terrain.setModel(loader.loadToVAO(terrainVertices, textureCoords, normals, indices));
	}
	
	/*
	public float[] updateVertices() {
		
	}
	*/
	
	private Vector3f calculateNormal(int x, int z, int terrainHeight) {
		/*
		float heightL = getHeight(x-1, z, generator);
		float heightR = getHeight(x+1, z, generator);
		float heightD = getHeight(x, z-1, generator);
		float heightU = getHeight(x, z+1, generator);
		*/
		//Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD - heightU);
		Vector3f normal = new Vector3f(0, 2f, 0);
		normal.normalise();
		return normal;
	}
	
	/*
	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
	*/
	
	
	public Terrain getTerrain() {
		return this.terrain;
	}
	
}
