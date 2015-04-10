package com.project.csc480.osubustracker;

/**
 * Created by rafaelamfonseca on 4/10/15.
 */
public class Notification {
    private long id;
    private String busStopName;
    private String routeName;
    private Integer notificationId;
    private String time;

    public String getRouteName() {
        return routeName;
    }

    public long getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public String getBusStopName() {
        return busStopName;
    }


    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public void setTime(String time) {
        this.time = time;
    }

}