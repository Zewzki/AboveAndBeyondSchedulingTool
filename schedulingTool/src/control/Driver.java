package control;

import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

public class Driver {

    public static void main(String[] args) {

        List<CSVRecord> clientList = CSVReader.readCSV("schedulingTool/src/rsc/clientList.csv");
        CSVReader.pruneEmptyRecords(clientList);

        List<CSVRecord> therapistList = CSVReader.readCSV("schedulingTool/src/rsc/therapistList.csv");
        CSVReader.pruneEmptyRecords(therapistList);

        List<CSVRecord> assignmentList = CSVReader.readCSV("schedulingTool/src/rsc/assignments.csv");
        CSVReader.pruneEmptyRecords(assignmentList);

    }

}
