package loadingScreen;
import org.lwjgl.util.vector.Vector2f;

import guis.GuiRenderer;
import guis.GuiTexture;
import rendering.DisplayManager;
import rendering.Loader;

public class LoadingThread extends Thread {

	public void run(GuiRenderer guiRenderer, Loader loader) {
		GuiTexture loadingBackground = new GuiTexture(loader.loadTexture("loading background"), new Vector2f(0, 0f), new Vector2f(1f, 1f));
		GuiTexture loadingText = new GuiTexture(loader.loadTexture("loading text"), new Vector2f(0, 0f), new Vector2f(1f, 1f));
		
		guiRenderer.render(loadingBackground, 1f);
		guiRenderer.render(loadingText, 1f);
		DisplayManager.updateDisplay();
	}
	
}
