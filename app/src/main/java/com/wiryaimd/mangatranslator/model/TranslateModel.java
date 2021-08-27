package com.wiryaimd.mangatranslator.model;

import java.util.List;

public class TranslateModel {

    private Translation[] translations;

    public TranslateModel(Translation[] translations) {
        this.translations = translations;
    }

    public Translation[] getTranslations() {
        return translations;
    }

    public static class Translation{
        private String text;
        private String to;

        public Translation(String text, String to) {
            this.text = text;
            this.to = to;
        }

        public String getText() {
            return text;
        }

        public String getTo() {
            return to;
        }
    }

}
