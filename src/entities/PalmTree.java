package entities;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import rendering.Loader;
import textures.ModelTexture;

public class PalmTree {
	public static Loader loader;
	
	public static RawModel treeModel;
	public static TexturedModel treeTexturedModel;
	
	private Entity entity;

	public PalmTree(float x, float y, float z, float rotY, float scale) {
		
		this.entity = new Entity(treeTexturedModel, new Vector3f(x, y, z), 0, rotY, 0, scale);
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public static void init(Loader loader) {
		treeModel = OBJFileLoader.loadModelDataToVAO(OBJFileLoader.loadOBJ("palm tree"), loader);
		treeTexturedModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("palmTex")));
		treeTexturedModel.getTexture().setUseFakeLighting(true);
	}
	
}
