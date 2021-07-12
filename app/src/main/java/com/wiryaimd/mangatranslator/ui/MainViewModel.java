package com.wiryaimd.mangatranslator.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wiryaimd.mangatranslator.model.SelectedModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public interface OpenFile{
        void openImage();
        void openPdf();
    }

    private OpenFile openFile;

    private MutableLiveData<List<SelectedModel>> selectedModelLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> downloadedModelsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagFromLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> flagToLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull @org.jetbrains.annotations.NotNull Application application) {
        super(application);
    }

    public void setSelectedModelLiveData(List<SelectedModel> selectedList){
        selectedModelLiveData.postValue(selectedList);
    }

    public MutableLiveData<List<String>> getDownloadedModelsLiveData() {
        return downloadedModelsLiveData;
    }

    public MutableLiveData<Integer> getFlagFromLiveData() {
        return flagFromLiveData;
    }

    public MutableLiveData<Integer> getFlagToLiveData() {
        return flagToLiveData;
    }

    public LiveData<List<SelectedModel>> getSelectedModelLiveData() {
        return selectedModelLiveData;
    }

    public OpenFile getOpenFile() {
        return openFile;
    }

    public void setOpenFile(OpenFile openFile) {
        this.openFile = openFile;
    }
}
