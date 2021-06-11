package api.commands;

import api.shared.ResponseHandler;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.STATUSCODES;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Command implements Runnable {
    protected HashMap<String, String> map;
    protected Connection dbConn;
    protected CallableStatement proc;
    protected PreparedStatement func;
    protected Statement query;
    protected ResultSet set;
    protected Map<String, String> details = new HashMap<String, String>();
    protected JsonNodeFactory nf = JsonNodeFactory.instance;

    protected ObjectNode root = nf.objectNode();


    public abstract void execute() throws Exception;
    public String authorize() throws Exception{
        return STATUSCODES.SUCCESS;
    }


    public void setMap(HashMap<String, String> map) {
        this.map=map;
    }

    @Override
    public void run() {
        try {
            String statusCode = authorize();
            if(statusCode.equals(STATUSCODES.SUCCESS))
                execute();
            else
                ResponseHandler.handleError("not authorized", statusCode, map.get("queue"), map.get("correlation_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}