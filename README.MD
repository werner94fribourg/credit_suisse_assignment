# Assignment
Credit Suisse postulation assignment

# Run the project - instructions to type in the console

mvn install:install-file -Dfile=src/main/resources/json-20210307.jar -DgroupId=org.json -DartifactId=json -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=src/main/resources/h2-1.4.200.jar -DgroupId=org.h2 -DartifactId=h2 -Dversion=1.0 -Dpackaging=jar

mvn compile

mvn clean install

mvn exec:java -Dexec.mainClass="main.Main"

# Description
I've tried to send this project last time by e-mail, after a week, as said in the instructions. Here is what I was saying in the e-mail :

<i>
ou will find as an attachment what i've done for the assignment.

Unfortunately, I wasn't able to improve what I achieved to do yesterday and today (my only days where I found the time to do it) due to the lack of time I had with my weekly busy schedule due to the civil service.

Here what I would do to improve my solution :

-Add logger messages when we start the program, simulate the entry creation, connect to the database, process an entry and close the program
-Transform the entry creation into multithreaded unit testing programs, with a single eventsArray (JSONArray) as a shared resource accessed through multithreading, that will be updated every time we create a new event
-Transform the main program by deleting the entry generator and transform it into a server that will listen to changes given to the json file and process the next entry in the file. The server will be continuously running.
</i>
Today, I just added log entries to the program and created a maven build for it (I was using my IDEA). I also modified the Main file to add, at the end of it, the number of entities that fulfill the condition posed in the exercice.

I hope that it will help you in your evaluation.

Best regards,
Werner Schmid
