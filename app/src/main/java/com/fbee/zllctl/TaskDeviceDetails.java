package com.fbee.zllctl;

public class TaskDeviceDetails
{
    private TaskInfo taskInfo;
    private TaskDeviceAction taskDeviceAction;

    public TaskInfo getTaskInfo()
    {
        return taskInfo;
    }

    public void setTaskInfo(TaskInfo taskInfo)
    {
        this.taskInfo = taskInfo;
    } 

    public TaskDeviceAction getTaskDeviceAction()
    {
        return taskDeviceAction;
    }

    public void setTaskDeviceAction(TaskDeviceAction taskDeviceAction)
    {
        this.taskDeviceAction = taskDeviceAction;
    }

}
