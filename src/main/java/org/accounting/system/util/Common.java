package org.accounting.system.util;

import io.quarkus.cache.CacheResult;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Common {

    @CacheResult(cacheName = "types-to-json")
    public static FileToJson fileToJson(String path) throws IOException, ParseException {

        Object object = new JSONParser().parse(new FileReader(path));
        JSONObject jo = (JSONObject) object;
        List<String> availableTypes = (List<String>) jo.values().stream().flatMap(a->(((JSONArray) a).stream())).map(obj->(((JSONObject) obj).get("name"))).collect(Collectors.toList());
        return FileToJson.builder().fileToString(jo.toJSONString()).availableTypes(availableTypes).build();
    }

}
