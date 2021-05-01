package panels;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.Main;
import robot.DrawingRobot;
import util.Colors;
import util.SwingInput;

public class MainPanel extends JPanel {
    private SwingInput input;

    private ArrayList<Point> colorsToPick = new ArrayList<Point>();

    private boolean holdingColorIndicator = false, holdingPin1 = false, holdingPin2 = false;
    private Point pin1, pin2;

    private BufferedImage image;

    private final int indicatorRadius = 5;

    public MainPanel() {
        setOpaque(false);

        JButton addColor = new JButton("Add color");
        addColor.addActionListener(event ->{
            holdingColorIndicator = true;
        });
        add(addColor);

        JButton removeColors = new JButton("Remove colors");
        removeColors.addActionListener(event ->{
            colorsToPick.clear();
        });
        add(removeColors);

        JButton setPin1 = new JButton("Set pin 1");
        setPin1.addActionListener(event ->{
            holdingPin1 = true;
        });
        add(setPin1);

        JButton setPin2 = new JButton("Set pin 2");
        setPin2.addActionListener(event ->{
            holdingPin2 = true;
        });
        add(setPin2);

        JButton draw = new JButton("Start drawing");
        draw.addActionListener(event ->{
            int baseX = getLocationOnScreen().x, baseY = getLocationOnScreen().y;
            ArrayList<Point> absoluteColorPickers = new ArrayList<Point>();
            for (Point relativeColorPicker:this.colorsToPick) {
                absoluteColorPickers.add(new Point(baseX+relativeColorPicker.x, baseY+relativeColorPicker.y));
            }
            int x, y, width, height;
            x = Math.min(pin1.x, pin2.x);
            y = Math.min(pin1.y, pin2.y);
            width = Math.abs(pin1.x-pin2.x);
            height = Math.abs(pin1.y-pin2.y);
            int absoluteX = x+baseX, absoluteY = y+baseY;

            DrawingRobot robot = new DrawingRobot(image, absoluteX, absoluteY, width, height, absoluteColorPickers);
            Main.frame.setVisible(false);
            robot.draw(input);
            Main.frame.setVisible(true);
        });
        add(draw);

        input = new SwingInput(this) {
            @Override
            public void onDrop(String path) {
                try {
                    image = ImageIO.read(new File(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        input.setAutoRepaint(true);

        input.setOnClick(this::click);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    public void click() {
        if (holdingColorIndicator) {
            colorsToPick.add(new Point(input.getMouseX(), input.getMouseY()));
            holdingColorIndicator = false;
        } else if (holdingPin1) {
            pin1 = new Point(input.getMouseX(), input.getMouseY());
            holdingPin1 = false;
        } else if (holdingPin2) {
            pin2 = new Point(input.getMouseX(), input.getMouseY());
            holdingPin2 = false;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Colors.BACKGROUND.getColor());
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (holdingColorIndicator) {
            g2.setColor(Colors.INDICATOR.getColor());
            g2.fillOval(input.getMouseX()-indicatorRadius, input.getMouseY()-indicatorRadius, indicatorRadius*2, indicatorRadius*2);
        }

        if (holdingPin1 || holdingPin2) {
            g2.setColor(Colors.PIN.getColor());
            g2.fillOval(input.getMouseX()-indicatorRadius, input.getMouseY()-indicatorRadius, indicatorRadius*2, indicatorRadius*2);
        }

        if (pin1 != null) {
            g2.setColor(Colors.PIN.getColor());
            g2.fillOval(pin1.x-indicatorRadius, pin1.y-indicatorRadius, indicatorRadius*2, indicatorRadius*2);
        }

        if (pin2 != null) {
            g2.setColor(Colors.PIN.getColor());
            g2.fillOval(pin2.x-indicatorRadius, pin2.y-indicatorRadius, indicatorRadius*2, indicatorRadius*2);
        }

        for (Point p:colorsToPick) {
            g2.setColor(Colors.INDICATOR.getColor());
            g2.fillOval(p.x-indicatorRadius, p.y-indicatorRadius, indicatorRadius*2, indicatorRadius*2);
        }

        if (pin1 != null && pin2 != null && image != null) {
            int x, y, width, height;
            x = Math.min(pin1.x, pin2.x);
            y = Math.min(pin1.y, pin2.y);
            width = Math.abs(pin1.x-pin2.x);
            height = Math.abs(pin1.y-pin2.y);
            g2.drawImage(image, x, y, width, height, null);
        }
    }
}
