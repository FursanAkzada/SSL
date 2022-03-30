package com.example.food;

public class Food {
    private String id;
    private String pname;
    private String pic;
    private String panel;
    private String lamp;
    private String pole;
    private String date;
    private String device;
    private String latitude;
    private String longitude;
    private String location;
    private byte[] image;
    private String others;

    public Food(String pname, String pic, String panel, String lamp, String pole, String date, String device, String latitude, String longitude, String location, byte[] image, String others, String id){
        this.pname = pname;
        this.pic = pic;
        this.panel = panel;
        this.lamp = lamp;
        this.pole = pole;
        this.date = date;
        this.device = device;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.image = image;
        this.others = others;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPname() { return pname; }

    public void setPname(String pname) { this.pname = pname; }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    public String getLamp() {
        return lamp;
    }

    public void setLamp(String lamp) {
        this.lamp = lamp;
    }

    public String getPole() {
        return pole;
    }

    public void setPole(String pole) {
        this.pole = pole;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }
}
