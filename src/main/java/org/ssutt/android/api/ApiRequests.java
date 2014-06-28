package org.ssutt.android.api;

import static java.lang.String.format;

public abstract class ApiRequests {
    private static final String API_URL = "http://api.ssutt.org:8080/%s";
    private static final String DEPARTMENTS = format(API_URL, "departments");
    private static final String DEPARTMENT_MSG = format(API_URL, "%s/msg");
    private static final String GROUPS = format(API_URL, "%s/groups/?filled=%d");
    private static final String SCHEDULE = format(API_URL, "%s/group/%s");

    public static String getDepartments() {
        return DEPARTMENTS;
    }

    public static String getDepartmentMsg(String departmentTag) {
        return format(DEPARTMENT_MSG, departmentTag);
    }

    public static String getGroups(String departmentTag, int fillingMode) {
        return format(GROUPS, departmentTag, fillingMode);
    }

    public static String getSchedule(String departmentTag, String groupName) {
        return format(SCHEDULE, departmentTag, groupName);
    }
}
