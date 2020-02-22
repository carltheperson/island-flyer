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
import models.TexturedModel;
import shaders.EntityShader;
import shaders.TerrainShader;
import shaders.WaterShader;
import terrain.Terrain;
import toolbox.DayAndNight;
import toolbox.WaveClock;

public class MasterRenderer {
	
	public static boolean isStartScreenLoaded = false;

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	// Sky colour
	private static final float RED = 0.5098f;
	private static final float GREEN = 0.8196f;
	private static final float BLUE = 0.90196f;

	private Matrix4f projectionMatrix;

	private EntityShader shader = new EntityShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private WaterRenderer waterRenderer;
	private WaterShader waterShader = new WaterShader();

	private Map<TexturedModel, ArrayList<Entity>> entities = new HashMap<TexturedModel, ArrayList<Entity>>();

	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();

		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		waterRenderer = new WaterRenderer(waterShader, projectionMatrix);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void renderScene(ArrayList<Entity> entities, Terrain[][] chunks, Light light, Camera camera,
			Vector3f playerPosition) {

		for (Entity entity : entities) {
			procesEntity(entity);
		}

		render(light, camera, chunks, playerPosition);
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void render(Light light, Camera camera, Terrain[][] chunks, Vector3f playerPosition) {
		float r = RED * (1 - DayAndNight.getBlendFactor());
		float g = GREEN * (1 - DayAndNight.getBlendFactor());
		float b = BLUE * (1 - DayAndNight.getBlendFactor());

		prepare();
		shader.start();
		shader.loadPlayerPosition(playerPosition);
		shader.loadSkyColour(r, g, b);
		shader.loadLight(light);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		renderer.renderVegetation(chunks);
		shader.stop();

		terrainShader.start();
		terrainShader.loadPlayerPosition(playerPosition);
		terrainShader.loadSkyColour(r, g, b);
		terrainShader.loadLight(light);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(chunks);
		terrainShader.stop();

		waterShader.start();
		waterShader.loadPlayerPosition(playerPosition);
		waterShader.loadViewMatrix(camera);
		waterShader.loadLight(light);
		waterShader.loadSkyColour(r, g, b);
		waterShader.loadWaveClock(WaveClock.getTime());
		waterRenderer.render(chunks);
		waterShader.stop();

		entities.clear();
		
		isStartScreenLoaded = true;

	}

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
		waterShader.cleanUp();
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
	}

	private void createProjectionMatrix() {
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
