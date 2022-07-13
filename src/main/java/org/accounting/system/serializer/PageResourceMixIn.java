package org.accounting.system.serializer;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.List;

public abstract class PageResourceMixIn {

    @JsonRawValue abstract List<String> getContent();
}