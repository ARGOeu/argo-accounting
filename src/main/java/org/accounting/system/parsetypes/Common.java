package org.accounting.system.parsetypes;

import io.quarkus.cache.CacheResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Common {

    @CacheResult(cacheName = "types-to-json")
    public static FileToJson fileToJson(String path, ParseJson parseJson) throws IOException, ParseException {

        Object object = new JSONParser().parse(new FileReader(path));
        JSONObject jo = (JSONObject) object;
        List<String> availableTypes = parseJson.availableTypes(jo);
        return FileToJson.builder().fileToString(jo.toJSONString()).availableTypes(availableTypes).build();
    }

}
