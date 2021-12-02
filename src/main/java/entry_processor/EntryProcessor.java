package entry_processor;

import json_api.Entry;
import json_api.ServerEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntryProcessor {
    private JSONTokener tokener;
    private HashMap<String, Entry> entries = new HashMap();
    private BufferedReader reader = null;
    private final int TIME_INTERVAL = 5000000;
    private Connection conn = null;

    private static final Logger LOGGER = Logger.getLogger("Processor Logger");

    public EntryProcessor(Connection conn) {
        this.conn = conn;
    }

    //Processing the json data
    public void processData() throws SQLException
    {
        try
        {
            LOGGER.info("Opening of the logs file to process it");
            reader = new BufferedReader(new FileReader("files/logs.json"));
            LOGGER.info("File successfully opened");
            LOGGER.info("Transformation of the json content of the file into a Json tokener object in Java");
            tokener = new JSONTokener(reader);
            LOGGER.info("Token transformation successfully done");
            LOGGER.info("Transformation into a JSON array");
            JSONArray array = (JSONArray) tokener.nextValue();
            LOGGER.info("Array transformation successfully done");
            LOGGER.info("Store the entries into the database");
            while(!array.isEmpty())
            {
                JSONObject object = (JSONObject) array.remove(0);
                String id = (String) object.get("id");
                LOGGER.info("Entry "+id+" processing");
                int timestamp = (int) object.get("timestamp");
                if(entries.get(id) != null)
                {
                    LOGGER.info("The entry has be processed earlier - check if we can store it in the database");
                    Entry entry = entries.remove(id);
                    storeEntryInDB(entry, timestamp);
                }
                else
                {
                    LOGGER.info("First time we encounter the entry - store it in the Hashtable");
                    Entry newEntry;
                    if(object.has("type"))
                    {
                        newEntry = new ServerEntry(id, (String)object.get("state"), timestamp, (String)object.get("type"), (String)object.get("hostname"));
                    }
                    else
                    {
                        newEntry = new Entry(id, (String)object.get("state"), timestamp);
                    }
                    entries.put(id, newEntry);
                }
                LOGGER.info("End of entry processing");
            }
            LOGGER.info("End of checking all the entries");
            reader.close();
        }catch (IOException exception)
        {
            LOGGER.log(Level.SEVERE, "Error while opening or closing the file");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            LOGGER.log(Level.SEVERE, sw.toString());
        }
    }

    //Store entry into the database
    public void storeEntryInDB(Entry entry, int newTimeStamp) throws SQLException
    {
        LOGGER.info("Storage of the entry in the database");
        int storedTimeStamp = entry.getTimestamp();
        int difference = Math.abs(newTimeStamp-storedTimeStamp);
        if(difference > TIME_INTERVAL)
        {
            LOGGER.info("Store entry "+entry.getId()+"Into database - Time interval : "+Integer.toString(difference));
            //Store in database
            PreparedStatement statement;
            if(entry instanceof ServerEntry)
                statement = conn.prepareStatement("INSERT INTO alert VALUES (NULL,?,?,?,?)");
            else
                statement = conn.prepareStatement("INSERT INTO alert VALUES (NULL,?,?,NULL,NULL)");
            statement.setString(1, entry.getId());
            statement.setInt(2, difference);
            if(entry instanceof ServerEntry)
            {
                ServerEntry entry1 = (ServerEntry) entry;
                statement.setString(3, entry1.getType());
                statement.setString(4, entry1.getHostname());
            }
            statement.executeUpdate();
            statement.close();
            LOGGER.info("Entry successfully stored in the database.");
        }
        else
        {
            LOGGER.info("The entry wasn't stored in the database - Time interval : "+Integer.toString(difference));
        }
        LOGGER.info("End of the storage");
    }
}
