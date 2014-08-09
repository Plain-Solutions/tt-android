package org.ssutt.android.domain.Lesson;

import java.util.List;

public class Subject {
    private String name;
    private String activity;
    private int parity;
    private List<Subgroup> subgroup;

    public Subject() {
    }

    public Subject(String name, String activity, int parity, List<Subgroup> subgroup) {
        this.name = name;
        this.activity = activity;
        this.parity = parity;
        this.subgroup = subgroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public List<Subgroup> getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(List<Subgroup> subgroup) {
        this.subgroup = subgroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (parity != subject.parity) return false;
        if (activity != null ? !activity.equals(subject.activity) : subject.activity != null) return false;
        if (name != null ? !name.equals(subject.name) : subject.name != null) return false;
        if (subgroup != null ? !subgroup.equals(subject.subgroup) : subject.subgroup != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (activity != null ? activity.hashCode() : 0);
        result = 31 * result + parity;
        result = 31 * result + (subgroup != null ? subgroup.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Subject{")
                .append("name='").append(name).append('\'')
                .append(", activity='").append(activity).append('\'')
                .append(", parity=").append(parity)
                .append(", subgroup=").append(subgroup)
                .append('}').toString();
    }
}
