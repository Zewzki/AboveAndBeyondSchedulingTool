package control;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    public static List<CSVRecord> readCSV(String path) {

        List<CSVRecord> records;

        File f = new File(path);
        System.out.println("Reading " + f.getAbsolutePath());

        try {

            CSVFormat format = CSVFormat.EXCEL;

            records = format.parse(new InputStreamReader(new FileInputStream(f))).getRecords();

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<CSVRecord>();
        }

        if(records.size() <= 0) {
            System.err.println("Error loading data: " + path);
        }

        return records;

    }

    public static void pruneEmptyRecords(List<CSVRecord> records) {

        for(int i = 0; i < records.size(); i++) {

            if(records.get(i).get(0).isBlank()) {
                records.remove(i);
                i--;
            }

        }

    }

    public static void printRecords(List<CSVRecord> records) {

        for(int i = 0; i < records.size(); i++) {

            CSVRecord r = records.get(i);

            for(int j = 0; j < r.size(); j++) System.out.print(r.get(j) + "\t");

            System.out.print("\n");

        }

    }

}
