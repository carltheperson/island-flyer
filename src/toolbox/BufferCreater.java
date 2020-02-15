package toolbox;

import java.io.InputStream;
import java.nio.ByteBuffer;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class BufferCreater {

	public static ByteBuffer createBuffer(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			InputStream in = Class.class.getResourceAsStream("/res/" + fileName + ".png");
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load buffer " + fileName + ", didn't work");
			System.exit(-1);
		}
		
		return buffer;
	}
	
}
