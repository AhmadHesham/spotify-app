package db;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.postgresql.Driver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;

public class RunningDBScript {
    public static void main(String args[]) throws Exception {
        //Registering the Driver
        DriverManager.registerDriver(new Driver());
        //Getting the connection
        String mysqlUrl = "jdbc:postgresql://localhost/postgres";
        Connection con = DriverManager.getConnection(mysqlUrl, "postgres", "postgres");
        System.out.println("Connection established......");
        //Initialize the script runner
        ScriptRunner sr = new ScriptRunner(con);
        //Creating a reader object
        Reader reader = new BufferedReader(new FileReader("./database/CREATE_TABLES.sql"));
        //Running the script
        sr.runScript(reader);
        //Registering the Driver
//        Connection con = PostgresConfig.getDataSource().getConnection();
        //Initialize the script runner
//        ScriptRunner sr = new ScriptRunner(con);
        //Creating a reader object
//        Reader reader = new BufferedReader(new FileReader("./database/CREATE_TABLES.sql"));
        //Running the script
//        sr.runScript(reader);
    }
}
