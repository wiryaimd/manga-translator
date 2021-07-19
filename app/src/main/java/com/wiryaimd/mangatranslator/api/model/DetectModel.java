package com.wiryaimd.mangatranslator.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetectModel {

    @SerializedName("language")
    private String lang;

    private String orientation;

    private List<Regions> regions;

    public class Regions{
        private String boundingBox;
        private List<Lines> lines;

        public Regions(String boundingBox, List<Lines> lines) {
            this.boundingBox = boundingBox;
            this.lines = lines;
        }

        public String getBoundingBox() {
            return boundingBox;
        }

        public List<Lines> getLines() {
            return lines;
        }
    }

    public class Lines{
        private String boundingBox;
        private List<Words> words;

        public Lines(String boundingBox, List<Words> words) {
            this.boundingBox = boundingBox;
            this.words = words;
        }

        public String getBoundingBox() {
            return boundingBox;
        }

        public List<Words> getWords() {
            return words;
        }
    }

    public class Words{
        private String boundingBox;
        private String text;

        public Words(String boundingBox, String text) {
            this.boundingBox = boundingBox;
            this.text = text;
        }

        public String getBoundingBox() {
            return boundingBox;
        }

        public String getText() {
            return text;
        }
    }

    public DetectModel(String lang, String orientation, List<Regions> regions) {
        this.lang = lang;
        this.orientation = orientation;
        this.regions = regions;
    }

    public String getLang() {
        return lang;
    }

    public String getOrientation() {
        return orientation;
    }

    public List<Regions> getRegions() {
        return regions;
    }
}
