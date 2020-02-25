package mainGame;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.PalmTree;
import entities.Plane;
import guis.GuiRenderer;
import loadingScreen.LoadingRenderer;
import loadingScreen.LoadingThread;
import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import rendering.DisplayManager;
import rendering.Loader;
import rendering.MasterRenderer;
import terrain.TerrainManager;
import textures.ModelTexture;
import toolbox.WaveClock;

public class MainGameLoop {
	public static void main(String[] args) {

		DisplayManager.createDisplay();

		Loader loader = new Loader();
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		new LoadingThread().run(guiRenderer, loader);

		RawModel model = OBJFileLoader.loadModelDataToVAO(OBJFileLoader.loadOBJ("plane"), loader);
		TexturedModel planeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("planeTex")));
		Plane plane = new Plane(planeModel, new Vector3f(0, 150, 0), 0, 0, 0, 3f, loader);
		
		Camera camera = new Camera(plane);

		MasterRenderer renderer = new MasterRenderer(loader, camera);

		PalmTree.init(loader);
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		
		Light light = new Light(new Vector3f(-100000, 1500000, -100000), new Vector3f(1f, 1f, 1f));

		TerrainManager terrainManager = new TerrainManager(loader, plane);

		LoadingRenderer loadingRenderer = new LoadingRenderer(guiRenderer, loader);
		while (!Display.isCloseRequested()) {
			
			if (Display.wasResized()) GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			WaveClock.updateWaveClock();
			
			plane.move();
			camera.move();
			terrainManager.update(loader);

			renderer.procesEntity(plane);
			renderer.procesEntity(plane.getPropeller());
			
			
			renderer.renderScene(entities, terrainManager.getChunks(), light, camera, plane.getPosition());
			
			loadingRenderer.render();
			
			DisplayManager.updateDisplay();
		}

		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();

		DisplayManager.closeDisplay();
	}
}
