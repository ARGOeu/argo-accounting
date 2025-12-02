package org.accounting.system.entitlements;

import java.util.List;

public class Entitlement {
    private final String group;
    private final List<String> hierarchy;
    private final String role;
    private final String raw;

    public Entitlement(String group, List<String> hierarchy, String role, String raw) {
        this.group = group;
        this.hierarchy = hierarchy;
        this.role = role;
        this.raw = raw;
    }

    public String getGroup() { return group; }
    public List<String> getHierarchy() { return hierarchy; }
    public String getRole() { return role; }
    public String getRaw() { return raw; }
    public boolean hasHierarchy() { return hierarchy != null && !hierarchy.isEmpty(); }

    @Override
    public String toString() {
        return "Entitlement{group='" + group + "', hierarchy=" + hierarchy + ", role='" + role + "'}";
    }
}