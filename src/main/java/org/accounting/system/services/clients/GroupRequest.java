package org.accounting.system.services.clients;

import java.util.List;

public class GroupRequest {

    public String name;
    public Attributes attributes;

    public static class Attributes {
        public List<String> description;
    }
}
