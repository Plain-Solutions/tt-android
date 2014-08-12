package org.ssutt.android.activity.schedule_activity.tabs;

import org.ssutt.android.R;

public class TabWednesday extends AbstractTab {
    @Override
    public int getLayoutId() {
        return R.layout.tab_view;
    }

    @Override
    public int getDayOfWeek() {
        return 2;
    }
}
