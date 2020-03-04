package dev.aban.visible.model;

public class DonateItem {
    private int id;
    private String title;
    private float price;
    private String imageURL;
    private String description;

    public DonateItem(int id, String title, float price, String imageURL, String description) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imageURL = imageURL;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
