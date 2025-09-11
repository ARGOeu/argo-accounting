package org.accounting.system.entitlements;

import java.util.*;
import java.util.regex.*;

public class EntitlementUtils {

    private static final Pattern PATTERN =
            Pattern.compile(".*group:([^:]+)(?::((?:[^:]+:)*[^:]+))?:role=([^:]+)$");


    public static Entitlement parse(String raw) {

        var m = PATTERN.matcher(raw);
        if (!m.matches()) return null;

        var group = m.group(1); // π.χ. "accounting", "billing", κλπ
        var hierarchyPart = m.group(2); // μπορεί να είναι null για sys admin
        List<String> hierarchy = new ArrayList<>();
        if (hierarchyPart != null) {
            for (var s : hierarchyPart.split(":")) {
                if (!s.isEmpty()) hierarchy.add(s);
            }
        }
        var role = m.group(3);

        return new Entitlement(group, hierarchy, role, raw);
    }


    public static List<Entitlement> parseEntitlements(List<String> rawEntitlements) {

        List<Entitlement> list = new ArrayList<>();
        for (String raw : rawEntitlements) {
           var e = parse(raw);
            if (e != null) list.add(e);
        }
        return list;
    }
}
