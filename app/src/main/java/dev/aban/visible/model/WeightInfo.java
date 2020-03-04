package dev.aban.visible.model;

public class WeightInfo {
    private String weightPath;
    private String labelPath;

    public WeightInfo(String weightPath, String labelPath) {
        this.weightPath = weightPath;
        this.labelPath = labelPath;
    }

    public String getWeightPath() {
        return weightPath;
    }

    public void setWeightPath(String weightPath) {
        this.weightPath = weightPath;
    }

    public String getLabelPath() {
        return labelPath;
    }

    public void setLabelPath(String labelPath) {
        this.labelPath = labelPath;
    }
}
