package persistence;

import model.Budget;
import model.BudgetList;
import org.json.*;
import java.io.*;

//NOTE: Modelled around JsonSerializationDemo
//      LINK: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class Writer {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    //EFFECTS: constructs writer to write to destination file
    public Writer(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    //          be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of workroom to file
    public void write(BudgetList budgetList) {
        JSONObject json = budgetList.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}
