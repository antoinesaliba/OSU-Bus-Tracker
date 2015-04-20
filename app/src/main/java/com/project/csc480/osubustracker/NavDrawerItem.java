package com.project.csc480.osubustracker;

/**
 * Created by Lucas on 4/20/2015.
 */
public class NavDrawerItem {

    String ItemName;
    int imgResID;
    String title;
    boolean isSpinner;

    public NavDrawerItem(String itemName, int imgResID) {
        ItemName = itemName;
        this.imgResID = imgResID;
    }

    public NavDrawerItem(boolean isSpinner) {
        this(null, 0);
        this.isSpinner = isSpinner;
    }

    public NavDrawerItem(String title) {
        this(null, 0);
        this.title = title;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getImgResID() {
        return imgResID;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSpinner() {
        return isSpinner;
    }

}

