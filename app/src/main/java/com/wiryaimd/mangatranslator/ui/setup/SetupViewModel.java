package com.wiryaimd.mangatranslator.ui.setup;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.storage.CStorage;
import com.wiryaimd.mangatranslator.util.translator.GApiTranslate;
import com.wiryaimd.mangatranslator.util.translator.GTranslate;
import com.wiryaimd.mangatranslator.util.vision.GRecognition;
import com.wiryaimd.mangatranslator.util.vision.MSRecognition;
import com.wiryaimd.mangatranslator.model.SelectedModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SetupViewModel extends AndroidViewModel {

    private static final String TAG = "SetupViewModel";

    private MutableLiveData<List<SelectedModel>> selectedModelLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagFromLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagToLiveData = new MutableLiveData<>();
    private MutableLiveData<TranslateEngine> teLiveData = new MutableLiveData<>();
    private MutableLiveData<OCREngine> ocrLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Bitmap>> bitmapListLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Bitmap>> pdfListLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> saveCodeLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> pageIndexLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> adsCount = new MutableLiveData<>();
    private MutableLiveData<String> infoMsg = new MutableLiveData<>();
    private MutableLiveData<Integer> countPage = new MutableLiveData<>();

    private String rapidKey = Const.RAPID_API_KEY;
    private String rapidHost = Const.RAPID_API_HOST;
    private String azureHost = "";
    private String azureKey = "";
    private Boolean availableMicrosoft = false;
    private Boolean availableAzure = false;

    private GApiTranslate gApiTranslate;
    private GTranslate gTranslate;

    private MSRecognition msRecognition;
//    private GRecognition gRecognition;

    public enum TranslateEngine{
        ON_DEVICE,
        USING_API,
        USING_MS
    }

    public enum OCREngine{
        ON_DEVICE,
        USING_API
    }

    public SetupViewModel(@NonNull @NotNull Application application) {
        super(application);

        teLiveData.setValue(TranslateEngine.USING_API);
        ocrLiveData.setValue(OCREngine.ON_DEVICE);
        saveCodeLiveData.setValue(0);
        pageIndexLiveData.setValue(0);
        countPage.setValue(0);

        gApiTranslate = GApiTranslate.getInstance();
        gTranslate = GTranslate.getInstance();

        msRecognition = MSRecognition.getInstance();
//        gRecognition = GRecognition.getInstance();
    }

    public GTranslate getGTranslate() {
        return gTranslate;
    }

//    public GRecognition getGRecognition() {
//        return gRecognition;
//    }


    public String getAzureHost() {
        return azureHost;
    }

    public String getAzureKey() {
        return azureKey;
    }

    public void setAzureHost(String azureHost) {
        this.azureHost = azureHost;
    }

    public void setAzureKey(String azureKey) {
        this.azureKey = azureKey;
    }

    public MSRecognition getMsRecognition() {
        return msRecognition;
    }

    public GApiTranslate getGApiTranslate() {
        return gApiTranslate;
    }

    public MutableLiveData<Integer> getSaveCodeLiveData() {
        return saveCodeLiveData;
    }

    public MutableLiveData<ArrayList<Bitmap>> getBitmapListLiveData() {
        return bitmapListLiveData;
    }

    public MutableLiveData<ArrayList<Bitmap>> getPdfListLiveData() {
        return pdfListLiveData;
    }

    public MutableLiveData<List<SelectedModel>> getSelectedModelLiveData() {
        return selectedModelLiveData;
    }

    public MutableLiveData<String> getInfoMsg() {
        return infoMsg;
    }

    public MutableLiveData<Integer> getAdsCount() {
        return adsCount;
    }

    public MutableLiveData<Integer> getPageIndexLiveData() {
        return pageIndexLiveData;
    }

    public MutableLiveData<OCREngine> getOcrLiveData() {
        return ocrLiveData;
    }

    public MutableLiveData<TranslateEngine> getTeLiveData() {
        return teLiveData;
    }

    public MutableLiveData<Integer> getFlagFromLiveData() {
        return flagFromLiveData;
    }

    public MutableLiveData<Integer> getFlagToLiveData() {
        return flagToLiveData;
    }

    public MutableLiveData<Integer> getCountPage() {
        return countPage;
    }

    public Boolean getAvailableAzure() {
        return availableAzure;
    }

    public Boolean getAvailableMicrosoft() {
        return availableMicrosoft;
    }

    public String getRapidKey() {
        return rapidKey;
    }

    public String getRapidHost() {
        return rapidHost;
    }

    public void setRapidHost(String rapidHost) {
        this.rapidHost = rapidHost;
    }

    public void setRapidKey(String rapidKey) {
        this.rapidKey = rapidKey;
    }

    public void setAvailableMicrosoft(Boolean availableMicrosoft) {
        this.availableMicrosoft = availableMicrosoft;
    }

    public void setAvailableAzure(Boolean availableAzure) {
        this.availableAzure = availableAzure;
    }
}
