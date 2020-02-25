package loadingScreen;


import org.lwjgl.util.vector.Vector2f;

import guis.GuiRenderer;
import guis.GuiTexture;
import rendering.DisplayManager;
import rendering.Loader;

public class LoadingRenderer {
	
	private static final float FADE_DURATION = 2f;
	private static final float OFFSET = 2f;
	private static final float TEXT_OFFSET = 0.75f;
	
	private GuiRenderer guiRenderer;
	private long startTime;
	private GuiTexture loadingBackgorund;
	private GuiTexture loadingText;
	
	public LoadingRenderer(GuiRenderer guiRenderer, Loader loader) {
		this.guiRenderer = guiRenderer;
		this.startTime = DisplayManager.getCurrentTime();
		this.loadingBackgorund = new GuiTexture(loader.loadTexture("loading background"), new Vector2f(0, 0f), new Vector2f(1f, 1f));
		this.loadingText = new GuiTexture(loader.loadTexture("loading text"), new Vector2f(0, 0f), new Vector2f(1f, 1f));
	}
	
	public void render() {
		float fadeFactor = (((float)((DisplayManager.getCurrentTime()-(OFFSET*1000f)) - startTime))/1000f)/FADE_DURATION;

		if (fadeFactor > 1) {
			return;
		}
		
		guiRenderer.render(loadingBackgorund, 1f - fadeFactor);
		fadeFactor = (((DisplayManager.getCurrentTime()-(TEXT_OFFSET*1000)) - startTime)/1000f)/FADE_DURATION;
		guiRenderer.render(loadingText, (1f - fadeFactor));
	}

}
