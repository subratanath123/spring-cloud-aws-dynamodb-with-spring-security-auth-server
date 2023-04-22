package com.authorization.server.converter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.authorization.server.entity.ObjectProperty;
import com.authorization.server.entity.User;
import com.authorization.server.entity.UserPrimaryKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ObjectPropertyConverter implements DynamoDBTypeConverter<String, UserPrimaryKey> {


    @Override
    public String convert(UserPrimaryKey object) {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserPrimaryKey unconvert(String object) {
        try {
            return new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(object, UserPrimaryKey.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
