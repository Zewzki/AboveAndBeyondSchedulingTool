package personalData;

import control.CSVReader;
import control.SessionLoader;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DataLoader {

    private static final boolean TEST_MODE = false;

    private static final int therapistAssignmentOffset = 7;

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    public static List<Client> loadClientData(List<Therapist> therapists) {

        JFileChooser fc = new JFileChooser();
        try {
            fc.setCurrentDirectory(new File(SessionLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        } catch (URISyntaxException e) {}
        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select client list (.csv)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        return loadClientData(fc.getSelectedFile().getAbsolutePath(), therapists);

    }

    public static List<Client> loadClientData(String path, List<Therapist> therapists) {

        LoadingBar lb = new LoadingBar();

        List<CSVRecord> raw = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(raw);

        List<Client> formatted = new ArrayList<>();

        int lim = TEST_MODE ? 10 : raw.size();

        for(int i = 0; i < lim; i++) {

            CSVRecord c = raw.get(i);

            String first = c.get(0);
            String last = c.get(1);
            String addr = c.get(2);
            String city = c.get(4);
            String state = c.get(5);
            String zip = c.get(6);

            lb.setText("Loading: " + first + " " + last);

            Client d = new Client(first, last, addr, city, state, zip);

            formatted.add(d);

            String[][] therapistNames = new String[c.size() - therapistAssignmentOffset][2];

            for(int j = therapistAssignmentOffset; j < c.size(); j++) {

                int adjustedIndex = j - therapistAssignmentOffset;

                try {

                    String[] splitNames = c.get(j).split(" ", 2);
                    splitNames[0] = splitNames[0].toLowerCase();
                    splitNames[1] = splitNames[1].toLowerCase();

                    therapistNames[adjustedIndex] = splitNames;

                } catch (PatternSyntaxException | ArrayIndexOutOfBoundsException e) {
                    therapistNames[adjustedIndex] = new String[]{"", ""};
                }

            }

            for(int j = 0; j < therapistNames.length; j++) {

                String tFirst = therapistNames[j][0];
                String tLast = therapistNames[j][1];

                for(Therapist t : therapists) {
                    if(t.getFirstName().toLowerCase().equals(tFirst) && t.getLastName().toLowerCase().equals(tLast)) t.addClient(d);
                }

            }

        }

        lb.close();

        return formatted;

    }

    public static List<Therapist> loadTherapistData() {

        JFileChooser fc = new JFileChooser();
        try {
            fc.setCurrentDirectory(new File(SessionLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        } catch (URISyntaxException e) {}
        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select therapist list (.csv)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        return loadTherapistData(fc.getSelectedFile().getAbsolutePath());

    }

    public static List<Therapist> loadTherapistData(String path) {

        LoadingBar lb = new LoadingBar();

        List<CSVRecord> raw = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(raw);

        ArrayList<Therapist> formatted = new ArrayList<>();

        int lim = TEST_MODE ? 10 : raw.size();

        for(int i = 0; i < lim; i++) {

            CSVRecord c = raw.get(i);

            String first = c.get(0);
            String last = c.get(1);
            String addr = c.get(2);
            String city = c.get(4);
            String state = c.get(5);
            String zip = c.get(6);

            lb.setText("Loading: " + first + " " + last);

            Therapist d = new Therapist(first, last, addr, city, state, zip);

            formatted.add(d);

        }

        lb.close();

        return formatted;

    }

    public static void printDataList(List<PersonalData> list) { for(PersonalData d : list) System.out.println(d.toString()); }

    private static class LoadingBar extends JFrame {

        private JLabel label;

        public LoadingBar() {

            setSize(300, 150);
            setUndecorated(true);
            setLocationRelativeTo(null);

            label = new JLabel("Loading...");
            add(label);

            setVisible(true);

            System.out.println("here");

        }

        public void setText(String s) {
            label.setText(s);
            repaint();
        }

        public void close() {
            dispose();
        }



    }

}
