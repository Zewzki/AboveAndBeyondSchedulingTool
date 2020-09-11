package personalData;

import control.CSVReader;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class DataLoader {

    private static final int therapistAssignmentOffset = 7;

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    public static List<Client> loadClientData() {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select client list (.csv)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        return loadClientData(fc.getSelectedFile().getAbsolutePath());

    }

    public static List<Client> loadClientData(String path) {

        List<CSVRecord> raw = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(raw);

        ArrayList<Client> formatted = new ArrayList<>();

        for(int i = 0; i < /*10*/ raw.size(); i++) {

            CSVRecord c = raw.get(i);

            String first = c.get(0);
            String last = c.get(1);
            String addr = c.get(2);
            String city = c.get(4);
            String state = c.get(5);
            String zip = c.get(6);

            Client d = new Client(first, last, addr, city, state, zip);

            formatted.add(d);

        }

        return formatted;

    }

    public static List<Therapist> loadTherapistData() {

        JFileChooser fc = new JFileChooser();
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

        List<CSVRecord> raw = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(raw);

        ArrayList<Therapist> formatted = new ArrayList<>();

        for(int i = 0; i < /*10*/ raw.size(); i++) {

            CSVRecord c = raw.get(i);

            String first = c.get(0);
            String last = c.get(1);
            String addr = c.get(2);
            String city = c.get(4);
            String state = c.get(5);
            String zip = c.get(6);

            Therapist d = new Therapist(first, last, addr, city, state, zip);

            formatted.add(d);

        }

        return formatted;

    }

    public static void loadAssignments(List<Client> clients, List<Therapist> therapists) {

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select assignment list (.csv)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        loadAssignments(fc.getSelectedFile().getAbsolutePath(), clients, therapists);

    }

    public static void loadAssignments(String path, List<Client> clients, List<Therapist> therapists) {

        List<CSVRecord> assignments = CSVReader.readCSV(path);
        CSVReader.pruneEmptyRecords(assignments);

        for(int i = 0; i < assignments.size(); i++) {

            CSVRecord curr = assignments.get(i);

            String[][] therapistNames = new String[curr.size() - therapistAssignmentOffset][2];

            for(int j = therapistAssignmentOffset; j < curr.size(); j++) {

                int adjustedIndex = j - therapistAssignmentOffset;

                try {

                    String[] splitNames = curr.get(j).split(" ", 2);
                    splitNames[0] = splitNames[0].toLowerCase();
                    splitNames[1] = splitNames[1].toLowerCase();

                    therapistNames[adjustedIndex] = splitNames;

                } catch (PatternSyntaxException | ArrayIndexOutOfBoundsException e) {
                    therapistNames[adjustedIndex] = new String[]{"", ""};
                }

            }


            Client c = null;
            ArrayList<Therapist> t = new ArrayList<>();

            String clientFirstName = curr.get(0).toLowerCase();
            String clientLastName = curr.get(1).toLowerCase();

            for(Client client : clients) {
                if(client.getFirstName().toLowerCase().equals(clientFirstName) && client.getLastName().toLowerCase().equals(clientLastName)) {
                    c = client;
                    break;
                }
            }

            if(c == null) continue;

            for(int j = 0; j < therapistNames.length; j++) {

                if(therapistNames[j][0].equals("") && therapistNames[j][1].equals("")) continue;

                for(Therapist therapist : therapists) {

                    if(therapist.getFirstName().toLowerCase().equals(therapistNames[j][0]) && therapist.getLastName().toLowerCase().equals(therapistNames[j][1])) {
                        //System.out.println(therapist.getFirstName() + " " + c.getLastName());
                        t.add(therapist);
                    }

                }
            }

            for(Therapist therapist : t) therapist.addClient(c);

        }

    }

    public static void printDataList(List<PersonalData> list) { for(PersonalData d : list) System.out.println(d.toString()); }

}
