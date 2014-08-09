package org.ssutt.android.deserializer;

import com.google.gson.*;

import org.ssutt.android.domain.Message;

import java.lang.reflect.Type;

public class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject message = jsonElement.getAsJsonObject();
        return new Message(message.get("msg").getAsString());
    }
}
