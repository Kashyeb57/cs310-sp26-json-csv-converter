package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.*;
import java.util.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // Initialize the CSV Reader
            StringReader stringReader = new StringReader(csvString);
            CSVReader reader = new CSVReader(stringReader);
            List<String[]> fullCsvData = reader.readAll();
            
            // Prepare JSON Containers
            JsonObject json = new JsonObject();
            JsonArray colHeadings = new JsonArray();
            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();
            
            // Extract Column Headings (The first row at index 0)
            String[] headerRow = fullCsvData.get(0);
            for (String heading : headerRow) {
                colHeadings.add(heading);
            }
            
            // Process Data Rows (Loop starting from index 1)
            for (int i = 1; i < fullCsvData.size(); i++) {
                String[] row = fullCsvData.get(i);

                // Add the first element to ProdNums
                prodNums.add(row[0]);
                
                // Create a sub-array for the remaining data
                JsonArray dataRow = new JsonArray();
                for (int j = 1; j < row.length; j++) { 
                    if (j == 2 || j == 3) {
                        dataRow.add(Integer.parseInt(row[j]));
                    } else {
                        dataRow.add(row[j]);
                    }
                }
                data.add(dataRow);
            }
            
            // 5. Final Assembly
            json.put("ProdNums", prodNums);
            json.put("ColHeadings", colHeadings);
            json.put("Data", data);

            result = Jsoner.serialize(json);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            // Parse the JSON string 
            JsonObject json = (JsonObject) Jsoner.deserialize(jsonString);
            
            // Extract arrays from the JSON object
            JsonArray colHeadings = (JsonArray) json.get("ColHeadings");
            JsonArray prodNums = (JsonArray) json.get("ProdNums");
            JsonArray data = (JsonArray) json.get("Data");
            
            // reate a list of String arrays for the CSV writer
            List<String[]> csvRows = new ArrayList<>();
            
            // Add the Column Headings as the first row
            String[] headers = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                headers[i] = colHeadings.get(i).toString();
            }
            csvRows.add(headers);
            
            // Reconstruct data rows
            for (int i = 0; i < prodNums.size(); i++) {
                String[] row = new String[headers.length];
                row[0] = prodNums.get(i).toString(); // ProdNum
                
                JsonArray dataRow = (JsonArray) data.get(i);
                for (int j = 0; j < dataRow.size(); j++) {
                    Object val = dataRow.get(j);
                    
                    // Special formatting for Episode column
                    if (j == 2) { 
                        row[j + 1] = String.format("%02d", Integer.parseInt(val.toString()));
                    } else {
                        row[j + 1] = val.toString();
                    }
                }
                csvRows.add(row);
            }
            
            // 6. Generate the CSV string using CSVWriter
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            csvWriter.writeAll(csvRows);
            
            result = writer.toString();
            

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
