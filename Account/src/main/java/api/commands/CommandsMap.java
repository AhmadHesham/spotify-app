package api.commands;

import cache.Redis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapAccounts = new ConcurrentHashMap<>();


    private static Properties prop = new Properties();

    public static void initialize() throws Exception {
        try {
            prop.load(CommandsMap.class.getClassLoader().getResourceAsStream("AccountCommands.properties"));
            Enumeration<?> propNames = prop.propertyNames();
            while (propNames.hasMoreElements()) {
                Object current = propNames.nextElement();
                cmdMapAccounts.put(current.toString(),
                        Class.forName("api.commands." + prop.get(current.toString()).toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public static void initialize() {
//
//        cmdMapAccounts =
//
//        cmdMapAccounts.put("get-reports", GetReports.class);
//        cmdMapAccounts.put("get-report", GetReport.class);
//        cmdMapAccounts.put("get-report-user", GetReportByUser.class);
//        cmdMapAccounts.put("get-report-reported", GetReportByReported.class);
//        cmdMapAccounts.put("edit-report", EditReport.class);
//        cmdMapAccounts.put("create-report", CreateReport.class);
//        cmdMapAccounts.put("delete-report", DeleteReport.class);
//
//        cmdMapAccounts.put("disable-premium", DisablePremium.class);
//        cmdMapAccounts.put("enable-premium", EnablePremium.class);
//
//        cmdMapAccounts.put("get-account", GetAccount.class);
//        cmdMapAccounts.put("get-account-username", GetAccountUsername.class);
//        cmdMapAccounts.put("get-account-email", GetAccountEmail.class);
//        cmdMapAccounts.put("get-accounts", GetAccounts.class);
//        cmdMapAccounts.put("delete-account", DeleteAccount.class);
//        cmdMapAccounts.put("edit-account", EditAccount.class);
//        cmdMapAccounts.put("block-user", BlockUser.class);
//        cmdMapAccounts.put("unblock-user", UnblockUser.class);
//        cmdMapAccounts.put("get-blocked-users", GetBlockedUsers.class);
//
//    }
//
    public static Class<?> queryClass(String cmd, String queue) {
        try {
            if (Redis.hasKey("account-freeze") && Redis.get("account-freeze").equals("true")) {
                return FROZEN.class;
            }
            switch (queue) {
                case "account":
                    return cmdMapAccounts.get(cmd);
                default:
                    return FOUROFOUR.class;
            }
        } catch (Exception e) {
            return FOUROFOUR.class;
        }

    }

    public static void replace(String key, Class<?> cls, String fileName) {
        if (cmdMapAccounts.containsKey(key)) {
            cmdMapAccounts.replace(key, cls);
        } else {
            cmdMapAccounts.put(key, cls);
            prop.setProperty(key, fileName);
            try {
                prop.store(new FileOutputStream("Account/src/main/resources/AccountCommands.properties"), null);
            } catch (IOException e) {
                System.out.println("Error in updating .properties file");
            }
        }
    }
}
