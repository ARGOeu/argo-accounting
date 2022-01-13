package org.accounting.system.parsetypes;

import io.quarkus.cache.CacheResult;
import io.vavr.control.Try;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class ParseType implements AbstractParseType {

    @CacheResult(cacheName = "types-to-json")
    public FileToJson fileToJson(String path) throws IOException, ParseException {

        Object object = new JSONParser().parse(new FileReader(path));
        JSONObject jo = (JSONObject) object;
        List<String> availableTypes = availableTypes(jo);
        return FileToJson.builder().fileToString(jo.toJSONString()).availableTypes(availableTypes).build();
    }

    public String getTypes(String path){
        return Try
                .of(()-> fileToJson(path))
                .map(FileToJson::getFileToString)
                .getOrElseThrow(throwable -> {
                    throw new RuntimeException("Internal Server Error.");});
    }

    public Optional<String> searchForType(String path, String type){
        return Try
                .of(()-> fileToJson(path))
                .map(FileToJson::getAvailableTypes)
                .getOrElseThrow(throwable -> {throw new RuntimeException("Internal Server Error.");})
                .stream()
                .filter(t->t.equals(type))
                .findAny();

    }
}
