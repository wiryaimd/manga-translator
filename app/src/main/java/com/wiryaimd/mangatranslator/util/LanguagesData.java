package com.wiryaimd.mangatranslator.util;

import com.google.mlkit.nl.translate.TranslateLanguage;

import java.util.Scanner;

public class LanguagesData {

    public static String[] flag_from = {"<From>", "English", "Japanese (Manga)", "Korean (Manhwa)", "Chinese (Manhua)", "Chinese (Simplified Vertical) (Beta)", "Chinese (Traditional Vertical) (Beta)", "Indonesian"};
    public static String[] flag_from_prem = {
            "<From>", "English", "Japanese (Manga)", "Korean (Manhwa)", "Chinese (Manhua)",
            "Chinese (Simplified Vertical) (Beta)", "Chinese (Traditional Vertical) (Beta)", "Indonesian",
            "Afrikaans", "Arabic", "Belarusian", "Bulgarian",
            "Bengali", "Catalan", "Czech", "Welsh",
            "Danish", "German", "Greek",
            "Esperanto", "Spanish", "Estonian", "Persian",
            "Finnish", "French", "Irish", "Galician",
            "Gujarati", "Hebrew", "Hindi", "Croatian",
            "Haitian", "Hungarian", "Icelandic",
            "Italian", "Georgian", "Kannada", "Lithuanian", "Latvian", "Macedonian",
            "Marathi", "Malay", "Maltese", "Dutch",
            "Norwegian", "Polish", "Portuguese", "Romanian",
            "Russian", "Slovak", "Slovenian", "Albanian",
            "Swedish", "Swahili", "Tamil", "Telugu",
            "Thai", "Tagalog", "Turkish", "Ukrainian",
            "Urdu", "Vietnamese"};

    public static String[] flag_to = {"<To>", "Afrikaans", "Arabic", "Belarusian", "Bulgarian",
            "Bengali", "Catalan", "Czech", "Welsh",
            "Danish", "German", "Greek", "English",
            "Esperanto", "Spanish", "Estonian", "Persian",
            "Finnish", "French", "Irish", "Galician",
            "Gujarati", "Hebrew", "Hindi", "Croatian",
            "Haitian", "Hungarian", "Indonesian", "Icelandic",
            "Italian", "Georgian", "Kannada",
            "Korean", "Lithuanian", "Latvian", "Macedonian",
            "Marathi", "Malay", "Maltese", "Dutch",
            "Norwegian", "Polish", "Portuguese", "Romanian",
            "Russian", "Slovak", "Slovenian", "Albanian",
            "Swedish", "Swahili", "Tamil", "Telugu",
            "Thai", "Tagalog", "Turkish", "Ukrainian",
            "Urdu", "Vietnamese", "Chinese"};

    public static String[] flag_id_from = {"none", TranslateLanguage.ENGLISH, TranslateLanguage.JAPANESE, TranslateLanguage.KOREAN, TranslateLanguage.CHINESE, "zh", "zh", TranslateLanguage.INDONESIAN};
    public static String[] flag_id_from_prem = {
            "none", TranslateLanguage.ENGLISH, TranslateLanguage.JAPANESE, TranslateLanguage.KOREAN, TranslateLanguage.CHINESE, "zh", "zh", TranslateLanguage.INDONESIAN,
            TranslateLanguage.AFRIKAANS, TranslateLanguage.ARABIC, TranslateLanguage.BELARUSIAN, TranslateLanguage.BULGARIAN, TranslateLanguage.BENGALI, TranslateLanguage.CATALAN, TranslateLanguage.CZECH, TranslateLanguage.WELSH, TranslateLanguage.DANISH, TranslateLanguage.GERMAN, TranslateLanguage.GREEK, TranslateLanguage.ESPERANTO, TranslateLanguage.SPANISH, TranslateLanguage.ESTONIAN, TranslateLanguage.PERSIAN, TranslateLanguage.FINNISH, TranslateLanguage.FRENCH, TranslateLanguage.IRISH, TranslateLanguage.GALICIAN, TranslateLanguage.GUJARATI, TranslateLanguage.HEBREW, TranslateLanguage.HINDI, TranslateLanguage.CROATIAN, TranslateLanguage.HAITIAN_CREOLE, TranslateLanguage.HUNGARIAN, TranslateLanguage.ICELANDIC, TranslateLanguage.ITALIAN, TranslateLanguage.GEORGIAN, TranslateLanguage.KANNADA, TranslateLanguage.LITHUANIAN, TranslateLanguage.LATVIAN, TranslateLanguage.MACEDONIAN, TranslateLanguage.MARATHI, TranslateLanguage.MALAY, TranslateLanguage.MALTESE, TranslateLanguage.DUTCH, TranslateLanguage.NORWEGIAN, TranslateLanguage.POLISH, TranslateLanguage.PORTUGUESE, TranslateLanguage.ROMANIAN, TranslateLanguage.RUSSIAN, TranslateLanguage.SLOVAK, TranslateLanguage.SLOVENIAN, TranslateLanguage.ALBANIAN, TranslateLanguage.SWEDISH, TranslateLanguage.SWAHILI, TranslateLanguage.TAMIL, TranslateLanguage.TELUGU, TranslateLanguage.THAI, TranslateLanguage.TAGALOG, TranslateLanguage.TURKISH, TranslateLanguage.UKRAINIAN, TranslateLanguage.URDU, TranslateLanguage.VIETNAMESE
    };

