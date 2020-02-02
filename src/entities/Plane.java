package entities;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import rendering.DisplayManager;
import rendering.Loader;
import textures.ModelTexture;

public class Plane extends Entity {

	private static final float SPEED = 15f;
	private static final float TURN_SPEED = SPEED * 2f;

	private static final float PROPELLER_SPEED = 400f;
	private Entity propeller;
	
	private float currentTurnSpeed = 10f;
	private float currentUpDownRotation = 0;

	public Plane(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) {
		super(model, position, rotX, rotY, rotZ, scale);
		RawModel propellerRawModel =  OBJFileLoader.loadModelDataToVAO(OBJFileLoader.loadOBJ("propeller"), loader);
		TexturedModel propellerModel = new TexturedModel(propellerRawModel,  new ModelTexture(loader.loadTexture("planeTex")));
		propeller = new Entity(propellerModel, position, rotX, rotY, rotZ, scale);
	}
	
	public void move() {

		checkInputs();
		
		float distance = SPEED * DisplayManager.getFrameTimeSeconds();
		
		float boostFactor = 0;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			boostFactor = 10f * DisplayManager.getFrameTimeSeconds();
		}
		distance += boostFactor;
		
		float YDistance = (float) (distance * Math.sin(Math.toRadians(super.getRotX())));
		float XZDistance = (float) (distance * Math.cos(Math.toRadians(super.getRotX())));

		super.increasePosition(0, -YDistance, 0);
		
		float dx = (float) (XZDistance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (XZDistance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds() * (boostFactor+1), 0);
		propeller.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds() * (boostFactor+1), PROPELLER_SPEED * DisplayManager.getFrameTimeSeconds() + boostFactor*7.5f);

		this.setRotX(currentUpDownRotation * 35);
		propeller.setRotX(currentUpDownRotation * 35);

	}
	
	
	private void checkInputs() {
	
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} if(!Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = 0;
		}
		
		currentUpDownRotation = (( (float) (Mouse.getY() - (Display.getHeight()/2)))/Display.getHeight()*2);
		if (currentUpDownRotation < 0) {
			currentUpDownRotation += 0.4*currentUpDownRotation*currentUpDownRotation;
		} if (currentUpDownRotation > 0) {
			currentUpDownRotation -= 0.4*currentUpDownRotation*currentUpDownRotation;
		}
		
	}
	
	public Entity getPropeller() {
		return propeller;
	}
	

}
