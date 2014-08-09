package org.ssutt.android.api;

import static java.lang.String.format;

public abstract class ApiRequests {
    private static final int DEPARTMENTS_VERSION = 1;
    private static final int DEPARTMENT_MSG_VERSION = 1;
    private static final int GROUPS_VERSION = 1;
    private static final int SCHEDULE_VERSION = 2;

    private static final String API_URL = "http://api.ssutt.org:8080/%d/%s";
    private static final String DEPARTMENTS = format(API_URL, DEPARTMENTS_VERSION, "departments");
    private static final String DEPARTMENT_MSG = format(API_URL, DEPARTMENT_MSG_VERSION, "/department/%s/msg");
    private static final String GROUPS = format(API_URL, GROUPS_VERSION, "department/%s/groups?filled=%d");
    private static final String SCHEDULE = format(API_URL, SCHEDULE_VERSION, "department/%s/group/%s");

    public static String getDepartments() {
        return DEPARTMENTS;
    }

    public static String getDepartmentMsg(String departmentTag) {
        return format(DEPARTMENT_MSG, departmentTag);
    }

    public static String getGroups(String departmentTag, GroupMode fillingMode) {
        return format(GROUPS, departmentTag, fillingMode.ordinal());
    }

    public static String getSchedule(String departmentTag, String groupName) {
        System.out.println(format(SCHEDULE, departmentTag, groupName));
        return format(SCHEDULE, departmentTag, groupName);
    }
}
