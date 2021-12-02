package entry_generator;

import json_api.Entry;
import json_api.ServerEntry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EntryGenerator
{
    private Set<Entry> processingEvents = new Set<>();
    private Random randomGenerator = new Random();
    private BufferedWriter writer = null;
    private JSONArray eventsArray = new JSONArray();

    //Generate multiple events
    public void generateEvents(int nbEvents)
    {
        try
        {
            writer = new BufferedWriter(new FileWriter("files/logs.json"));
            for(int i = 0; i < nbEvents; i++)
            {
                generateEvent();
            }
            //Close the remaining events that are stored in the processing event set
            while(!processingEvents.isEmpty())
            {
                pickProcessingEvent();
            }
            String jsonEntryStringValue = eventsArray.toString(4);
            writer.write(jsonEntryStringValue);
            writer.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    //Generate a single event
    private void generateEvent()
    {
        int eventChoice = randomGenerator.nextInt() % 2;
            // 0 : pick an existing event
            // 1 : generate a new event

        if(!processingEvents.isEmpty() && eventChoice == 0)
        {
            pickProcessingEvent();
        }
        else
        {
            createNewEvent();
        }
    }

    private void pickProcessingEvent()
    {
        //Pick a processing event from the set
        Entry entry = processingEvents.randomKey();
        Entry newEntry;
        //Create a new entry for the event in the json file
        if(entry instanceof ServerEntry)
        {
            ServerEntry entry1 = (ServerEntry) entry;
            newEntry = new ServerEntry(entry1.getId(), "FINISHED", entry1.getType(), entry1.getHostname());
        }
        else
        {
            newEntry = new Entry(entry.getId(), "FINISHED");
        }
        JSONObject jsonEntry = new JSONObject(newEntry);
        eventsArray.put(jsonEntry);
        //Remove the event from the processing events set
        processingEvents.remove(entry);
    }

    private void createNewEvent()
    {
        //Create a new event
        String event = generateRandomEntryName();
        int eventType = randomGenerator.nextInt() % 2;
        // 0 : simple entry
        // 1 : server entry
        Entry newEntry;
        if(eventType == 0)
        {
            newEntry = new Entry(event, "STARTED");
        }
        else
        {
            //Supposition : only one type of APPLICATION_LOG and hostname, since those informations are irrelevant to test the program
            newEntry = new ServerEntry(event, "STARTED", "APPLICATION_LOG", "12345");
        }
        //Create a new entry for the event in the json file
        JSONObject jsonEntry = new JSONObject(newEntry);
        eventsArray.put(jsonEntry);
        //Add the newly created event in the processing events set
        processingEvents.add(newEntry);
    }

    //Generate a random name for the event - in 10 characters length
    private String generateRandomEntryName()
    {
        byte[] array = new byte[10];
        randomGenerator.nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    //Set of processing Events - based on a stackoverflow answer
    // https://stackoverflow.com/questions/12385284/how-to-select-a-random-key-from-a-hashmap-in-java
    private class Set<A>
    {
        private ArrayList<A> contents = new ArrayList<A>();
        private HashMap<A, Integer> indices = new HashMap<A, Integer>();
        private Random randomGenerator = new Random();

        //selects random element in constant time
        A randomKey()
        {
            return contents.get(randomGenerator.nextInt(contents.size()));
        }

        //adds new element in constant time
        void add(A a)
        {
            indices.put(a, contents.size());
            contents.add(a);
        }

        //removes element in constant time
        void remove(A a)
        {
            int index = indices.get(a);
            contents.set(index, contents.get(contents.size()-1));
            indices.put(contents.get(index), index);
            contents.remove((int)(contents.size()-1));
            indices.remove(a);
        }

        boolean isEmpty()
        {
            return indices.isEmpty();
        }
    }
}
