package rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.WaterShader;
import terrain.Terrain;
import toolbox.Maths;

public class WaterRenderer {

	WaterShader shader;

	public WaterRenderer(WaterShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Terrain[][] chunks) {
		MasterRenderer.disableCulling();

		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks[i].length; j++) {
				prepareWater(chunks[i][j]);
				loadModelMatrix(chunks[i][j]);

				GL11.glDrawElements(GL11.GL_TRIANGLES, chunks[i][j].getWaterModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);

				GL30.glBindVertexArray(0);
			}
		}

	}

	private void prepareWater(Terrain terrain) {
		RawModel rawModel = terrain.getWaterModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths
				.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
