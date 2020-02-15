package rendering;

import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

import toolbox.BufferCreater;

import java.nio.ByteBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;


public class DisplayManager {
	
	private static final int WIDTH = (int) 1280;
	private static final int HEIGHT = (int) 720;
	
	private static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		
		ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);
		
		try {
			Display.setTitle("Island Flyer");
			
			// Icon
			ByteBuffer[] list = new ByteBuffer[2];
			list[0] = BufferCreater.createBuffer("icon_16");
			list[1] = BufferCreater.createBuffer("icon_32");
			Display.setIcon(list);
			
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withSamples(8).withDepthBits(24), attribs);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		
		delta = ((float) currentFrameTime - lastFrameTime)/1000;
		
		
		lastFrameTime = currentFrameTime;
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}
	
	public static void closeDisplay() {
		Display.destroy();
		
	}
	
	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

}
