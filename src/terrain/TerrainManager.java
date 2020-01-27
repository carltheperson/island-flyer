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
	private static final int VERTEX_COUNT = 100;
	
	private float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];
	private int count = VERTEX_COUNT * VERTEX_COUNT;
	private float[] vertices = new float[count * 3];
	private float[] normals = new float[count * 3];
	private int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
	
	private HeightsGenerator generator = new HeightsGenerator(0, 0, 128, new Random().nextInt(1000000000));

	private float a = 0;
	
	private Terrain terrain = new Terrain(0, 0);
	
	public TerrainManager(Loader loader, Player player) {
		generateTerrain(loader, player);
		
		System.out.println(getOffset(51, 0.7f));
		
	}
	
	private void generateTerrain(Loader loader, Player player){
		
		updateTerrainData(player);
		
		this.terrain.setModel(loader.loadToVAO(vertices, normals, indices));
		
		
	}
	
	public void update(Player player, Loader loader) {
		terrain.setX(player.getPosition().x - SIZE / 2);
		terrain.setZ(player.getPosition().z - SIZE / 2);

		
		updateTerrainData(player);
		loader.updatePositionsAndNormals(vertices, normals);
	}
	
	
	public void updateTerrainData(Player player) {
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				
				vertices[vertexPointer*3] = (float)j / ((float)VERTEX_COUNT - 1) * SIZE; // x
				
				float distance = ((float)2/((float)VERTEX_COUNT - 1) * SIZE) - ((float)(1)/((float)VERTEX_COUNT - 1) * SIZE);
	
				
				float height = getHeight((int) (i + player.getPosition().z / distance), (int) (j + player.getPosition().x / distance), generator);
				
				heights[j][i] = height;
				vertices[vertexPointer*3+1] = height; // y
				
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE; // z
				

				Vector3f normal = calculateNormal((int) (i + player.getPosition().z / distance), (int) (j + player.getPosition().x / distance), (int) height);
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
		
		a += DisplayManager.getFrameTimeSeconds() * 20;
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
		
		if (x % 30 == 0 && z % 40 == 0) {
			return 15;
		}
		
		if (x % 10 == 0 && z % 10 == 0) {
			return 5;
		}
		
		
		
		return 0;
		
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
