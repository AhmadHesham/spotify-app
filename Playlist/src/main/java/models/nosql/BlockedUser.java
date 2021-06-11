package models.nosql;

public class BlockedUser {
    String user_id;
    String user_name;


    public BlockedUser(String user_id, String user_name){
        this.user_id= user_id;
        this.user_name=user_name;
    }
    public BlockedUser(){
    }
    public void setUserId(String user_id){ this.user_id=user_id;}
    public void setUserName(String user_name){ this.user_name=user_name;}
    public String getUserId(){ return this.user_id;}
    public String getUserName(){ return this.user_name;}
}
