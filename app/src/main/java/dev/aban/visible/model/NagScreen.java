package dev.aban.visible.model;

public class NagScreen {
    private int id;
    private String title;
    private String description;
    private String imageURL;
    private String yesLink;

    public NagScreen(int id, String title, String description, String imageURL, String yesLink) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.yesLink = yesLink;
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

    public String getYesLink() {
        return yesLink;
    }

    public void setYesLink(String yesLink) {
        this.yesLink = yesLink;
    }
}
