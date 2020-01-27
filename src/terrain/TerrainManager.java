package terrain;

import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import rendering.Loader;

public class TerrainManager {
	
	private static final float SIZE = 100;
	private static final int VERTEX_COUNT = 128;

	private int terrainHeight = 0;
	private float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
	private int count = VERTEX_COUNT * VERTEX_COUNT;
	private float[] vertices = new float[count * 3];
	private float[] normals = new float[count * 3];
	private float[] textureCoords = new float[count*2];
	private int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
	
	private float a = 0;

	
	private Terrain terrain = new Terrain(0, 0);
	
	public TerrainManager(Loader loader) {
		generateTerrain(loader);
	}
	
	private void generateTerrain(Loader loader){
		
		updateTerrainData();
		
		this.terrain.setModel(loader.loadToVAO(vertices, normals, indices));
		
		
	}
	
	public void update(Player player, Loader loader) {
		terrain.setX(player.getPosition().x - SIZE / 2);
		terrain.setZ(player.getPosition().z - SIZE / 2);
		System.out.println(player.getPosition().x);
		
		updateTerrainData();
		loader.updatePositionsAndNormals(vertices, normals);
	}
	
	
	public void updateTerrainData() {
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				vertices[vertexPointer*3] = (float)j / ((float)VERTEX_COUNT - 1) * SIZE; // x
				
				float height = terrainHeight;
				
				if (i % 2 == 0 && j % 2 == 0) {
					height = 1 + a;
				}
				
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height; // y
				
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE; // z
				
				
				Vector3f normal = calculateNormal(j, i, (int) height);
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
		
		a = a + 0.005f;
	}
	
	
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