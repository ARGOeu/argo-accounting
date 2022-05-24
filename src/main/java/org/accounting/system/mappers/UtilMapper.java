package org.accounting.system.mappers;

import org.bson.types.ObjectId;

public class UtilMapper {

    public String asString(ObjectId objectId) {
        return objectId.toString();
    }

    public ObjectId asObjectId(String objectId) {
        return new ObjectId(objectId);
    }
}
