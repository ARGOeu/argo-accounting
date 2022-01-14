package org.accounting.system.parsetypes;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * You can implement this interface {@link AbstractParseType} in order to declare how the JSONObject,
 * which contains the available types, will be parsed. Once the parsing is completed, the result should be a List with the available types.
 */
public interface AbstractParseType {

    List<String> availableTypes(JSONObject jsonObject);

}
