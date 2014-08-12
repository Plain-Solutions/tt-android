package org.ssutt.android.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.ssutt.android.domain.Lesson.Lesson;
import org.ssutt.android.domain.Lesson.Subgroup;
import org.ssutt.android.domain.Lesson.Subject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LessonDeserializer implements JsonDeserializer<Lesson> {
    @Override
    public Lesson deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Lesson lesson = new Lesson();

        JsonObject jsonLesson = jsonElement.getAsJsonObject();
        lesson.setDay(jsonLesson.get("day").getAsInt());
        lesson.setSequence(jsonLesson.get("sequence").getAsInt());

        List<Subject> subjects = new ArrayList<Subject>();
        for (JsonElement curJsonSubject : jsonLesson.get("subject").getAsJsonArray()) {
            Subject subject = new Subject();
            JsonObject jsonSubject = curJsonSubject.getAsJsonObject();

            subject.setName(jsonSubject.get("name").getAsString());
            subject.setActivity(jsonSubject.get("activity").getAsString());
            subject.setParity(jsonSubject.get("parity").getAsInt());

            List<Subgroup> subgroups = new ArrayList<Subgroup>();
            for (JsonElement curSubgroup : jsonSubject.get("subgroups").getAsJsonArray()) {
                Subgroup subgroup = new Subgroup();
                JsonObject jsonSubgroup = curSubgroup.getAsJsonObject();

                subgroup.setSubgroup(jsonSubgroup.get("subgroup").getAsString());
                subgroup.setTeacher(jsonSubgroup.get("teacher").getAsString());
                subgroup.setLocation(jsonSubgroup.get("location").getAsString());

                subgroups.add(subgroup);
            }

            subject.setSubgroup(subgroups);
            subjects.add(subject);
        }

        lesson.setSubject(subjects);
        return lesson;
    }
}
