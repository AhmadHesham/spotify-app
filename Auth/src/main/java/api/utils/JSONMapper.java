package api.utils;

import com.couchbase.client.core.deps.com.fasterxml.jackson.core.type.TypeReference;
import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

public class JSONMapper {

    private String json;

    public JSONMapper(String json){
        this.json = json;
    }

    public HashMap<String, String> toHash() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json,
                new TypeReference<HashMap<String, String>>() {
                });
    }

}
