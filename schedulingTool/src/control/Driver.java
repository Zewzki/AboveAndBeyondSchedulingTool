package control;

import client.ClientFrame;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    public static void main(String[] args) {

        try {

            for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if(info.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (Exception e) {

        }

        ClientFrame client = new ClientFrame();

    }

}
