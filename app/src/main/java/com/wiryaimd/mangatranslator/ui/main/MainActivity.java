package com.wiryaimd.mangatranslator.ui.main;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;

import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.api.model.Detect2Model;
import com.wiryaimd.mangatranslator.api.model.DetectModel;
import com.wiryaimd.mangatranslator.api.model.Ex1Model;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.main.fragment.SelectFragment;
import com.wiryaimd.mangatranslator.ui.setup.SetupActivity;
import com.wiryaimd.mangatranslator.ui.main.fragment.dialog.SelectDialog;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.PermissionHelper;
import com.wiryaimd.mangatranslator.util.RealPath;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainViewModel mainViewModel;

    private ActivityResultLauncher<String> launcherImg = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> result) {
            ArrayList<SelectedModel> selectedList = new ArrayList<>();
            for (Uri uri : result){
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                selectedList.add(new SelectedModel(cursor.getColumnName(nameIndex), uri, SelectedModel.Type.IMAGE));
            }

            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            intent.putParcelableArrayListExtra(Const.SELECTED_LIST, selectedList);
            startActivity(intent);
        }
    });

    private ActivityResultLauncher<String> launcherPdf = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            ArrayList<SelectedModel> selectedList = new ArrayList<>();

            Cursor cursor = getContentResolver().query(result, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            selectedList.add(new SelectedModel(cursor.getColumnName(nameIndex), result, SelectedModel.Type.PDF));

            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            intent.putExtra(Const.SELECTED_LIST, selectedList);
            startActivity(intent);
            Log.d(TAG, "onActivityResult: asuuuu");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(MainActivity.this).get(MainViewModel.class);

        MainViewModel.OpenFile openFile = new MainViewModel.OpenFile() {
            @Override
            public void openImage() {
                launcherImg.launch("image/*");
            }

            @Override
            public void openPdf() {
                launcherPdf.launch("application/pdf");
            }
        };
        mainViewModel.setOpenFile(openFile);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new SelectFragment());
        ft.commit();

        setupRetrofit();

    }

    public void setupRetrofit(){
//        Map<String, String> map = new HashMap<>();
//        map.put("userId", "12");
//        map.put("userId", "15");
//        mainViewModel.getApiEndpoint().getDetecModel(map);
//        mainViewModel.getApiEndpoint().getDetectModel(new Integer[]{1, 4, 12}, "id", "desc").enqueue(new Callback<List<DetectModel>>() {
//            @Override
//            public void onResponse(Call<List<DetectModel>> call, Response<List<DetectModel>> response) {
//                if (!response.isSuccessful()){
//                    return;
//                }
//
//                List<DetectModel> detectList = response.body();
//                for (DetectModel detectModel : detectList){
//                    Log.d(TAG, "onResponse: id: " + detectModel.getId());
//                    Log.d(TAG, "onResponse: title: " + detectModel.getTitle());
//                    Log.d(TAG, "onResponse: body: " + detectModel.getText());
//                    Log.d(TAG, "onResponse: ");
//                }
//
//                Log.d(TAG, "onResponse: crottin bang ahahah");
//            }
//
//            @Override
//            public void onFailure(Call<List<DetectModel>> call, Throwable t) {
//                Log.d(TAG, "onFailure: boom bitch ahaha iihih");
//            }
//        });
//
//        mainViewModel.getApiEndpoint().getEx1(1, "comments").enqueue(new Callback<List<Ex1Model>>() {
//            @Override
//            public void onResponse(Call<List<Ex1Model>> call, Response<List<Ex1Model>> response) {
//                if (!response.isSuccessful()){
//                    return;
//                }
//
//                List<Ex1Model> ex1List = response.body();
//                for (Ex1Model ex1 : ex1List){
//                    Log.d(TAG, "onResponse: postId: " + ex1.getPostId());
//                    Log.d(TAG, "onResponse: email: " + ex1.getEmail());
//                    Log.d(TAG, "onResponse: ");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Ex1Model>> call, Throwable t) {
//                Log.d(TAG, "onFailure: lu ngapa sehj");
//            }
//        });

//        Log.d(TAG, "setupRetrofit: cek boss ahahahaha");
//        mainViewModel.getApiEndpoint().postDetect("https://kumacdn.club/wp-content/uploads/S/Shonen%20no%20Abyss/Chapter%2040/012.jpg", true, "ja")
//                .enqueue(new Callback<Detect2Model>() {
//                    @Override
//                    public void onResponse(Call<Detect2Model> call, Response<Detect2Model> response) {
//                        if (!response.isSuccessful()){
//                            Log.d(TAG, "onResponse: napa boss?");
//                            Log.d(TAG, "onResponse: res: " + response.message());
//                            return;
//                        }
//                        Detect2Model detect = response.body();
//                        Log.d(TAG, "onResponse: masukk");
//                        if (detect != null) {
//                            Log.d(TAG, "onResponse: lang nihhh: " + detect.getLanguage());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Detect2Model> call, Throwable t) {
//                        Log.d(TAG, "onFailure: wtff menn");
//                    }
//                });
        DetectModel detectModel = new DetectModel("ngonto", "aowkawok kont");
        mainViewModel.getApiEndpoint().postDetect(detectModel).enqueue(new Callback<DetectModel>() {
            @Override
            public void onResponse(Call<DetectModel> call, Response<DetectModel> response) {
                if (!response.isSuccessful()){
                    Log.d(TAG, "onResponse: res: " + response.code() + " " + response.message());
                    return;
                }

                DetectModel detect = response.body();
                Log.d(TAG, "onResponse: title: " + detect.getTitle());
                Log.d(TAG, "onResponse: text: " + detect.getText());
            }

            @Override
            public void onFailure(Call<DetectModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionHelper.PERM_CODE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new SelectDialog().show(getSupportFragmentManager(), "SELECT_DIALOG");
            } else {
                Log.d(TAG, "onActivityResult: Permission result DENIED brohh");
            }
        }
    }
}