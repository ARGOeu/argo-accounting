package org.accounting.system.parsetypes;

import org.json.simple.JSONObject;

import java.util.List;

public interface ParseJson {

    List<String> availableTypes(JSONObject jsonObject);
}