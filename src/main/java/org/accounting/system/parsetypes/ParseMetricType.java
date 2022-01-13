package org.accounting.system.parsetypes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@MetricType
@ApplicationScoped
public class ParseMetricType extends ParseType {
    @Override
    public List<String> availableTypes(JSONObject jsonObject) {
        return (List<String>) jsonObject.values().stream().flatMap(a->(((JSONArray) a).stream())).map(obj->(((JSONObject) obj).get("metric_type"))).collect(Collectors.toList());
    }
}
