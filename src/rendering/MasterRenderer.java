package rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import shaders.EntityShader;
import shaders.TerrainShader;
import terrain.Terrain;
import toolbox.DayAndNight;

public class MasterRenderer {
	
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;

	// Sky colour
	public static final float RED = 0f;
	public static final float GREEN = 0.635f;
	public static final float BLUE = 0.9098f;
	
	private Matrix4f projectionMatrix;

	private EntityShader shader = new EntityShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	
	private Map<TexturedModel, ArrayList<Entity>> entities = new HashMap<TexturedModel, ArrayList<Entity>>();
	//private SkyboxRenderer skyboxRenderer;
	
	
	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();
		
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		
		//skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	 public void renderScene(ArrayList<Entity> entities, Terrain[][] chunks, ArrayList<Light> lights,
	            Camera camera, Vector3f playerPosition) {
		 
	        for (Entity entity : entities) {
	            procesEntity(entity);
	        }

	        render(lights, camera, chunks, playerPosition);
	    }
	 
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void render(ArrayList<Light> lights, Camera camera, Terrain[][] chunks, Vector3f playerPosition) {
		float r = RED * (1 - DayAndNight.getBlendFactor());
		float g = GREEN * (1 - DayAndNight.getBlendFactor());
		float b = BLUE * (1 - DayAndNight.getBlendFactor());
		
		prepare();
		shader.start();
		shader.loadPlayerPosition(playerPosition);
		shader.loadSkyColour(r, g, b);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		
		terrainShader.start();
		terrainShader.loadPlayerPosition(playerPosition);
		terrainShader.loadSkyColour(r, g, b);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(chunks);
		terrainShader.stop();
		//skyboxRenderer.render(camera, r, g, b); 
		
		entities.clear();
	}
	 
	/*
	public void proccesTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	*/
	
	public void procesEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		ArrayList<Entity> batch = entities.get(entityModel);
		
		if (batch != null) {
			batch.add(entity);
		} else {
			ArrayList<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}
	
	private void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }

}
