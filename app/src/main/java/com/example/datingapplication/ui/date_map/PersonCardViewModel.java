package com.example.datingapplication.ui.date_map;

public class PersonCardViewModel {

    private String name;
    private String age;
    private String comment;

    private String latitude;
    private String longitude;
    private String phoneNumber;

    public PersonCardViewModel(String name, String age, String comment, String latitude,
                               String longitude, String phoneNumber) {
        this.name = name;
        this.age = age;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
