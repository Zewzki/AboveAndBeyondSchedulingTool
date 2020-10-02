package client;

import control.SessionLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientFrame extends JFrame {

    public static final int STARTING_WIDTH = 900;
    public static final int STARTING_HEIGHT = 900;

    private static final Font infoFont = new Font("Didot", Font.PLAIN, 32);

    private static final String infoString = "<html>1. Export (as csv) a client list from ESRP containing first and last, address, and assigned therapist. No more no less.<br>" +
                                             "2. Export (as csv) an employee list from ESRP containing first and last and address. No more no less.<br>" +
                                             "3. Run this program and select 'Generate New'. It will guide you through correctly selecting these files.<br>" +
                                             "4. A map with points representing people will now display. Clicking will show assignments.<br>" +
                                             "5. Adjust the window size until dots appear to be in correct position, they should be pretty close but may need to adjust.<br>" +
                                             "6. You can save the session for quick loading in the future. This saves the size so you don't need adjust in the future.</html>";

    private ClientPanel clientPanel;

    private JButton loadExistingButton;
    private JButton generateNewButton;
    private JButton helpButton;
    private JButton backButton;

    private JMenu menu;
    private JMenuBar menuBar;
    private JMenuItem menuItem;

    private JLabel infoLabel;

    public ClientFrame() {

        setTitle("A&B Client Visualization");
        //setSize(STARTING_WIDTH, STARTING_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        //addMouseListener(new MouseHandler());
        //addKeyListener(new KeyHandler());
        //addMouseMotionListener(new MouseMotionHandler());
        //addComponentListener(new ResizeHandler());

        loadExistingButton = new JButton("Load Existing");
        generateNewButton = new JButton("Generate New");
        helpButton = new JButton("How To Use");
        backButton = new JButton("Back");
        infoLabel = new JLabel(infoString);

        loadExistingButton.setVisible(true);
        generateNewButton.setVisible(true);
        helpButton.setVisible(true);

        backButton.setVisible(false);
        infoLabel.setVisible(false);
        infoLabel.setFont(infoFont);

        ButtonListener listener = new ButtonListener();

        loadExistingButton.addActionListener(listener);
        generateNewButton.addActionListener(listener);
        helpButton.addActionListener(listener);
        backButton.addActionListener(listener);

        menuBar = new JMenuBar();

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_CONTROL);

        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(clientPanel == null) return;

                String dir = JOptionPane.showInputDialog("Enter save name (no spaces or special characters)") + ".csv";

                int[] imSize = new int[] {(int) clientPanel.getSize().getWidth(), (int) clientPanel.getSize().getHeight()};

                SessionLoader.writeSession(imSize[0], imSize[1], clientPanel.getAllData(), dir);

            }
        });

        menu.add(menuItem);
        menuBar.add(menu);

        setJMenuBar(menuBar);

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.weightx = .5;
        gc.weighty = .5;
        gc.ipadx = 5;
        gc.ipady = 5;

        gc.gridx = 0;
        gc.gridy = 0;
        add(loadExistingButton, gc);

        gc.gridx = 1;
        add(generateNewButton, gc);

        gc.gridx = 2;
        add(helpButton, gc);

        gc.ipadx = 1;
        gc.ipady = 1;

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(infoLabel, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.fill = GridBagConstraints.NONE;
        add(backButton, gc);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == loadExistingButton) {

                clientPanel = new ClientPanel();

                generateNewButton.setVisible(false);
                loadExistingButton.setVisible(false);
                helpButton.setVisible(false);
                backButton.setVisible(false);
                infoLabel.setVisible(false);

                /*
                GridBagConstraints gc = new GridBagConstraints();

                gc.gridx = 0;
                gc.gridy = 0;
                gc.ipadx = 0;
                gc.ipady = 0;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.gridwidth = 3;
                gc.gridheight = 3;
                add(clientPanel, gc);

                */

                setLayout(new FlowLayout());
                add(clientPanel);

                //setSize(clientPanel.getImSize()[0], clientPanel.getImSize()[1]);

                addMouseListener(new MouseHandler());
                addKeyListener(new KeyHandler());
                addMouseMotionListener(new MouseMotionHandler());
                addComponentListener(new ResizeHandler());

                pack();
                setLocationRelativeTo(null);

                repaint();

            }
            else if(e.getSource() == generateNewButton) {
                clientPanel = new ClientPanel(STARTING_WIDTH, STARTING_HEIGHT);

                generateNewButton.setVisible(false);
                loadExistingButton.setVisible(false);
                helpButton.setVisible(false);
                backButton.setVisible(false);
                infoLabel.setVisible(false);

                /*

                GridBagConstraints gc = new GridBagConstraints();

                gc.gridx = 0;
                gc.gridy = 0;
                gc.ipadx = 0;
                gc.ipady = 0;
                gc.weightx = 1.0;
                gc.weighty = 1.0;
                gc.gridwidth = 3;
                gc.gridheight = 3;
                add(clientPanel, gc);

                */

                setLayout(new FlowLayout());
                add(clientPanel);

                addMouseListener(new MouseHandler());
                addKeyListener(new KeyHandler());
                addMouseMotionListener(new MouseMotionHandler());
                addComponentListener(new ResizeHandler());

                pack();
                setLocationRelativeTo(null);

                repaint();

            }
            else if (e.getSource() == helpButton) {

                generateNewButton.setVisible(false);
                loadExistingButton.setVisible(false);
                helpButton.setVisible(false);
                backButton.setVisible(true);
                infoLabel.setVisible(true);

                pack();
                setLocationRelativeTo(null);

                repaint();

            }
            else if (e.getSource() == backButton) {

                generateNewButton.setVisible(true);
                loadExistingButton.setVisible(true);
                helpButton.setVisible(true);
                backButton.setVisible(false);
                infoLabel.setVisible(false);

                pack();
                setLocationRelativeTo(null);

                repaint();

            }

        }
    }

    private class MouseHandler implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {

            clientPanel.processClick();

        }

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
            int y = e.getY() - 50;
            clientPanel.setMousePosition(x, y);

        }

    }

    private class ResizeHandler implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {

            clientPanel.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            clientPanel.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());

            clientPanel.repaint();

            //pack();
            setLocationRelativeTo(null);

            repaint();

        }

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}
    }

}
