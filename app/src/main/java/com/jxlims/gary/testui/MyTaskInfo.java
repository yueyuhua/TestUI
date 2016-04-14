package com.jxlims.gary.testui;

import java.util.Date;

/**
 * Created by Gary on 2016/4/4.
 */
public class MyTaskInfo {

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskDetail() {
        return TaskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        TaskDetail = taskDetail;
    }

    public Date getTaskDate() {
        return TaskDate;
    }

    public void setTaskDate(Date taskDate) {
        TaskDate = taskDate;
    }

    private String TaskName;
    private String TaskDetail;
    private Date TaskDate;

}
