package com.wiryaimd.mangatranslator.ui.setup;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.wiryaimd.mangatranslator.util.storage.CStorage;
import com.wiryaimd.mangatranslator.util.translator.GTranslate;
import com.wiryaimd.mangatranslator.util.vision.GRecognition;
import com.wiryaimd.mangatranslator.util.vision.MSRecognition;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.util.translator.AWSTranslate;

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

    private Boolean availableAws = false;
    private Boolean availableMicrosoft = false;

    private AWSTranslate awsTranslate;
    private GTranslate gTranslate;

    private MSRecognition msRecognition;
    private GRecognition gRecognition;

    public enum TranslateEngine{
        ON_DEVICE,
        USING_API
    }

    public enum OCREngine{
        ON_DEVICE,
        USING_API
    }

    public SetupViewModel(@NonNull @NotNull Application application) {
        super(application);

        teLiveData.setValue(TranslateEngine.ON_DEVICE);
        ocrLiveData.setValue(OCREngine.ON_DEVICE);
        saveCodeLiveData.setValue(0);

        awsTranslate = AWSTranslate.getInstance();
        gTranslate = GTranslate.getInstance();

        msRecognition = MSRecognition.getInstance();
        gRecognition = GRecognition.getInstance();
    }

    public GTranslate getGTranslate() {
        return gTranslate;
    }

    public GRecognition getGRecognition() {
        return gRecognition;
    }

    public MSRecognition getMsRecognition() {
        return msRecognition;
    }

    public AWSTranslate getAwsTranslate() {
        return awsTranslate;
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

    public Boolean getAvailableAws() {
        return availableAws;
    }

    public Boolean getAvailableMicrosoft() {
        return availableMicrosoft;
    }

    public void setAvailableAws(Boolean availableAws) {
        this.availableAws = availableAws;
    }

    public void setAvailableMicrosoft(Boolean availableMicrosoft) {
        this.availableMicrosoft = availableMicrosoft;
    }
}
