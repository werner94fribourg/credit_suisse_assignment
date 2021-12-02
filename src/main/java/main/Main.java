package main;

import entry_generator.EntryGenerator;
import entry_processor.EntryProcessor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main
{
    static final String DB_URL = "jdbc:h2:./files/my";
    static final String USER = "root";
    static final String PASS = "password";
    static Connection conn = null;
    public static void main(String[] args)
    {
        EntryGenerator generator = new EntryGenerator();
        generator.generateEvents(1000);
        //Open a connection
        //Class.forName("org.h2.Driver").newInstance();
        try{
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database created");
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
            EntryProcessor processor = new EntryProcessor(conn);
            processor.processData();
            conn.close();
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
        }
    }
}
