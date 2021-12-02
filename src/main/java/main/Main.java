package main;

import entry_generator.EntryGenerator;
import entry_processor.EntryProcessor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main
{
    static final String DB_URL = "jdbc:h2:./files/my";
    static final String USER = "root";
    static final String PASS = "password";
    static Connection conn = null;
    private static final Logger LOGGER = Logger.getLogger("Main logger");
    public static void main(String[] args)
    {
        LOGGER.info("Start of the program");
        LOGGER.info("Instanciation of the generator");
        EntryGenerator generator = new EntryGenerator();
        generator.generateEvents(1000);
        //Open a connection
        //Class.forName("org.h2.Driver").newInstance();
        LOGGER.info("Connection to the h2 database");
        try{
            LOGGER.info("Start of the connection");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            LOGGER.info("Database created");
            LOGGER.info("Creation of the alert table in the database");
            Statement statement1 = conn.createStatement();
            String cleanDatabase = "DROP TABLE IF EXISTS alert";
            statement1.execute(cleanDatabase);
            statement1.close();
            Statement statement2 = conn.createStatement();
            String tableCreation = "CREATE TABLE alert (" +
                    "id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
                    "eventid VARCHAR(40) NOT NULL," +
                    "duration INT(10) NOT NULL," +
                    "type VARCHAR(40)," +
                    "host VARCHAR(40)" +
                    ")";
            statement2.execute(tableCreation);
            statement2.close();
            LOGGER.info("table alert successfully created");
            LOGGER.info("Instanciation of the processor");
            EntryProcessor processor = new EntryProcessor(conn);
            LOGGER.info("Processor successfully instanciated");
            LOGGER.info("Processing of the entries in the database");
            processor.processData();
            LOGGER.info("End of the entry processing");
            LOGGER.info("Retrieve the total number of entries in the database");
            Statement statement3 = conn.createStatement();
            String entriesRetrieving = "SELECT COUNT(*) as total FROM alert";
            ResultSet result = statement3.executeQuery(entriesRetrieving);
            int nbElements = 0;
            if(result.next()){
                nbElements = result.getInt("total");
            }
            LOGGER.info("Nb of elements : "+Integer.toString(nbElements));
            statement3.close();
            conn.close();
        }
        catch (SQLException exception)
        {
            LOGGER.log(Level.SEVERE, "Error while trying to connect to the database");
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            LOGGER.log(Level.SEVERE, sw.toString());
        }
    }
}
