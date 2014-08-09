package org.ssutt.android.deserializer;

import com.google.gson.*;

import org.ssutt.android.domain.Department;

import java.lang.reflect.Type;

public class DepartmentDeserializer implements JsonDeserializer<Department> {
    @Override
    public Department deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        String tag = asJsonObject.get("tag").getAsString();
        String name = asJsonObject.get("name").getAsString();
        return new Department(tag, name);
    }
}
