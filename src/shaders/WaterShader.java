package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class WaterShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/shaders/waterVertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/waterFragmentShader.txt";
	private static final String GEOMETRY_FILE = "/shaders/waterGeometryShader.txt";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_attenuation;
	private int location_playerPosition;
	private int location_skyColour;
	private int location_waveClock;

	public WaterShader() {
		super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "offset");
	}

	@Override
	protected void getAllUniformLocations() {
		// TODO Auto-generated method stub
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_playerPosition = super.getUniformLocation("playerPosition");
		location_skyColour = super.getUniformLocation("skyColour");
		location_waveClock = super.getUniformLocation("waveClock");

		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour");
		location_attenuation = super.getUniformLocation("attenuation");
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLight(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
		super.loadVector(location_attenuation, light.getAttenuation());
	}

	public void loadPlayerPosition(Vector3f position) {
		super.loadVector(location_playerPosition, position);
	}

	public void loadSkyColour(float r, float g, float b) {
		super.loadVector(location_skyColour, new Vector3f(r, g, b));
	}

	public void loadWaveClock(float time) {
		super.loadFloat(location_waveClock, time);
	}

}
