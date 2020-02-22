package mainGame;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.PalmTree;
import entities.Plane;
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

		RawModel model = OBJFileLoader.loadModelDataToVAO(OBJFileLoader.loadOBJ("plane"), loader);
		TexturedModel planeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("planeTex")));
		Plane plane = new Plane(planeModel, new Vector3f(0, 150, 0), 0, 0, 0, 3f, loader);
		
		Camera camera = new Camera(plane);

		MasterRenderer renderer = new MasterRenderer(loader, camera);

		PalmTree.init(loader);
		
		ArrayList<Entity> entities = new ArrayList<Entity>();

		Light light = new Light(new Vector3f(-100000, 1500000, -100000), new Vector3f(1f, 1f, 1f));

		TerrainManager terrainManager = new TerrainManager(loader, plane);

		while (!Display.isCloseRequested()) {
			WaveClock.updateWaveClock();

			plane.move();
			camera.move();
			terrainManager.update(loader);

			renderer.procesEntity(plane);
			renderer.procesEntity(plane.getPropeller());

			renderer.renderScene(entities, terrainManager.getChunks(), light, camera, plane.getPosition());

			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();

		DisplayManager.closeDisplay();
	}
}
