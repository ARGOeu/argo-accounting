package org.accounting.system.services.clients;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GroupMembership {

    private String id;
    private Group group;
    private String name;
    private boolean active;
    private boolean requireApprovalForExtension;
    private boolean requireApproval;
    private boolean visibleToNotMembers;
    private boolean multiselectRole;
    private List<String> groupRoles;
    private boolean commentsNeeded;
    private String commentsLabel;
    private String commentsDescription;

    @Getter
    @Setter
    public static class Group {

        private String id;
        private String name;
        private Map<String, List<String>> attributes;
    }
}
