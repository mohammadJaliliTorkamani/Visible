package dev.aban.visible.model;

public class MoreApp {
    private int id;
    private String link;
    private String title;
    private String description;
    private String imageURL;
    private String containerColor;

    public MoreApp(int id, String link, String title, String description, String imageURL, String containerColor) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.containerColor = containerColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getContainerColor() {
        return containerColor;
    }

    public void setContainerColor(String containerColor) {
        this.containerColor = containerColor;
    }
}