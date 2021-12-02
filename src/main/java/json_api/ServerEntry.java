package json_api;

public class ServerEntry extends Entry
{

    private String type;
    private String hostname;

    public ServerEntry(String id, String state, String type, String hostname)
    {
        super(id, state);
        this.type = type;
        this.hostname = hostname;
    }

    public ServerEntry(String id, String state, int timestamp, String type, String hostname)
    {
        super(id, state, timestamp);
        this.type = type;
        this.hostname = hostname;
    }

    public String getType()
    {
        return type;
    }

    public String getHostname()
    {
        return hostname;
    }
}
