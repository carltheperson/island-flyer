package terrain;

import java.security.Timestamp;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import rendering.DisplayManager;
import rendering.Loader;
import terrain.HeightsGenerator;

public class TerrainManager {
	
	private static final float SIZE = 100;
	private static final int VERTEX_COUNT = 128;
	
	private float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
	private int count = VERTEX_COUNT * VERTEX_COUNT;
	private float[] vertices = new float[count * 3];
	private float[] normals = new float[count * 3];
	private int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
	
	private HeightsGenerator generator = new HeightsGenerator(0, 0, 128, new Random().nextInt(1000000000));
	
	private Terrain terrain = new Terrain(-40, -40);
	
	public TerrainManager(Loader loader, Player player) {
		generateTerrain(loader, player);
		
		
	}
	
	private void generateTerrain(Loader loader, Player player){
		
		updateTerrainData(player);
		
		this.terrain.setModel(loader.loadToVAO(vertices, normals, indices));
		
		
	}
	
	public void update(Player player, Loader loader) {
		
		if (player.getPosition().x < (SIZE / 3) + terrain.getX()) {
			terrain.setX(terrain.getX() - (SIZE / 3));
			updateTerrainData(player);
			loader.updatePositionsAndNormals(vertices, normals);
		}
		
		if (player.getPosition().x > (terrain.getX() + SIZE) - (SIZE / 3)) {
			terrain.setX(terrain.getX() + (SIZE / 3));
			updateTerrainData(player);
			loader.updatePositionsAndNormals(vertices, normals);
		}
		
		if (player.getPosition().z < terrain.getZ() + (SIZE / 3)) {
			terrain.setZ(terrain.getZ() - (SIZE / 3));
			updateTerrainData(player);
			loader.updatePositionsAndNormals(vertices, normals);
		}
		
		if (player.getPosition().z > (terrain.getZ() + SIZE) - (SIZE / 3)) {
			terrain.setZ(terrain.getZ() + (SIZE / 3));
			updateTerrainData(player);
			loader.updatePositionsAndNormals(vertices, normals);
		}
		
	}
	
	
	public void updateTerrainData(Player player) {
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				float x = (float)j / ((float)VERTEX_COUNT - 1) * SIZE;
				float z = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				
				vertices[vertexPointer*3] = x; // x
				vertices[vertexPointer*3+2] = z; // z
				
				//System.out.println(((float)j / ((float)VERTEX_COUNT - 1) * SIZE) - ((float)(j +1) / ((float)VERTEX_COUNT - 1) * SIZE));

				float height = getHeight((int)(x + terrain.getX()), (int)(z + terrain.getZ()), generator);
				
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height; // y

				Vector3f normal = calculateNormal((int)(x + terrain.getX()), (int)(z + terrain.getZ()), (int) height);
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
		return (float) ((float) ((float) Math.cos(x / 5) * Math.sin(z / 5)));
		/*
		if (x % 30 == 0 && z % 40 == 0) {
			return 15;
		}
		
		if (x % 10 == 0 && z % 10 == 0) {
			return 5;
		}
		
		
		
		return 0;
		*/
		
		//return generator.generateHeight(x, z);
	}
	
	
	public Terrain getTerrain() {
		return this.terrain;
	}
	
	private int getOffset(float coord, float number) {

		int times = 0;
		
		while (coord >= number) {
			coord = coord - number;
			times ++;
		}
		
		return times;
	}
	
}
