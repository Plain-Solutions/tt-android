package org.ssutt.android.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.ssutt.android.domain.Group;

import java.lang.reflect.Type;

public class GroupDeserializer implements JsonDeserializer<Group>{

    @Override
    public Group deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Group(jsonElement.getAsString());
    }
}
