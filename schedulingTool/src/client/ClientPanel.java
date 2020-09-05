package client;

import control.CSVReader;
import control.HttpCommands;
import control.PersonalData;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class ClientPanel extends JPanel {

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    private static final Color clientColor = Color.RED;
    private static final Color therapistColor = new Color(0, 173, 0);
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

    private List<PersonalData> clientData;
    private List<PersonalData> therapistData;
    private List<PersonalData> allData;
    private List<PersonalData> assignmentData;

    public ClientPanel(int w, int h) {

        //clientData = selectFileAndLoad("client");
        //therapistData = selectFileAndLoad("therapist");
        //assignmentData = selectFileAndLoad("assignment");

        clientData = loadData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\clientList.csv", PersonalData.PersonType.Client);
        therapistData = loadData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\therapistList.csv", PersonalData.PersonType.Therapist);
        allData = new ArrayList<PersonalData>();
        allData.addAll(clientData);
        allData.addAll(therapistData);

        //assignmentData = loadData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\assignmentList.csv");

        boundingBox = new float[] {90.0f, -180.0f, -90.0f, 180.0f};
        center = new float[] {39, -100};
        zoom = 14;
        size = new int[] {w, h};

        findBoundingBox();
        updateMap();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(map, mapOffsetX, mapOffsetY, size[0], size[1], null);

        int bestIndex = -1;
        float minDistance = Float.MAX_VALUE;
        int quadrant = 1;
        int[] bestPos = new int[2];

        for(int i = 0; i < allData.size(); i++) {

            PersonalData curr = allData.get(i);
            float lat = curr.getLatitude();
            float lng = curr.getLongitude();

            // (w - 0) / (x - 0) = (maxLat - minLat) / (lat - minLat)

            int mapX = (int) (size[0] / ((boundingBox[2] - boundingBox[0]) / (lat - boundingBox[0])));
            int mapY = (int) (size[1] / ((boundingBox[3] - boundingBox[1]) / (lng - boundingBox[1])));

            float distanceFromMouse = (float) Math.sqrt(Math.pow(mapX - mouseX, 2) + Math.pow(mapY - mouseY, 2));
            if(distanceFromMouse < minDistance) {

                bestIndex = i;
                minDistance = distanceFromMouse;
                bestPos[0] = mapX;
                bestPos[1] = mapY;

                if(mapX > size[0] / 2 && mapY <= size[1] / 2) quadrant = 1;
                else if(mapX <= size[0] / 2 && mapY <= size[1] / 2) quadrant = 2;
                else if(mapX <= size[0] / 2 && mapY > size[1] / 2) quadrant = 3;
                else quadrant = 4;

            }

            Color c = curr.getType() == PersonalData.PersonType.Client ? clientColor : therapistColor;

            g.setColor(c);
            g.fillOval(mapX, mapY, pointSize, pointSize);

        }

        System.out.println(String.format("(%d, %d) -> (%d, %d): %f", mouseX, mouseY, bestPos[0], bestPos[1], minDistance));

        System.out.println("Min Distance: " + minDistance);

        if(minDistance <= displayDistance) {

            String name = allData.get(bestIndex).getFirstName() + " " + allData.get(bestIndex).getLastName();
            g.setColor(Color.BLACK);
            g.setFont(nameFont);

            int xOffs = 0;
            int yOffs = 0;

            if(quadrant == 1) {
                yOffs = 0;
                xOffs = -1 * (name.length() * 10);
            }
            else if(quadrant == 2) {
                yOffs = 0;
                xOffs = 10;
            }
            else if(quadrant == 3) {
                yOffs = 0;
                xOffs = 10;
            }
            else if(quadrant == 4) {
                yOffs = 0;
                xOffs = -1 * (name.length() * 10);
            }

            g.drawString(name, bestPos[0] + xOffs, bestPos[1] + yOffs);

        }

    }

    private void findBoundingBox() {

        float avgLat = 0.0f;
        float avgLng = 0.0f;

        for(int i = 0; i < allData.size(); i++) {

            float lat = allData.get(i).getLatitude();
            float lng = allData.get(i).getLongitude();

            avgLat += lat;
            avgLng += lng;

            if(lat < boundingBox[0]) boundingBox[0] = lat;
            if(lat > boundingBox[2]) boundingBox[2] = lat;
            if(lng > boundingBox[1]) boundingBox[1] = lng;
            if(lng < boundingBox[3]) boundingBox[3] = lng;

        }

        center[0] = avgLat / (float) allData.size();
        center[1] = avgLng / (float) allData.size();

        System.out.println(String.format("Bounding Box: (%f, %f), (%f, %f)", boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]));
        System.out.println(String.format("Center: (%f, %f)", center[0], center[1]));

    }

    private void updateMap() {

        String centerString = center[0] + "," + center[1];
        String zoomString = Integer.toString(zoom);
        String sizeString = size[0] + "," + size[1];
        String boundingString = boundingBox[0] + "," + boundingBox[1] + "," + boundingBox[2] + "," + boundingBox[3];
        String marginString = Integer.toString(margin);

        map = HttpCommands.getStaticMap(centerString, zoomString, sizeString, boundingString, marginString);

    }

    private List<PersonalData> selectFileAndLoad(String listName, PersonalData.PersonType type) {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select " + listName + " list (.csv)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        return loadData(fc.getSelectedFile().getAbsolutePath(), type);

    }

    private List<PersonalData> loadData(String path, PersonalData.PersonType type) {

        List<CSVRecord> rawData = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(rawData);

        ArrayList<PersonalData> formattedData = new ArrayList<PersonalData>();

        for(int i = 0; i < 1 /*rawData.size()*/; i++) {

            CSVRecord c = rawData.get(i);

            String first = c.get(0);
            String last = c.get(1);
            String addr = c.get(2);
            String city = c.get(4);
            String state = c.get(5);
            String zip = c.get(6);

            PersonalData d = new PersonalData(first, last, addr, city, state, zip, type);

            System.out.println(d.toString());

            formattedData.add(d);

            //System.out.println(d.toString());

        }

        return formattedData;

    }

    public void setMousePosition(int x, int y) {

        if(x == mouseX && y == mouseY) return;

        mouseX = x;
        mouseY = y;
        repaint();

    }

}
