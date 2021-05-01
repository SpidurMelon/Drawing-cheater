package util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import lombok.Getter;
import lombok.Setter;

public abstract class SwingInput implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, DropTargetListener {
    private boolean[] keys = new boolean[KeyEvent.KEY_LAST];
    private boolean[] mouseButtons = new boolean[MouseEvent.MOUSE_LAST];

    @Getter
    private int mouseX, mouseY, mouseXOnScreen, mouseYOnScreen, scrollX = 0, scrollY = 0;

    @Setter
    private Runnable onClick;

    private JPanel parentPanel;
    @Setter
    boolean autoRepaint = false;

    public SwingInput(JPanel parentPanel) {
        this.parentPanel = parentPanel;

        parentPanel.setFocusable(true);

        parentPanel.addMouseListener(this);
        parentPanel.addMouseMotionListener(this);
        parentPanel.addMouseWheelListener(this);
        parentPanel.addKeyListener(this);
        parentPanel.setDropTarget(new DropTarget(parentPanel, this));
    }

    public abstract void onDrop(String path);

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseXOnScreen = e.getXOnScreen();
        mouseYOnScreen = e.getYOnScreen();
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        mouseXOnScreen = e.getXOnScreen();
        mouseYOnScreen = e.getYOnScreen();
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtons[e.getButton()] = true;
        if (autoRepaint) parentPanel.repaint();
        if (onClick != null) onClick.run();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtons[e.getButton()] = false;
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollY += e.getWheelRotation();
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void drop(DropTargetDropEvent e) {
        DataFlavor[] fileTypes = e.getCurrentDataFlavors();
        if(!fileTypes[0].isFlavorJavaFileListType()) e.rejectDrop();
        try {
            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            List<File> files = (List<File>)e.getTransferable().getTransferData(fileTypes[0]);
            onDrop(files.get(0).getPath());
        } catch (UnsupportedFlavorException | IOException exception) {
            exception.printStackTrace();
            e.rejectDrop();
        }
        if (autoRepaint) parentPanel.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }



    public boolean isMouseButtonHeldDown(int button) {
        return mouseButtons[button];
    }

    public boolean isHeldDown(int key) {
        return keys[key];
    }
}
