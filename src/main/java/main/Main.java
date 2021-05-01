package main;

import frame.TransparentFrame;
import javax.swing.*;
import panels.MainPanel;
import panels.OptionPanel;

public class Main {
    public static TransparentFrame frame;
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        frame = new TransparentFrame();
        frame.addPanel(new MainPanel());
        frame.setVisible(true);
    }
}
