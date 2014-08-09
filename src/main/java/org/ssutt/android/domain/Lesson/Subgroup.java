package org.ssutt.android.domain.Lesson;

public class Subgroup {
    private String subgroup;
    private String teacher;
    private String location;

    public Subgroup() {
    }

    public Subgroup(String subgroup, String teacher, String location) {
        this.subgroup = subgroup;
        this.teacher = teacher;
        this.location = location;
    }

    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subgroup subgroup1 = (Subgroup) o;

        if (location != null ? !location.equals(subgroup1.location) : subgroup1.location != null) return false;
        if (subgroup != null ? !subgroup.equals(subgroup1.subgroup) : subgroup1.subgroup != null) return false;
        if (teacher != null ? !teacher.equals(subgroup1.teacher) : subgroup1.teacher != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subgroup != null ? subgroup.hashCode() : 0;
        result = 31 * result + (teacher != null ? teacher.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Subgroup{")
                .append("subgroup='").append(subgroup).append('\'')
                .append(", teacher='").append(teacher).append('\'')
                .append(", location='").append(location).append('\'')
                .append('}').toString();
    }
}
