package com.wiryaimd.mangatranslator.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {

    public interface OpenFile{
        void openImage();
        void openPdf();
    }

    private OpenFile openFile;

    public MainViewModel(@NonNull @org.jetbrains.annotations.NotNull Application application) {
        super(application);
    }

    public OpenFile getOpenFile() {
        return openFile;
    }

    public void setOpenFile(OpenFile openFile) {
        this.openFile = openFile;
    }
}
