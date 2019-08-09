package com.example.awei.slamshow;

public class house {
    private String location;
    private String price;
    private String image;
    private String name;
    private String plyUrl;

    public house(String location,String price,String name,String image, String plyUrl){
        this.location = location;
        this.price = price;
        this.name = name;
        this.image = image;
        this.plyUrl = plyUrl;
    }
    public String getImage() {
        return image;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getPlyUrl() {
        return plyUrl;
    }

    public void setPlyUrl(String plyUrl) {
        this.plyUrl = plyUrl;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }
}
