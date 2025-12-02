package org.accounting.system.dtos.ams;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmsMessage {

    private Message message;
    private String subscription;
}
