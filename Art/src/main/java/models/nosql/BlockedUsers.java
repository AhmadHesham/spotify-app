package models.nosql;

import java.util.ArrayList;
import java.util.UUID;

public class BlockedUsers {

    String id;
    String user_id;
    ArrayList<BlockedUser> blocked_users;

    public BlockedUsers(String user_id, String blocked_id){
        id = UUID.randomUUID().toString();
        this.user_id = user_id;
        //BlockedUsers will be an array of IDs only for now
        ArrayList<BlockedUser> blockedList = new ArrayList<BlockedUser>();
        this.blocked_users = blockedList;
    }

    public static BlockedUsers createBlockedUser(String user_id, String blocked_id){
        return new BlockedUsers(user_id, blocked_id);
    }
}
