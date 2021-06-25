package api.commands;

import cache.Redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommandsMap {
    private static ConcurrentMap<String, Class<?>> cmdMapAccounts;


    public static void initialize() {

        cmdMapAccounts = new ConcurrentHashMap<>();

        cmdMapAccounts.put("get-reports", GetReports.class);
        cmdMapAccounts.put("get-report", GetReport.class);
        cmdMapAccounts.put("get-report-user", GetReportByUser.class);
        cmdMapAccounts.put("get-report-reported", GetReportByReported.class);
        cmdMapAccounts.put("edit-report", EditReport.class);
        cmdMapAccounts.put("create-report", CreateReport.class);
        cmdMapAccounts.put("delete-report", DeleteReport.class);

        cmdMapAccounts.put("disable-premium", DisablePremium.class);
        cmdMapAccounts.put("enable-premium", EnablePremium.class);

        cmdMapAccounts.put("get-account", GetAccount.class);
        cmdMapAccounts.put("get-account-username", GetAccountUsername.class);
        cmdMapAccounts.put("get-account-email", GetAccountEmail.class);
        cmdMapAccounts.put("get-accounts", GetAccounts.class);
        cmdMapAccounts.put("delete-account", DeleteAccount.class);
        cmdMapAccounts.put("edit-account", EditAccount.class);
        cmdMapAccounts.put("block-user", BlockUser.class);
        cmdMapAccounts.put("unblock-user", UnblockUser.class);
        cmdMapAccounts.put("get-blocked-users", GetBlockedUsers.class);

    }

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


}
