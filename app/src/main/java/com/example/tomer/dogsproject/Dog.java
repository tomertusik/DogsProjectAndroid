package com.example.tomer.dogsproject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Tomer on 19/05/2018.
 */

@Entity
public class Dog {

    @ColumnInfo(name = "lastUpdate")
    private long lastUpdate;
    @ColumnInfo(name = "user_id")
    private String ownerUserID;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "city")
    private String city;
    @ColumnInfo(name = "age")
    private String age;
    @ColumnInfo(name = "phone")
    private String phone;
    @NonNull@PrimaryKey
    private String imageURL;

    public Dog() {
    }

    public Dog(String ownerUserID, String name, String age, String city, String phone, long time) {
        this.ownerUserID = ownerUserID;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.city = city;
        this.lastUpdate = time;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOwnerUserID() {
        return ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        this.ownerUserID = ownerUserID;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
