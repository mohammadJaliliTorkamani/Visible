package dev.aban.visible.model;

public class User {
    private int id;
    private String name;
    private String imageURL;
    private String phone;
    private int bd_y;
    private int bd_m;
    private int bd_d;

    public User(int id, String name, String imageURL, String phone, int bd_y, int bd_m, int bd_d) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.phone = phone;
        this.bd_y = bd_y;
        this.bd_m = bd_m;
        this.bd_d = bd_d;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBd_y() {
        return bd_y;
    }

    public void setBd_y(int bd_y) {
        this.bd_y = bd_y;
    }

    public int getBd_m() {
        return bd_m;
    }

    public void setBd_m(int bd_m) {
        this.bd_m = bd_m;
    }

    public int getBd_d() {
        return bd_d;
    }

    public void setBd_d(int bd_d) {
        this.bd_d = bd_d;
    }
}
