/*
* Copyright 2014 Plain Solutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.ssutt.android.domain.Lesson;

import java.util.List;

public class Lesson {
    private int day;
    private int sequence;
    private List<Subject> subject;

    public Lesson() {
    }

    public Lesson(int day, int sequence, List<Subject> subject) {
        this.day = day;
        this.sequence = sequence;
        this.subject = subject;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public List<Subject> getSubject() {
        return subject;
    }

    public void setSubject(List<Subject> subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lesson lesson = (Lesson) o;

        if (day != lesson.day) return false;
        if (sequence != lesson.sequence) return false;
        if (subject != null ? !subject.equals(lesson.subject) : lesson.subject != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + sequence;
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Lesson{")
                .append("day=").append(day)
                .append(", sequence=").append(sequence)
                .append(", subject=").append(subject)
                .append('}').toString();
    }
}