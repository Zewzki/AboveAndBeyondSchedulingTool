package client;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelector extends JFrame {

    private static FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");

    private JFileChooser jfc;

    public FileSelector() {

        jfc = new JFileChooser();
        jfc.setFileFilter(filter);

        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(jfc);

    }

}