    public static String[] flag_id_to = {"none", TranslateLanguage.AFRIKAANS, TranslateLanguage.ARABIC, TranslateLanguage.BELARUSIAN, TranslateLanguage.BULGARIAN, TranslateLanguage.BENGALI, TranslateLanguage.CATALAN, TranslateLanguage.CZECH, TranslateLanguage.WELSH, TranslateLanguage.DANISH, TranslateLanguage.GERMAN, TranslateLanguage.GREEK, TranslateLanguage.ENGLISH, TranslateLanguage.ESPERANTO, TranslateLanguage.SPANISH, TranslateLanguage.ESTONIAN, TranslateLanguage.PERSIAN, TranslateLanguage.FINNISH, TranslateLanguage.FRENCH, TranslateLanguage.IRISH, TranslateLanguage.GALICIAN, TranslateLanguage.GUJARATI, TranslateLanguage.HEBREW, TranslateLanguage.HINDI, TranslateLanguage.CROATIAN, TranslateLanguage.HAITIAN_CREOLE, TranslateLanguage.HUNGARIAN, TranslateLanguage.INDONESIAN, TranslateLanguage.ICELANDIC, TranslateLanguage.ITALIAN, TranslateLanguage.GEORGIAN, TranslateLanguage.KANNADA, TranslateLanguage.KOREAN, TranslateLanguage.LITHUANIAN, TranslateLanguage.LATVIAN, TranslateLanguage.MACEDONIAN, TranslateLanguage.MARATHI, TranslateLanguage.MALAY, TranslateLanguage.MALTESE, TranslateLanguage.DUTCH, TranslateLanguage.NORWEGIAN, TranslateLanguage.POLISH, TranslateLanguage.PORTUGUESE, TranslateLanguage.ROMANIAN, TranslateLanguage.RUSSIAN, TranslateLanguage.SLOVAK, TranslateLanguage.SLOVENIAN, TranslateLanguage.ALBANIAN, TranslateLanguage.SWEDISH, TranslateLanguage.SWAHILI, TranslateLanguage.TAMIL, TranslateLanguage.TELUGU, TranslateLanguage.THAI, TranslateLanguage.TAGALOG, TranslateLanguage.TURKISH, TranslateLanguage.UKRAINIAN, TranslateLanguage.URDU, TranslateLanguage.VIETNAMESE, TranslateLanguage.CHINESE};

    public static String[] flag_code_from = {"none", "en", "ja", "ko", "zh", "zh-sv", "zh-tv", "id"};
    public static String[] flag_code_from_prem = {
            "none", "en", "ja", "ko", "zh", "zh-sv", "zh-tv", "id",
            "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da", "de", "el", "eo", "es", "et", "fa", "fi", "fr", "ga", "gl", "gu", "he", "hi", "hr", "ht", "hu", "is", "it", "ka", "kn", "lt", "lv", "mk", "mr", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi"
    };

    public static String[] flag_code = {"none", "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da", "de", "el", "en", "eo", "es", "et", "fa", "fi", "fr", "ga", "gl", "gu", "he", "hi", "hr", "ht", "hu", "id", "is", "it", "ka", "kn", "ko", "lt", "lv", "mk", "mr", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sv", "sw", "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh"};

    public static void setPremium(){
        flag_from = flag_from_prem;
        flag_id_from = flag_id_from_prem;
        flag_code_from = flag_code_from_prem;
    }

}
