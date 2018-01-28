package com.example.kevinluu.doyouknowtheway;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by kevinluu on 2018-01-27.
 */

public class TextToSpeech implements Runnable {

    ServiceCall call;
    List<Voice> voices;
    com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech service;
    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hello_world.wav";
    Context context;
    static ArrayList<Information> arrayList;

    @Override
    public void run() {
        service = new com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech();
        service.setUsernameAndPassword("48aa6a56-30e4-4645-99f3-75ccf7769967", "NwORktBQtMEd");
        voices = service.getVoices().execute(); //sets the voice
        context = MainActivity.getApplicationContext2();

        Log.i("Voices" , voices.toString());
        speech(voices);
        playFile();

    }

    public void playFile(){
        Uri file = Uri.parse(filepath);
        MediaPlayer mp = MediaPlayer.create(context , file);
        mp.setLooping(false);
        mp.start();
    }

    public static void setArrayList(ArrayList<Information> arrayList2){
         arrayList = arrayList2 ;
    }


    public void speech(List<Voice> voices) {
        try {
            String text = arrayList.get(0).toString() + arrayList.get(1).toString() + arrayList.get(2).toString();
            InputStream stream = service.synthesize(text, voices.get(2),
                    AudioFormat.WAV).execute();
            InputStream in = WaveUtils.reWriteWaveHeader(stream);
            OutputStream out = new FileOutputStream(filepath);
            Log.d("filepath", filepath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            Log.i("Info:", out.toString());
            out.close();
            in.close();
            stream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
