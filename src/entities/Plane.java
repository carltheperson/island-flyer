package entities;


import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;
import rendering.DisplayManager;
import rendering.Loader;
import textures.ModelTexture;

public class Plane extends Entity {

	private static final float RUN_SPEED = 30;
	private static final float TURN_SPEED = 110;
	public static final float GRAVITY = -25;
	private static final float JUMP_POWER = 10;
	
	private static final float TERRAIN_HEIGHT = 50;

	private static final float PROPELLER_ROTATIONAL_SPEED = 400f;
	private Entity propeller;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	public Plane(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Loader loader) {
		super(model, position, rotX, rotY, rotZ, scale);
		RawModel propellerRawModel =  OBJFileLoader.loadModelDataToVAO(OBJFileLoader.loadOBJ("propeller"), loader);
		TexturedModel propellerModel = new TexturedModel(propellerRawModel,  new ModelTexture(loader.loadTexture("planeTex")));
		propeller = new Entity(propellerModel, position, rotX, rotY, rotZ, scale);
	}
	
	public void move() {

		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();

		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float terrainHeight = TERRAIN_HEIGHT;
		
		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
		}

		propeller.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), PROPELLER_ROTATIONAL_SPEED * DisplayManager.getFrameTimeSeconds());
	}
	
	private void jump() {
		this.upwardsSpeed = JUMP_POWER;
	}
	
	private void checkInputs() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} if(!Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.currentTurnSpeed = 0;
		}
		
		
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (upwardsSpeed == 0) {
				
			}
			
			jump();
		}
		
	}
	
	public Entity getPropeller() {
		return propeller;
	}
	

}
