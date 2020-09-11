package client;

import control.HttpCommands;
import personalData.Client;
import personalData.DataLoader;
import personalData.PersonalData;
import personalData.Therapist;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class ClientPanel extends JPanel {

    private enum Quadrant {

        ONE, TWO, THREE, FOUR;

        public int getXOffset() {
            if(this == Quadrant.ONE || this == Quadrant.FOUR) return -1;
            else return 10;
        }

        public int getYOffset() {
            if(this == Quadrant.ONE || this == Quadrant.TWO) return 20;
            else return 0;
        }

    }

    private static final int displayOffsetX = -10;
    private static final int displayOffsetY = -40;

    private static final float distanceFromCenterCoefficient = 200.0f;

    private static final Color clientColor = Color.RED;
    private static final Color therapistColor = new Color(0, 173, 0);
    private static final Color assignmentColor = new Color(6, 137, 198);
    private static final Color textColor = new Color(0, 0, 0);
    private static final Color textBackgroundColor = new Color(232, 223, 180);

    private static final BasicStroke thinStroke = new BasicStroke(1);
    private static final BasicStroke thickStroke = new BasicStroke(3);

    private static final int textBoxHeight = 20;
    private static final int pointSize = 10;
    private static final int displayDistance = 10;
    private static final int nameFontSize = 24;
    private static final Font nameFont = new Font("Calibri", Font.BOLD, nameFontSize);

    private BufferedImage map;
    private int mapOffsetX;
    private int mapOffsetY;

    private int mouseX;
    private int mouseY;

    // tlX, tlY, brX, brY
    private float[] boundingBox;
    private float[] center;
    private int zoom;
    private int[] size;
    private int margin = 0;

    private float minDistance;
    private int closestPointIndex = -1;
    private Quadrant quadrant = Quadrant.ONE;

    private List<Client> clientData;
    private List<Therapist> therapistData;
    private List<PersonalData> allData;

    public ClientPanel(int w, int h) {

        //setSize(w, h);

        //clientData = selectFileAndLoad("client");
        //therapistData = selectFileAndLoad("therapist");
        //assignmentData = selectFileAndLoad("assignment");

        //clientData = DataLoader.loadClientData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\clientList.csv");
        //therapistData = DataLoader.loadTherapistData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\therapistList.csv");
        clientData = DataLoader.loadClientData();
        therapistData = DataLoader.loadTherapistData();
        DataLoader.loadAssignments(clientData, therapistData);

        //DataLoader.loadAssignments("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTOol\\src\\rsc\\assignmentlIST.csv", clientData, therapistData);

        allData = new ArrayList<PersonalData>();
        allData.addAll(clientData);
        allData.addAll(therapistData);

        //[maxLat, minLng, minLat, maxLng]
        boundingBox = new float[] {-90.0f, 180.0f, 90.0f, -180.0f};
        center = new float[] {39, -100};
        zoom = 14;
        size = new int[] {w, h};
        minDistance = Float.MAX_VALUE;

        findBoundingBox();
        DataLoader.printDataList(allData);
        updateMap();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(map, mapOffsetX, mapOffsetY, size[0] + mapOffsetX, size[1] + mapOffsetY, null);

        for(int i = 0; i < allData.size(); i++) {

            PersonalData curr = allData.get(i);

            Color c = curr.getType() == PersonalData.PersonType.Client ? clientColor : therapistColor;
            g.setColor(c);

            if(curr.getType() == PersonalData.PersonType.Therapist) {

                Therapist t = (Therapist) curr;

                if(t.getDisplayClients()) {

                    List<Client> therapistClients = t.getClientList();

                    g.setColor(assignmentColor);

                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(thickStroke);

                    for(Client client : therapistClients)
                        g2.drawLine(curr.getTranslationX() + pointSize / 2, curr.getTranslationY() + pointSize / 2, client.getTranslationX() + pointSize / 2, client.getTranslationY() + pointSize / 2);

                    g2.setStroke(thinStroke);

                }

            }

            g.setColor(c);
            g.fillOval(curr.getTranslationX(), curr.getTranslationY(), pointSize, pointSize);

        }

        if(minDistance <= displayDistance && closestPointIndex >= 0) {

            String name = allData.get(closestPointIndex).getFirstName() + " " + allData.get(closestPointIndex).getLastName();
            g.setColor(textColor);
            g.setFont(nameFont);

            int x = allData.get(closestPointIndex).getTranslationX();
            int y = allData.get(closestPointIndex).getTranslationY();

            int xOffs = (quadrant == Quadrant.ONE || quadrant == Quadrant.FOUR) ? quadrant.getXOffset() * g.getFontMetrics().stringWidth(name) : quadrant.getXOffset();
            int yOffs = quadrant.getYOffset();

            int textBoxWidth = g.getFontMetrics().stringWidth(name);
            //int textBoxHeight = g.getFontMetrics().getHeight();

            g.setColor(textBackgroundColor);
            g.fillRect(x + xOffs, y + yOffs - textBoxHeight, textBoxWidth, textBoxHeight);

            g.setColor(textColor);
            g.drawRect(x + xOffs, y + yOffs - textBoxHeight, textBoxWidth, textBoxHeight);
            g.drawString(name, x + xOffs, y + yOffs);

        }

    }

    //[maxLat, minLng, minLat, maxLng]
    private void findBoundingBox() {

        //float avgLat = 0.0f;
        //float avgLng = 0.0f;

        for(int i = 0; i < allData.size(); i++) {

            float lat = allData.get(i).getLatitude();
            float lng = allData.get(i).getLongitude();

            //avgLat += lat;
            //avgLng += lng;

            if(lng < boundingBox[1]) boundingBox[1] = lng;
            if(lng > boundingBox[3]) boundingBox[3] = lng;
            if(lat < boundingBox[2]) boundingBox[2] = lat;
            if(lat > boundingBox[0]) boundingBox[0] = lat;

        }

        //center[0] = avgLat / (float) allData.size();
        //center[1] = avgLng / (float) allData.size();

        center[0] = boundingBox[2] + ((boundingBox[0] - boundingBox[2]) / 2);
        center[1] = boundingBox[1] + ((boundingBox[3] - boundingBox[1]) / 2);

        System.out.println(String.format("Bounding Box: (%f, %f), (%f, %f)", boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]));
        System.out.println(String.format("Center: (%f, %f)", center[0], center[1]));

        //[maxLat, minLng, minLat, maxLng]
        for(int i = 0; i < allData.size(); i++)
            allData.get(i).calculateTranslation(boundingBox[2], boundingBox[0], boundingBox[1], boundingBox[3]);

    }

    private void updateMap() {

        String centerString = center[0] + "," + center[1];
        String zoomString = Integer.toString(zoom);
        String sizeString = size[0] + "," + size[1];
        String boundingString = boundingBox[0] + "," + boundingBox[1] + "," + boundingBox[2] + "," + boundingBox[3];
        String marginString = Integer.toString(margin);

        map = HttpCommands.getStaticMap(centerString, zoomString, sizeString, boundingString, marginString);

    }

    private void calculateNearestToMouse() {

        minDistance = Float.MAX_VALUE;

        for(int i = 0; i < allData.size(); i++) {

            PersonalData curr = allData.get(i);
            int x = curr.getTranslationX();
            int y = curr.getTranslationY();

            float distanceFromMouse = (float) Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));

            if(distanceFromMouse < minDistance) {

                closestPointIndex = i;
                minDistance = distanceFromMouse;

                if(x > size[0] / 2 && y <= size[1] / 2) quadrant = Quadrant.ONE;
                else if(x <= size[0] / 2 && y <= size[1] / 2) quadrant = Quadrant.TWO;
                else if(x <= size[0] / 2 && y > size[1] / 2) quadrant = Quadrant.THREE;
                else quadrant = Quadrant.FOUR;

            }

        }

        //System.out.println(String.format("Mouse: (%d, %d) -> (%d, %d) = %f", mouseX, mouseY, allData.get(closestPointIndex).getTranslationX(), allData.get(closestPointIndex).getTranslationY(), minDistance));

    }

    public void processClick() {

        //System.out.println(String.format("Click @ (%d, %d)", mouseX, mouseY));

        if(closestPointIndex < 0 || closestPointIndex > allData.size()) return;

        if(allData.get(closestPointIndex).getType() == PersonalData.PersonType.Therapist) {
            Therapist t = (Therapist) allData.get(closestPointIndex);
            t.toggleDisplayClients();
        }

        repaint();

    }

    public void setMousePosition(int x, int y) {

        if(x == mouseX && y == mouseY) return;

        mouseX = x;
        mouseY = y;
        calculateNearestToMouse();
        repaint();

    }

    public void setSize(int x, int y) {
        size[0] = x + displayOffsetX;
        size[1] = y + displayOffsetY;

        PersonalData.setScreenDimensions(size[0], size[1]);

        for(PersonalData p : allData)
            p.calculateTranslation(boundingBox[2], boundingBox[0], boundingBox[1], boundingBox[3]);

    }

}
