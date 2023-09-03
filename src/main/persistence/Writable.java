package persistence;

//NOTE: Modelled around JsonSerializationDemo
//      LINK: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import org.json.JSONObject;

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
