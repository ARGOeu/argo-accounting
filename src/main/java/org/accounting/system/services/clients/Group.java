package org.accounting.system.services.clients;

import java.util.List;

public class Group {
    public String id;
    public String path;
    public Attributes attributes;
    public List<Group> extraSubGroups;
}
