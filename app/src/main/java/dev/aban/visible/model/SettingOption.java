package dev.aban.visible.model;

public class SettingOption {
    private String key; //value to shoe, like english
    private String value; //value to store in sharedPref . like en

    public SettingOption(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
