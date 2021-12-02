package json_api;

import java.sql.Timestamp;

public class Entry
{
    private String id;
    private String state;
    private Timestamp timestamp;

    public Entry(String id, String state)
    {
        this.id = id;
        this.state = state;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Entry(String id, String state, int timestamp)
    {
        this.id = id;
        this.state = state;
        this.timestamp = new Timestamp(timestamp);
    }

    public String getId()
    {
        return id;
    }

    public String getState()
    {
        return state;
    }

    public int getTimestamp()
    {
        return timestamp.getNanos();
    }
}
