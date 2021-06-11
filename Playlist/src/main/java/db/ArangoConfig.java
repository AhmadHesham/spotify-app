package db;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArangoConfig {

    public static ArangoDatabase arangoDatabase;
    public static String arangoUser;
    public static String arangoPassword;


    public static void initialize(String database) throws Exception {
        try {
            String file = System.getProperty("user.dir") + "/arango.conf";

            System.out.println(file);
            java.util.List<String> lines = new ArrayList<String>();
            Pattern pattern = Pattern.compile("\\[(.+)\\]");
            Matcher matcher;
            Stream<String> stream = Files.lines(Paths.get(file));
            lines = stream.filter(line -> !line.startsWith("#")).collect(Collectors.toList());
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("user")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        setArangoUser(matcher.group(1));
                    else
                        throw new Exception("empty user in arango.conf");
                }
                if (lines.get(i).startsWith("pass")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        setArangoPassword(matcher.group(1));
                    else
                        setArangoPassword("");
                }


            }
            ArangoDB arangoDB = new ArangoDB.Builder().user(arangoUser).password(arangoPassword).build();

            try {
                arangoDatabase = arangoDB.db(database);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        catch (Exception e){
            setArangoUser("root");
            setArangoPassword("");
            ArangoDB arangoDB = new ArangoDB.Builder().user(arangoUser).password(arangoPassword).build();
            arangoDatabase = arangoDB.db(database);
            e.printStackTrace();
        }
    }

    public static void setArangoUser(String arangoUser){
        ArangoConfig.arangoUser = arangoUser;
    }
    public static void setArangoPassword(String arangoPassword){
        ArangoConfig.arangoPassword = arangoPassword;
    }



    public static ArangoCollection getCollection(String collection){
        return arangoDatabase.collection(collection);
    }



}
