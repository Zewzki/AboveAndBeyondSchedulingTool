package control;

import personalData.Client;
import personalData.PersonalData;
import personalData.Therapist;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SessionLoader {

    public static int[] size;
    public static List<PersonalData> data;

    private static final FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    public static boolean writeSession(int w, int h, List<PersonalData> data, String saveDir) {

        File f = new File(saveDir);

        try {

            if(f.exists()) f.delete();

            f.createNewFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            bw.write(w + "," + h + "\n");

            for(PersonalData p : data) {

                String dataString = "";
                dataString += p.getType() + ",";
                dataString += p.getFirstName() + "," + p.getLastName() + ",";
                dataString += p.getAddress() + "," + p.getCity() + "," + p.getState() + "," + p.getZip() + ",";
                dataString += p.getLatitude() + "," + p.getLongitude();

                if(p.getType() == PersonalData.PersonType.Therapist) {

                    List<Client> clientList = ((Therapist) p).getClientList();

                    String clientString = "(";

                    if(clientList.size() > 0) {

                        for(int i = 0; i < clientList.size() - 1; i++) clientString += clientList.get(i).getFirstName() + " " + clientList.get(i).getLastName() + ";";
                        clientString += clientList.get(clientList.size() - 1).getFirstName() + " " + clientList.get(clientList.size() - 1).getLastName();

                    }

                    clientString += ")";
                    dataString += "," + clientString;

                }

                bw.write(dataString + "\n");

            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static boolean readSession() {

        JFileChooser fc = new JFileChooser();
        try {
            fc.setCurrentDirectory(new File(SessionLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        } catch (URISyntaxException e) {}

        fc.setFileFilter(filter);

        JOptionPane.showMessageDialog(null, "Select session to load (.abss)");
        int returnVal = fc.showOpenDialog(null);

        if(returnVal == JFileChooser.CANCEL_OPTION) System.exit(0);

        while(returnVal != JFileChooser.APPROVE_OPTION || !fc.getSelectedFile().exists()) {
            JOptionPane.showMessageDialog(null, "Couldn't load data, please select another file or try again.");
            returnVal = fc.showOpenDialog(null);
        }

        return readSession(fc.getSelectedFile().getAbsolutePath());

    }

    // type,first,last,addr,city,state,zip,lat,lng,(clients)

    public static boolean readSession(String loadDir) {

        File f = new File(loadDir);

        try {

            if(!f.exists()) return false;

            List<PersonalData> data = new ArrayList<>();

            BufferedReader br = new BufferedReader(new FileReader(f));

            String line = br.readLine();
            String[] s = line.split(",");
            int w = Integer.parseInt(s[0]);
            int h = Integer.parseInt(s[1]);

            size = new int[] {w, h};

            while((line = br.readLine()) != null) {

                String[] splits = line.split(",");

                if(splits[0].equals("Client")) {
                    data.add(new Client(splits[1], splits[2], splits[3], splits[4], splits[5], splits[6], Float.parseFloat(splits[7]), Float.parseFloat(splits[8]), PersonalData.PersonType.Client));
                }
                else if(splits[0].equals("Therapist")) {
                    data.add(new Therapist(splits[1], splits[2], splits[3], splits[4], splits[5], splits[6], Float.parseFloat(splits[7]), Float.parseFloat(splits[8]), PersonalData.PersonType.Therapist));
                }

            }

            br.close();

            br = new BufferedReader(new FileReader(f));
            line = br.readLine();

            int i = -1;

            while((line = br.readLine()) != null) {

                i++;

                String[] splits = line.split(",");

                if(splits[0].equals("Client")) continue;

                String clientListString = splits[9];
                clientListString = clientListString.replace("(", "");
                clientListString = clientListString.replace(")", "");

                String[] clientList = clientListString.split(";");

                for(String client : clientList) {

                    String[] nameSplit = client.split(" ");

                    for(PersonalData p : data) {

                        if(p.getType() == PersonalData.PersonType.Therapist) continue;

                        if(p.getFirstName().toLowerCase().equals(nameSplit[0].toLowerCase()) && p.getLastName().toLowerCase().equals(nameSplit[1].toLowerCase())) {
                            ((Therapist) data.get(i)).addClient((Client) p);
                        }

                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

}
