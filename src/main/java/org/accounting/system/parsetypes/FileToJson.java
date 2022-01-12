package org.accounting.system.parsetypes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FileToJson {

    private String fileToString;
    private List<String> availableTypes;
}
