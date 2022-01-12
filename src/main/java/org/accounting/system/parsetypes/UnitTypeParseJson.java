package org.accounting.system.parsetypes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@UnitType
@ApplicationScoped
public class UnitTypeParseJson implements ParseJson{
    @Override
    public List<String> availableTypes(JSONObject jsonObject) {
        return (List<String>) jsonObject.values().stream().flatMap(a->(((JSONArray) a).stream())).map(obj->(((JSONObject) obj).get("symbol"))).collect(Collectors.toList());
    }
}
