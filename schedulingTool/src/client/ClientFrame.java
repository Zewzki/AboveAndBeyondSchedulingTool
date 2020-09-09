package client;

import javax.swing.*;
import java.awt.event.*;

public class ClientFrame extends JFrame {

    private static final int STARTING_WIDTH = 900;
    private static final int STARTING_HEIGHT = 900;

    //private static final int STARTING_DIM = 800;

    private ClientPanel panel;

    public ClientFrame() {

        setSize(STARTING_WIDTH, STARTING_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        addMouseListener(new MouseHandler());
        addKeyListener(new KeyHandler());
        addMouseMotionListener(new MouseMotionHandler());
        addComponentListener(new ResizeHandler());

        panel = new ClientPanel(STARTING_WIDTH, STARTING_HEIGHT);
        add(panel);

        setVisible(true);

    }

    private class MouseHandler implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

    }

    private class KeyHandler implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}

    }

    private class MouseMotionHandler implements MouseMotionListener {


        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {

            int x = e.getX() - 10;
            int y = e.getY() - 40;
            panel.setMousePosition(x, y);

        }

    }

    private class ResizeHandler implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {

            panel.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            panel.repaint();

        }

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}
    }

}
