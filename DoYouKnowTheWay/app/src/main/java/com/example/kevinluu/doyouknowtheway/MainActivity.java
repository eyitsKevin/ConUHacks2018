package com.example.kevinluu.doyouknowtheway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE=1;
    private final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE=1;
    private final int MY_PERMISSIONS_INTERNET=1;

    public static String bestpath;
    Button button;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext(); // this looks useless but without it, it breaks!!! (Error: Failure delivering result ResultInfo)
        button = findViewById(R.id.cameraButton);

        getPermissions();
        button.setOnClickListener(new View.OnClickListener() {
            int i=0;
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                i++;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };
                if (i == 1) {
                    //Single click
                    handler.postDelayed(r, 250);
                } else if (i == 2) {
                    //Double click
                    i = 0;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }



    public void getPermissions(){
        // ask permission for INTERNET, READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                    PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_INTERNET);
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (data != null){
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));
                Log.d(finalFile.toString(), "ok");
                sendMessage(finalFile.toString());
            }}
    }

    // function used in TextToSpeech (prob better way to use it)
    public static Context getApplicationContext2(){
        return  context;
    }

    // function used to go from MainActivity to Main2Activity
    public void sendMessage(String path) {
        Intent intent = new Intent(this, Main2Activity.class);
        bestpath = path;
        intent.putExtra(bestpath,path);
        startActivity(intent);
    }

    // function used in onActivityResult()
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // function used in onActivityResult()
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}


