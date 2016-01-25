package hetca;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Anthony Eden
 */
public class BufferedImageBuilder {

  public static void imageToBufferedImageSave(Image im, String outputDir, int Cycle) throws IOException {
	BufferedImage bi = new BufferedImage(im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_RGB);
	Graphics bg = bi.getGraphics();
	bg.drawImage(im, 0, 0, null);
	bg.dispose();
	File rep1 = new File(outputDir);

	if (!rep1.exists()) {
		rep1.mkdirs();
	}
	File output = new File(outputDir + "/" + Cycle + ".png");
	ImageIO.write(bi, "png", output);
  }

    static void imageToBufferedImageSave2(BufferedImage theImage, String outputDir, int Cycle) throws IOException {

	File rep1 = new File(outputDir);

	if (!rep1.exists()) {
		rep1.mkdirs();
	}
	File output = new File(outputDir + "/" + Cycle + ".png");
	ImageIO.write(theImage, "png", output);    
    }

}