package org.accounting.system.dtos.ams;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Message {

    private Map<String, String> attributes;
    private String data;
    private String messageId;
}
