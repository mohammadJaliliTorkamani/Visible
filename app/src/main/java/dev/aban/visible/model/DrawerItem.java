package dev.aban.visible.model;

public class DrawerItem {
    private int position;
    private int name;
    private int icon;

    public DrawerItem(int position, int name, int icon) {
        this.position = position;
        this.name = name;
        this.icon = icon;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
