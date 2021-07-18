package com.wiryaimd.mangatranslator.api.model;

public class Detect2Model {

    private String language;
    private String textAngle;

    public Detect2Model(String language, String textAngle) {
        this.language = language;
        this.textAngle = textAngle;
    }

    public String getLanguage() {
        return language;
    }

    public String getTextAngle() {
        return textAngle;
    }
}
