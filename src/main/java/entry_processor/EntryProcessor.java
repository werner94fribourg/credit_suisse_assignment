package entry_processor;

import json_api.Entry;
import json_api.ServerEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class EntryProcessor {
    private JSONTokener tokener;
    private HashMap<String, Entry> entries = new HashMap();
    private BufferedReader reader = null;
    private final int TIME_INTERVAL = 5000000;
    private Connection conn = null;

    public EntryProcessor(Connection conn) {
        this.conn = conn;
    }

    //Processing the json data
    public void processData() throws SQLException
    {
        try
        {
            reader = new BufferedReader(new FileReader("files/logs.json"));
            tokener = new JSONTokener(reader);
            JSONArray array = (JSONArray) tokener.nextValue();
            while(!array.isEmpty())
            {
                JSONObject object = (JSONObject) array.remove(0);
                String id = (String) object.get("id");
                int timestamp = (int) object.get("timestamp");
                if(entries.get(id) != null)
                {
                    Entry entry = entries.remove(id);
                    storeEntryInDB(entry, timestamp);
                }
                else
                {
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
            }
            reader.close();
        }catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    //Store entry into the database
    public void storeEntryInDB(Entry entry, int newTimeStamp) throws SQLException
    {
        int storedTimeStamp = entry.getTimestamp();
        int difference = Math.abs(newTimeStamp-storedTimeStamp);
        if(difference > TIME_INTERVAL)
        {
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
        }
    }
}
