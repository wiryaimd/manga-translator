package com.wiryaimd.mangatranslator.ui.setup.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.InfoModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.SetupActivity;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.ui.setup.adapter.InfoAdapter;
import com.wiryaimd.mangatranslator.ui.setup.adapter.SelectAdapter;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.InfoDialog;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.LoadPDFDialog;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.ProcessDialog;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;
import com.wiryaimd.mangatranslator.util.RealPath;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProcessFragment extends Fragment {

    private static final String TAG = "ProcessFragment";

    private SetupViewModel setupViewModel;

    private Button btnrequire, btnprocess;
    private ProgressBar loading;
    private ImageView imgalert;

    private TextView tvondevice, tvusingapi, msDevice, msApi, processCount;

    private Spinner spinFrom, spinTo;

    private ViewPager viewPager;
    private RecyclerView recyclerView;

    private List<SelectedModel> selectedList = new ArrayList<>();
    private List<String> downloadedList = new ArrayList<>();

    private TranslateRemoteModel translateRemoteModel;
    private RemoteModelManager remoteModelManager;
    private DownloadConditions downloadConditions;

    private SetupViewModel.TranslateEngine translateEngine = SetupViewModel.TranslateEngine.ON_DEVICE;
    private SetupViewModel.OCREngine ocrEngine = SetupViewModel.OCREngine.ON_DEVICE;

    private int flagFrom = 0, flagTo = 0;
    private boolean sucFrom = false, sucTo = false;

    private boolean downloadedFrom = true;
    private boolean downloadedTo = true;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        remoteModelManager = RemoteModelManager.getInstance();

        btnrequire = view.findViewById(R.id.processlang_downloadlanguages);
        btnprocess = view.findViewById(R.id.processlang_processtranslate);
        loading = view.findViewById(R.id.processlang_loadingdownload);
        imgalert = view.findViewById(R.id.processlang_imgalert);
        spinFrom = view.findViewById(R.id.processlang_spinfrom);
        spinTo = view.findViewById(R.id.processlang_spinto);
        recyclerView = view.findViewById(R.id.processlang_recyclerview);
        viewPager = view.findViewById(R.id.processlang_viewpager);
        tvondevice = view.findViewById(R.id.processlang_engine_ondevice);
        tvusingapi = view.findViewById(R.id.processlang_engine_api);
        msDevice = view.findViewById(R.id.processlang_engine_detecttext_ondevice);
        msApi = view.findViewById(R.id.processlang_engine_detecttext_api);
        processCount = view.findViewById(R.id.processlang_processcount);

        // // oke ternyatod data yang tersimpan pada viewmodel berbeda ya tod tiap activity nya tod kentod
        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        selectedList = setupViewModel.getSelectedModelLiveData().getValue();
        if (selectedList == null || selectedList.size() == 0) {
            requireActivity().finish();
            Toast.makeText(setupViewModel.getApplication(), "Cannot find data on fragment", Toast.LENGTH_SHORT).show();
            return;
        }

        processCount.setText((selectedList.size() + " Image sorted by file name"));

        List<InfoModel> infoList = new ArrayList<>();
        infoList.add(new InfoModel("Japanese languages", "Currently, i can't translate from japanese language because the writing is horizontal and has a lot of bugs when trying to translate, this will fixed soon, wait me for next update!"));
        infoList.add(new InfoModel("Download Models", "Before translate Manga/Manhwa/Manhua if you use 'On Device' Engine, you need to download model languages dan make sure your internet is connected"));
        infoList.add(new InfoModel("Not 100% Accurate", "This app is the first version and still has many shortcomings, you can use 'Using API' Engine for more accurate but not 100% accurate"));
        InfoAdapter infoAdapter = new InfoAdapter(setupViewModel.getApplication(), infoList);

        viewPager.setAdapter(infoAdapter);
        viewPager.setPadding(0, 0, 160, 0);

        movePage();

        SelectAdapter selectAdapter = new SelectAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(setupViewModel.getApplication(), 2));
        recyclerView.setAdapter(selectAdapter);
        selectAdapter.setSelectedList(setupViewModel.getApplication(), selectedList);

        ArrayAdapter<String> spinnerFromAdapter = new ArrayAdapter<>(setupViewModel.getApplication(), R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_from);
        spinFrom.setAdapter(spinnerFromAdapter);
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(setupViewModel.getApplication(), R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_to);
        spinTo.setAdapter(spinnerToAdapter);

        remoteModelManager = RemoteModelManager.getInstance();
        downloadConditions = new DownloadConditions.Builder().build();
        updateDownloadedModels();

        setupViewModel.getOcrLiveData().observe(getViewLifecycleOwner(), new Observer<SetupViewModel.OCREngine>() {
            @Override
            public void onChanged(SetupViewModel.OCREngine oE) {
                ocrEngine = oE;
            }
        });

        setupViewModel.getTeLiveData().observe(getViewLifecycleOwner(), new Observer<SetupViewModel.TranslateEngine>() {
            @Override
            public void onChanged(SetupViewModel.TranslateEngine tE) {
                translateEngine = tE;
            }
        });

        setupViewModel.getFlagFromLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setupViewModel.getTeLiveData().observe(getViewLifecycleOwner(), new Observer<SetupViewModel.TranslateEngine>() {
                    @Override
                    public void onChanged(SetupViewModel.TranslateEngine tE) {
                        if (tE == SetupViewModel.TranslateEngine.ON_DEVICE) {
                            checkRequireDownload(integer, 0);
                            if (!downloadedFrom || !downloadedTo) {
                                btnrequire.setVisibility(View.VISIBLE);
                                imgalert.setVisibility(View.VISIBLE);
                            } else {
                                btnrequire.setVisibility(View.GONE);
                                imgalert.setVisibility(View.GONE);
                            }
                        } else {
                            btnrequire.setVisibility(View.GONE);
                            imgalert.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        msDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getOcrLiveData().setValue(SetupViewModel.OCREngine.ON_DEVICE);
                msDevice.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                msApi.setBackgroundColor(ContextCompat.getColor(setupViewModel.getApplication(), R.color.primary));
            }
        });

        msApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getOcrLiveData().setValue(SetupViewModel.OCREngine.USING_API);
                msApi.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                msDevice.setBackgroundColor(ContextCompat.getColor(setupViewModel.getApplication(), R.color.primary));
            }
        });

        setupViewModel.getFlagToLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setupViewModel.getTeLiveData().observe(getViewLifecycleOwner(), new Observer<SetupViewModel.TranslateEngine>() {
                    @Override
                    public void onChanged(SetupViewModel.TranslateEngine tE) {
                        if (tE == SetupViewModel.TranslateEngine.ON_DEVICE) {
                            checkRequireDownload(integer, 1);
                            if (!downloadedFrom || !downloadedTo) {
                                btnrequire.setVisibility(View.VISIBLE);
                                imgalert.setVisibility(View.VISIBLE);
                            } else {
                                btnrequire.setVisibility(View.GONE);
                                imgalert.setVisibility(View.GONE);
                            }
                        } else {
                            btnrequire.setVisibility(View.GONE);
                            imgalert.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        spinFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 2){
//                    spinFrom.setSelection(0);
//                    new InfoDialog("Coming Soon!", "Currently, i can't translate from japanese language, wait me for next update!", false).show(getParentFragmentManager(), "INFO_COMING_SOON");
//                    return;
//                }

                setupViewModel.getFlagFromLiveData().setValue(i);
                flagFrom = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupViewModel.getFlagToLiveData().setValue(i);
                flagTo = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvondevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getTeLiveData().setValue(SetupViewModel.TranslateEngine.ON_DEVICE);
                tvondevice.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                tvusingapi.setBackgroundColor(ContextCompat.getColor(setupViewModel.getApplication(), R.color.primary));
            }
        });

        tvusingapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getTeLiveData().setValue(SetupViewModel.TranslateEngine.USING_API);
                tvusingapi.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                tvondevice.setBackgroundColor(ContextCompat.getColor(setupViewModel.getApplication(), R.color.primary));
            }
        });

        btnprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setupViewModel.getFlagFromLiveData().getValue() == null ||
                        setupViewModel.getFlagToLiveData().getValue() == null ||
                        setupViewModel.getFlagFromLiveData().getValue() == 0 ||
                        setupViewModel.getFlagToLiveData().getValue() == 0) {
                    Toast.makeText(setupViewModel.getApplication(), "Please select languages", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (flagFrom == 2 && !setupViewModel.getAvailableAzure()){
//                    new InfoDialog("Not Available", "You can try it later for japanese language", false).show(getParentFragmentManager(), "NOT_AVAILABLE_JAPAN");
//                    return;
//                }else {
                    if (translateEngine == SetupViewModel.TranslateEngine.USING_API && !setupViewModel.getAvailableAws() &&
                            ocrEngine == SetupViewModel.OCREngine.USING_API && !setupViewModel.getAvailableMicrosoft()) {
                        new InfoDialog("Not Available", "Translate API/Detect Text API is not available now, maybe later will available again, you can use 'On Device' for now", false).show(getParentFragmentManager(), "NOT_AVAILABLE_BOTH");
                        return;
                    }

                    if ((flagFrom == 3 || flagFrom == 4) && !setupViewModel.getAvailableMicrosoft()) {
                        new InfoDialog("AH GOMEN!", "Translate from Chinese or Korean language not available for now, you can try it later", false).show(getParentFragmentManager(), "NOT_AVAILABLE_MS");
                        return;
                    }
//                }

                if (translateEngine == SetupViewModel.TranslateEngine.USING_API && !setupViewModel.getAvailableAws()){
                    new InfoDialog("Translate API N/A", "Translate API is not available now, maybe later will available again, you can use 'On Device' for now", false).show(getParentFragmentManager(), "NOT_AVAILABLE_AWS");
                    return;
                }

                checkRequireDownload(flagFrom, 0);
                checkRequireDownload(flagTo, 1);

                if (selectedList.get(0).getType() == SelectedModel.Type.IMAGE) {
                    if (setupViewModel.getTeLiveData().getValue() == SetupViewModel.TranslateEngine.ON_DEVICE) {
                        if (downloadedFrom && downloadedTo) {
                            Log.d(TAG, "onClick: process cek btn");
                            new ProcessDialog().show(getParentFragmentManager(), "PROCESS_FRAGMENT_SETUP");
                        } else {
                            new InfoDialog("Download Language", "You need to download required language before start translate", false).show(getParentFragmentManager(), "DIALOG_DOWNLOAD_REQ");
                        }
                    } else {
                        new ProcessDialog().show(getParentFragmentManager(), "PROCESS_FRAGMENT_SETUP_API");
                    }
                } else {
                    if (setupViewModel.getTeLiveData().getValue() == SetupViewModel.TranslateEngine.ON_DEVICE) {
                        if (downloadedFrom && downloadedTo) {
                            new LoadPDFDialog(selectedList.get(0).getUri(), new ArrayList<>()).show(getParentFragmentManager(), "LOAD_PDF_DIALOG");
                        } else {
                            new InfoDialog("Download Language", "You need to download required language before start translate", false).show(getParentFragmentManager(), "DIALOG_DOWNLOAD_REQ2");;
                        }
                    } else {
                        new LoadPDFDialog(selectedList.get(0).getUri(), new ArrayList<>()).show(getParentFragmentManager(), "LOAD_PDF_DIALOG_API");
                    }
                }
            }
        });

        btnrequire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagTo == 0 && flagFrom == 0) {
                    Toast.makeText(setupViewModel.getApplication(), "Please select language", Toast.LENGTH_SHORT).show();
                    return;
                }
                sucFrom = false;
                sucTo = false;

                btnprocess.setVisibility(View.GONE);
                imgalert.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);

                if (flagFrom == flagTo && !downloadedFrom) {

                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnprocess.setVisibility(View.VISIBLE);
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                            updateDownloadedModels();
                        }
                    });
                    return;
                }

                if (!downloadedFrom && !downloadedTo) {
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            sucFrom = true;
                            if (sucFrom && sucTo) {
                                btnprocess.setVisibility(View.VISIBLE);
                                btnrequire.setVisibility(View.GONE);
                                loading.setVisibility(View.GONE);
                                btnprocess.setEnabled(true);
                                updateDownloadedModels();
                            }
                        }
                    });
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_to[flagTo]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            sucTo = true;
                            if (sucFrom && sucTo) {
                                btnprocess.setVisibility(View.VISIBLE);
                                btnrequire.setVisibility(View.GONE);
                                loading.setVisibility(View.GONE);
                                btnprocess.setEnabled(true);
                                updateDownloadedModels();
                            }
                        }
                    });
                } else if (!downloadedFrom && flagFrom != 0) {
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnprocess.setVisibility(View.VISIBLE);
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                            updateDownloadedModels();
                        }
                    });
                } else if (!downloadedTo && flagTo != 0) {
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_to[flagTo]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnprocess.setVisibility(View.VISIBLE);
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                            updateDownloadedModels();
                        }
                    });
                }
            }
        });

        setupViewModel.getPageIndexLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                viewPager.setCurrentItem(integer);
            }
        });

        imgalert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new InfoDialog("Download Models", "Before you translate using on device, you need to download models to translate the language", false);
            }
        });

    }

    private void updateDownloadedModels() {
        downloadedList.clear();
        remoteModelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(@NonNull @NotNull Set<TranslateRemoteModel> translateRemoteModels) {
                for (TranslateRemoteModel model : translateRemoteModels) {
                    downloadedList.add(model.getLanguage());
                    Log.d(TAG, "onSuccess: lang: " + model.getLanguage());
                }
            }
        });
    }

    public void checkRequireDownload(int pos, int type) {

        if (pos == 0 && type == 0) {
            downloadedFrom = true;
            return;
        } else if (pos == 0 && type == 1) {
            downloadedTo = true;
            return;
        }

        String data;
        if (type == 0) {
            data = LanguagesData.flag_code_from[pos];
        } else {
            data = LanguagesData.flag_code[pos];
        }

        for (String lang : downloadedList) {
            if (data.equalsIgnoreCase(lang)) {
                if (type == 0) {
                    downloadedFrom = true;
                } else {
                    downloadedTo = true;
                }
                return;
            }
        }

        if (type == 0) {
            downloadedFrom = false;
        } else {
            downloadedTo = false;
        }
    }

    private int pageIndex = 0;

    public void movePage(){

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (pageIndex < 3){
                    setupViewModel.getPageIndexLiveData().postValue(pageIndex);
                    pageIndex += 1;
                }else{
                    pageIndex = 0;
                    setupViewModel.getPageIndexLiveData().postValue(pageIndex);
                }
            }
        }, 1000, 17000);
    }
}
