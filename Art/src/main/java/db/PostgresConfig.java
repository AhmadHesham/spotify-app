package db;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PostgresConfig {
    private static String DBUser;
    private static String DBName;
    private static String DBPassword;
    private static String DBHost;
    private static String DBPort;
    private static String DBURL;
    private static String DB_INIT_CONNECTIONS;
    private static String DB_MAX_CONNECTIONS;
    private static PoolingDriver dbDriver;
    private static PoolingDataSource<PoolableConnection> dataSource;


    public static void readonfFile() throws Exception {
        try {
            String file = System.getProperty("user.dir") + "/postgres.conf";

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
                        setDBUser(matcher.group(1));
                    else
                        throw new Exception("empty user in Postgres.conf");
                }
                if (lines.get(i).startsWith("database")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        setDBName(matcher.group(1));
                    else
                        throw new Exception("empty database name in Postgres.conf");
                }
                if (lines.get(i).startsWith("pass")) {
                    matcher = pattern.matcher(lines.get(i));
                    matcher.find();
                    setDBPassword(matcher.group(1));
                }
                if (lines.get(i).startsWith("host")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        setDBHost(matcher.group(1));
                    else
                        setDBHost("localhost");
                }
                if (lines.get(i).startsWith("port")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        setDBPort(matcher.group(1));
                    else
                        setDBPort("5432");
                }
                if (lines.get(i).startsWith("init")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        PostgresConfig.DB_INIT_CONNECTIONS = matcher.group(1);
                    else
                        PostgresConfig.DB_INIT_CONNECTIONS = "10";
                }
                if (lines.get(i).startsWith("max")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        PostgresConfig.DB_MAX_CONNECTIONS = matcher.group(1);
                    else
                        PostgresConfig.DB_MAX_CONNECTIONS = "50";
                }
            }
            System.out.println(DBHost);
            if (DBHost.equals("localhost")) {
                setDBURL("jdbc:postgresql://" + DBHost + ":" + DBPort + "/" + DBName);

            } else {
                setDBURL("jdbc:postgresql://" + DBHost + ":" + DBPort + "/" + DBName);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            setDBName("postgres");
            setDBPassword("postgres");
            setDBHost("localhost");
            setDBUser("postgres");
            setDBPort("5432");
            setDBURL("jdbc:postgresql://" + DBHost + ":" + DBPort + "/" + DBName);
            PostgresConfig.DB_INIT_CONNECTIONS = "10";
            PostgresConfig.DB_MAX_CONNECTIONS = "50";
        }
    }


    private static boolean formatURL() {

        Pattern pattern = Pattern.compile("^\\w+:\\w+:\\/{2}\\w+:\\d+\\/\\w+(?:\\W|\\w)*$");
        Matcher matcher = pattern.matcher(DBURL);
        return matcher.matches();
    }

    public static String getDBUser() {
        return DBUser;
    }

    public static void setDBUser(String DBUser) {
        PostgresConfig.DBUser = DBUser;
    }

    public static String getDBName() {
        return DBName;
    }

    public static void setDBName(String DBName) {
        PostgresConfig.DBName = DBName;
    }

    public static String getDBPassword() {
        return DBPassword;
    }

    public static void setDBPassword(String DBPassword) {
        PostgresConfig.DBPassword = DBPassword;
    }

    public static String getDBHost() {
        return DBHost;
    }

    public static void setDBHost(String DBHost) {
        PostgresConfig.DBHost = DBHost;
    }

    public static String getDBPort() {
        return DBPort;
    }

    public static void setDBPort(String DBPort) {
        PostgresConfig.DBPort = DBPort;
    }

    public static String getDBURL() {
        return DBURL;
    }

    public static void setDBURL(String DBURL) {
        PostgresConfig.DBURL = DBURL;
    }

    public static void initSource() {
        if(dataSource!=null){
            return;
        }
        try {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException ex) {
//                LOGGER.log(Level.SEVERE,
//                        "Error loading Postgres driver: " + ex.getMessage(), ex);
                ex.printStackTrace();
            }


            System.out.println("Used Config File For DB");


            System.out.println("inside init");
            Properties props = new Properties();
            props.setProperty("user", DBUser);
            props.setProperty("password", DBPassword);
            props.setProperty("initialSize", DB_INIT_CONNECTIONS);
            props.setProperty("maxActive", DB_MAX_CONNECTIONS);

            ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                    DBURL, props);
            PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                    connectionFactory, null);
            poolableConnectionFactory.setPoolStatements(true);

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxIdle(Integer.parseInt(DB_INIT_CONNECTIONS));
            poolConfig.setMaxTotal(Integer.parseInt(DB_MAX_CONNECTIONS));

            ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
                    poolableConnectionFactory, poolConfig);
            poolableConnectionFactory.setPool(connectionPool);

            Class.forName("org.apache.commons.dbcp2.PoolingDriver");
            dbDriver = (PoolingDriver) DriverManager
                    .getDriver("jdbc:apache:commons:dbcp:");
            dbDriver.registerPool(DBName, connectionPool);

            dataSource = new PoolingDataSource<>(connectionPool);

            System.out.println("data source is ready");
        } catch (Exception ex) {
//            LOGGER.log(Level.SEVERE, "Got error initializing data source: "
//                    + ex.getMessage(), ex);

            ex.printStackTrace();
        }
    }

    public static PoolingDataSource<PoolableConnection> getDataSource() {
        return dataSource;
    }


    public static void setMaxDBConnections(int maxDBConnections) throws ClassNotFoundException, SQLException {
        DB_MAX_CONNECTIONS = maxDBConnections+"";
        Properties props = new Properties();
        props.setProperty("user", DBUser);
        props.setProperty("password", DBPassword);
        props.setProperty("initialSize", DB_INIT_CONNECTIONS);
        props.setProperty("maxActive", DB_MAX_CONNECTIONS);

        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                DBURL, props);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                connectionFactory, null);
        poolableConnectionFactory.setPoolStatements(true);

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(Integer.parseInt(DB_INIT_CONNECTIONS));
        poolConfig.setMaxTotal(Integer.parseInt(DB_MAX_CONNECTIONS));

        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
                poolableConnectionFactory, poolConfig);
        poolableConnectionFactory.setPool(connectionPool);

        Class.forName("org.apache.commons.dbcp2.PoolingDriver");
        dbDriver = (PoolingDriver) DriverManager
                .getDriver("jdbc:apache:commons:dbcp:");
        dbDriver.registerPool(DBName, connectionPool);

        dataSource = new PoolingDataSource<>(connectionPool);

    }

}
