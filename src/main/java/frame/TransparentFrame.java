package frame;

import java.awt.*;
import javax.swing.*;

public class TransparentFrame extends JFrame {
    public TransparentFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setSize(new Dimension(800, 800));

        setBackground(new Color(0,0,0,0));
    }
    public void addPanel(JPanel panel) {
        add(panel);
    }
}
