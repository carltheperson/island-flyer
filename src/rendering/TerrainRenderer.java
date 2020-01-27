package rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import models.RawModel;
import shaders.TerrainShader;
import terrain.Terrain;
import toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Terrain terrain) {
		
		prepareTerrain(terrain);
		loadModelMatrix(terrain);
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		GL30.glBindVertexArray(0);
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		shader.loadShineVariables(1, 0);

	}
	
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
}
