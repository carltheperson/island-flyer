package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);

		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	// Terrain

	public static float getSmoothIslandBlendFactor(float x0, float y0, float x1, float y1, float r, float x, float z,
			float total) {
		float d = (float) Math.sqrt(Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2));

		boolean posible = true;
		if (d > r * 2) {
			posible = false;
		}
		if (d <= 0) {
			posible = false;
		}
		
		float finalHeight = 0;
		
		if (posible) {

			float a = (float) ((Math.pow(r, 2) - Math.pow(r, 2) + Math.pow(d, 2)) / (d * 2));

			float h = (float) Math.sqrt(Math.pow(r, 2) - Math.pow(a, 2));

			float x2 = x0 + a * (x1 - x0) / d;
			float y2 = y0 + a * (y1 - y0) / d;
			float p0x = x2 + h * (y1 - y0) / d;
			float p0y = y2 - h * (x1 - x0) / d;
			float p1x = x2 - h * (y1 - y0) / d;
			float p1y = y2 + h * (x1 - x0) / d;

			float pDistance = pDistance(x, z, p0x, p0y, p1x, p1y);
			float intersectingWidth = r * 2 - d;

			float factor = ((intersectingWidth / 2) - pDistance) / (intersectingWidth / 2);

			float d0 = (float) Math.sqrt(Math.pow((x - x0), 2) + Math.pow((z - y0), 2));
			float d1 = (float) Math.sqrt(Math.pow((x - x1), 2) + Math.pow((z - y1), 2));

			if (d0 < r - 10 && d1 < r - 10) {
				float midX = (p0x + p1x) / 2;
				float midZ = (p0y + p1y) / 2;
				float dMid = (float) Math.sqrt(Math.pow((x - midX), 2) + Math.pow((z - midZ), 2));
				float dInter = (float) Math.sqrt(Math.pow((p0x - p1x), 2) + Math.pow((p0y - p1y), 2));

				float dEdge = 0;
				if (r - d0 < r - d1) {
					dEdge = r - d0;
				} else {
					dEdge = r - d0;
				}
				float edgeFactor = (dEdge - 5) / (pDistance + dEdge);
				float midFactor = 1 - dMid / (dInter);
				finalHeight = total * edgeFactor * midFactor * factor * 0.5f;
				if (finalHeight == 0) {
					finalHeight = total;
				}
			}
		}
		return finalHeight;
	}

	private static float pDistance(float x, float y, float x1, float y1, float x2, float y2) {
		float A = x - x1;
		float B = y - y1;
		float C = x2 - x1;
		float D = y2 - y1;
		float E = -D;
		float F = C;
		float dot = A * E + B * F;
		float len_sq = E * E + F * F;

		return (float) (Math.abs(dot) / Math.sqrt(len_sq));
	}
	
	public static float getSlope(float x0, float y0, float x1, float y1) {
		return (y1 - y0) / (x1 - x0);
	}

	public static float getIntercept(float x, float y, float slope) {
		return y - slope * x;
	}

}
