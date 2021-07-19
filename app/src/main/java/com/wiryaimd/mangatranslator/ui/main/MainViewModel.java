package com.wiryaimd.mangatranslator.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wiryaimd.mangatranslator.MtRepository;
import com.wiryaimd.mangatranslator.api.ApiEndpoint;
import com.wiryaimd.mangatranslator.model.SelectedModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public interface OpenFile{
        void openImage();
        void openPdf();
    }

    private OpenFile openFile;
    private MtRepository repository;

    public MainViewModel(@NonNull @org.jetbrains.annotations.NotNull Application application) {
        super(application);

        repository = new MtRepository();
    }

    public MtRepository getRepository() {
        return repository;
    }

    public ApiEndpoint getApiEndpoint(){
        return repository.getApiEndpoint();
    }

    public OpenFile getOpenFile() {
        return openFile;
    }

    public void setOpenFile(OpenFile openFile) {
        this.openFile = openFile;
    }
}
