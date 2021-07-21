package com.wiryaimd.mangatranslator.ui.setup;

import android.app.Application;

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

import java.util.List;

public class SetupViewModel extends AndroidViewModel {

    private static final String TAG = "SetupViewModel";

    private MutableLiveData<List<SelectedModel>> selectedModelLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagFromLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagToLiveData = new MutableLiveData<>();

    private AWSTranslate awsTranslate;
    private GTranslate gTranslate;

    private MSRecognition msRecognition;
    private GRecognition gRecognition;

    private CStorage storage;

    public SetupViewModel(@NonNull @NotNull Application application) {
        super(application);

        awsTranslate = AWSTranslate.getInstance();
        gTranslate = GTranslate.getInstance();

        msRecognition = MSRecognition.getInstance();
        gRecognition = GRecognition.getInstance();

        storage = CStorage.getInstance(getApplication());
    }

    public CStorage getStorage() {
        return storage;
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

    public MutableLiveData<List<SelectedModel>> getSelectedModelLiveData() {
        return selectedModelLiveData;
    }

    public MutableLiveData<Integer> getFlagFromLiveData() {
        return flagFromLiveData;
    }

    public MutableLiveData<Integer> getFlagToLiveData() {
        return flagToLiveData;
    }
}
