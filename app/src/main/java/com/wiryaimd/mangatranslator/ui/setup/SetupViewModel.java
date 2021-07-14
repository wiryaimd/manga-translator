package com.wiryaimd.mangatranslator.ui.setup;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.wiryaimd.mangatranslator.model.SelectedModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetupViewModel extends AndroidViewModel {

    private MutableLiveData<List<SelectedModel>> selectedModelLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagFromLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagToLiveData = new MutableLiveData<>();

    public SetupViewModel(@NonNull @NotNull Application application) {
        super(application);
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
