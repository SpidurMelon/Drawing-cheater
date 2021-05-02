package robot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.LocalDateTime;
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

    public void draw(int skip) {
        long before = System.currentTimeMillis();
        if (robot == null) return;
        HashMap<Point, Color> availableColors = new HashMap<Point, Color>();
        for (Point p:colorPickers) {
            availableColors.put(p, robot.getPixelColor(p.x, p.y));
        }
        BufferedImage scaledImage = convertImage(image.getScaledInstance(width, height, 0));
        FastRGB fastRGB = new FastRGB(scaledImage);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int y = 0; y < scaledImage.getHeight(null); y+=skip) {
            Color prevColor = null;
            int startX = 0;
            for (int x = 0; x < scaledImage.getWidth(null); x+=skip) {
                int rgb = fastRGB.getRGB(x, y);
                Map.Entry<Point, Color> colorPick = getBestColorPick(new Color(rgb), availableColors);
                if (prevColor == null) {
                    pickColor(colorPick);
                    prevColor = colorPick.getValue();
                } else if (!prevColor.equals(colorPick.getValue())) {
                    for (int yAddition = 0; yAddition < skip; yAddition++) {
                        move(this.x + startX, this.y + y + yAddition);
                        press();
                        move(this.x + x, this.y + y + yAddition);
                        release();
                    }
                    startX = x;
                    pickColor(colorPick);
                    prevColor = colorPick.getValue();
                }
            }
            for (int yAddition = 0; yAddition < skip; yAddition++) {
                move(this.x + startX, this.y + y + yAddition);
                press();
                move(this.x+scaledImage.getWidth(null)-1, this.y + y + yAddition);
                release();
            }
        }
        long after = System.currentTimeMillis();
        System.out.println("The image took " + ((after-before)/1000f) + " seconds to draw");
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
}
