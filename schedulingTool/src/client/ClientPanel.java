package client;

import control.CSVReader;
import control.HttpCommands;
import control.PersonalData;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientPanel extends JPanel {

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    private static final Color clientColor = Color.RED;
    private static final Color therapistColor = Color.GREEN;

    private BufferedImage map;
    private int mapOffsetX;
    private int mapOffsetY;

    // tlX, tlY, brX, brY
    private float[] boundingBox;
    private float[] center;
    private int zoom;
    private int[] size;
    private int margin = 0;

    private List<PersonalData> clientData;
    private List<PersonalData> therapistData;
    private List<PersonalData> assignmentData;

    public ClientPanel(int w, int h) {

        //clientData = selectFileAndLoad("client");
        //therapistData = selectFileAndLoad("therapist");
        //assignmentData = selectFileAndLoad("assignment");

        clientData = loadData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\clientList.csv", PersonalData.PersonType.Client);
        therapistData = loadData("E:\\Code\\GitRepo\\AboveAndBeyondSchedulingTool\\schedulingTool\\src\\rsc\\therapistList.csv", PersonalData.PersonType.Therapist);
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

        for(int i = 0; i < clientData.size(); i++) {

            PersonalData curr = clientData.get(i);
            float lat = curr.getLatitude();
            float lng = curr.getLongitude();

            // (w - 0) / (x - 0) = (maxLat - minLat) / (lat - minLat)

            int mapX = (int) (size[0] / ((boundingBox[2] - boundingBox[0]) / (lat - boundingBox[0])));
            int mapY = (int) (size[1] / ((boundingBox[3] - boundingBox[1]) / (lng - boundingBox[1])));

            g.setColor(clientColor);
            g.fillOval(mapX, mapY, 5, 5);

        }

    }

    private void findBoundingBox() {

        float avgLat = 0.0f;
        float avgLng = 0.0f;

        for(int i = 0; i < clientData.size(); i++) {

            float lat = clientData.get(i).getLatitude();
            float lng = clientData.get(i).getLongitude();

            avgLat += lat;
            avgLng += lng;

            if(lat < boundingBox[0]) boundingBox[0] = lat;
            if(lat > boundingBox[2]) boundingBox[2] = lat;
            if(lng > boundingBox[1]) boundingBox[1] = lng;
            if(lng < boundingBox[3]) boundingBox[3] = lng;

        }

        for(int i = 0; i < therapistData.size(); i++) {

            float lat = therapistData.get(i).getLatitude();
            float lng = therapistData.get(i).getLongitude();

            avgLat += lat;
            avgLng += lng;

            if(lat < boundingBox[0]) boundingBox[0] = lat;
            if(lat > boundingBox[2]) boundingBox[2] = lat;
            if(lng > boundingBox[1]) boundingBox[1] = lng;
            if(lng < boundingBox[3]) boundingBox[3] = lng;

        }

        center[0] = avgLat / (float) (clientData.size() + therapistData.size());
        center[1] = avgLng / (float) (clientData.size() + therapistData.size());

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

        for(int i = 0; i < 5/*rawData.size()*/; i++) {

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

}
