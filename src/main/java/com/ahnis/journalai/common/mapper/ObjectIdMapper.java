package com.ahnis.journalai.common.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObjectIdMapper { default ObjectId toObjectId(String id) {
        return (id != null && ObjectId.isValid(id)) ? new ObjectId(id) : null;
    }

    default String toString(ObjectId id) {
        return (id != null) ? id.toHexString() : null;
    }
}
