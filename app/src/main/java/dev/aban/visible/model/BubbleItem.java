package dev.aban.visible.model;

import android.os.Environment;

import java.util.LinkedList;
import java.util.List;

import dev.aban.visible.utils.Constants;
import dev.aban.visible.utils.Helper;

public class BubbleItem {
    private String title;
    private String description;
    private String imageURL;
    private float price;
    private int id;
    private String sku;
    private boolean permittedToUse;
    private List<String> pictures;

    public BubbleItem(String title, String description, String sku, String imageURL, float price, int id, boolean permittedToUse, List<String> pictures) {
        this.title = title;
        this.sku = sku;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.id = id;
        this.permittedToUse = permittedToUse;
        this.pictures = pictures;
    }

    public static BubbleItem getItemWithID(int id, List<BubbleItem> bubbleItemList) {
        for (BubbleItem bubbleItem : bubbleItemList)
            if (bubbleItem.getId() == id)
                return bubbleItem;
        return null;
    }

    public static String getModelPathWithID(int bubbleID) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "visible/" + bubbleID + Constants.WEIGHT_FILE_SUFFIX;
    }

    public static String getLabelPathWithID(int bubbleID) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "visible/" + bubbleID + Constants.LABEL_FILE_SUFFIX;
    }

    public static List<BubbleItem> getLocallyExistBubbleItems(List<BubbleItem> bubbleItems) {
        List<BubbleItem> list = new LinkedList<>();
        for (BubbleItem bubbleItem : bubbleItems)
            if (Helper.bubbleExists(bubbleItem.getId() + Constants.WEIGHT_FILE_SUFFIX) && Helper.bubbleExists(bubbleItem.getId() + Constants.LABEL_FILE_SUFFIX))
                list.add(bubbleItem);

        return list;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isPermittedToUse() {
        return permittedToUse;
    }

    public void setPermittedToUse(boolean permittedToUse) {
        this.permittedToUse = permittedToUse;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BubbleItem bubbleItem = (BubbleItem) o;
        return bubbleItem.getTitle().equals(this.getTitle()) &&
                bubbleItem.getId() == this.getId() &&
                bubbleItem.getImageURL().equals(this.getImageURL()) &&
                bubbleItem.getPrice() == this.getPrice() &&
                bubbleItem.isPermittedToUse() == this.isPermittedToUse();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + title.hashCode();
        result = 31 * result + imageURL.hashCode();
        result = (int) (31 * result + price);
        result = (int) (31 * result + id);
        return result;
    }
}
