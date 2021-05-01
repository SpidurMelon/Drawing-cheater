package robot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import util.FastRGB;
import util.SwingInput;

public class DrawingRobot {
    private final BufferedImage image;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final ArrayList<Point> colorPickers;
    private Robot robot;

    public DrawingRobot(BufferedImage image, int x, int y, int width, int height, ArrayList<Point> colorPickers) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.colorPickers = colorPickers;
        try {
            this.robot = new Robot();
            robot.setAutoDelay(1);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void draw(SwingInput input) {
        if (robot == null) return;
        HashMap<Point, Color> availableColors = new HashMap<Point, Color>();
        for (Point p:colorPickers) {
            availableColors.put(p, robot.getPixelColor(p.x, p.y));
        }
        BufferedImage scaledImage = convertImage(image.getScaledInstance(width, height, 0));
        FastRGB fastRGB = new FastRGB(scaledImage);

        for (int y = 0; y < scaledImage.getHeight(null); y++) {
            Color prevColor = null;
            int startX = 0;
            for (int x = 0; x < scaledImage.getWidth(null); x++) {
                int rgb = fastRGB.getRGB(x, y);
                Map.Entry<Point, Color> colorPick = getBestColorPick(new Color(rgb), availableColors);
                if (prevColor == null) {
                    pickColor(colorPick);
                    prevColor = colorPick.getValue();
                } else if (!prevColor.equals(colorPick.getValue())) {
                    move(this.x+startX, this.y+y);
                    press();
                    move(this.x+x, this.y+y);
                    release();

                    startX = x;
                    pickColor(colorPick);
                    prevColor = colorPick.getValue();
                }
            }
            move(this.x+startX, this.y+y);
            press();
            move(this.x+scaledImage.getWidth(null)-1, this.y+y);
            release();
        }

    }
    private void pickColor(Map.Entry<Point, Color> colorPick) {
        release();
        move(colorPick.getKey().x, colorPick.getKey().y);
        press();
        release();
    }
    private void move(int x, int y) {
        robot.mouseMove(x, y);
    }
    private void press() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }
    private void release() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private Map.Entry<Point, Color> getBestColorPick(Color requiredColor, HashMap<Point, Color> availableColors) {
        Map.Entry<Point, Color> result = null;
        double minDifferenceScore = Integer.MAX_VALUE;
        for (Map.Entry<Point, Color> colorPicker:availableColors.entrySet()) {
            Color currentColor = colorPicker.getValue();
            double colorDifferenceScore =
                    Math.abs(Math.pow((currentColor.getRed()-requiredColor.getRed())/255f, 3)) +
                    Math.abs(Math.pow((currentColor.getBlue()-requiredColor.getBlue())/255f, 3)) +
                    Math.abs(Math.pow((currentColor.getGreen()-requiredColor.getGreen())/255f, 3));
            if (colorDifferenceScore < minDifferenceScore) {
                result = colorPicker;
                minDifferenceScore = colorDifferenceScore;
            }
        }
        return result;
    }

    private BufferedImage convertImage(Image toConvert) {
        BufferedImage result = new BufferedImage(toConvert.getWidth(null), toConvert.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();
        g.drawImage(toConvert, 0, 0, toConvert.getWidth(null), toConvert.getHeight(null), null);
        g.dispose();
        return result;
    }

    private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {
        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }
}
