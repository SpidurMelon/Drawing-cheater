package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class FastRGB {
    public int width;
    public int height;
    private int[] pixels;

    public FastRGB(BufferedImage image) {
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
    }

    public int getRGB(int x, int y) {
        int pos = (y * width) + (x);
        return pixels[pos];
    }
}
