package com.example.kevinluu.doyouknowtheway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;

import com.ibm.watson.developer_cloud.service.exception.ForbiddenException;
import com.ibm.watson.developer_cloud.service.exception.NotFoundException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main2Activity extends AppCompatActivity implements Runnable{

    public static String apipath; // used to get the path of the picture from the camera to use in the visual api
    public VisualRecognition service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();

        String path = intent.getStringExtra(MainActivity.bestpath); // get the path of the picture from MainActivity
        apipath = path;
        (new Thread(new Main2Activity())).start(); // start the thread for the visual api
        Intent intent1 = new Intent(this,MainActivity.class);
        startActivity(intent1);
    }

    // run the thread used for visual recognition api
    public void run(){
        service = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20
        );
        service.setApiKey("7f2daee8b7f01422103d477d947c72895896bd58"); // used on 28 jan ( think expires after 30 days)
        getPathIBM(apipath, service);
    }

    // takes the picture and the path
    public void getPathIBM(String path, VisualRecognition service) {
        try {
            InputStream imagesStream = new FileInputStream(path);
            String[] arr = path.split("/");
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(imagesStream)
                    .imagesFileContentType("jpg")
                    .imagesFilename(arr[arr.length - 1])
                    .build();
            ClassifiedImages result = service.classify(classifyOptions).execute();

            // calls the function to get the text from the json
            jsonToText(result.getImages().get(0).getClassifiers().get(0).getClasses().toArray());

        } catch (FileNotFoundException ex) {
            Log.e("error", "file not found");
        } catch (NotFoundException ex) {
            Log.e("error", "not found exception");
        } catch (ForbiddenException ex) {
            Log.e("error", "authentication not good");
        }
    }

    // function used to change the json from the visual api to a text
    public void jsonToText(Object[] arr){
        // Transforms gets classes array from JSON
        Object[] classArr = arr;

        ArrayList<Information> information = new ArrayList<>();

        for (int i = 0; i < classArr.length; i++) {
            String a = classArr[i].toString().replaceAll("\\s", ""); // remove linebreaks (Zhen needs to confirm this)
            String regexString = Pattern.quote("{\"class\":") + "(.*?)" + Pattern.quote(","); // regex used to find words after "class":
            Pattern pattern = Pattern.compile(regexString);
            Matcher matcher = pattern.matcher(a);
            String textInBetween = "";
            while (matcher.find()) {
                textInBetween = matcher.group(1); // find a class word
            }

            String scoreInBetween = "";
            Pattern pattern2 = Pattern.compile("(\\d.\\d\\d)\\d"); // regex to find the score
            Matcher m = pattern2.matcher(a);
            while(m.find()) {
                scoreInBetween = m.group(); // find the score
            }

            Information ok = new Information(textInBetween, scoreInBetween); // create the object Information
            information.add(ok); // add the Information Object to the arraylist
        }
        // using the text, it runs the text to speech api
        runTextToSpeech(information);
    }

    // function used to run the text to speech api
    public void runTextToSpeech(ArrayList<Information>information){
        TextToSpeech.setArrayList(information);
        Runnable textToSpeech = new TextToSpeech();
        Thread textToSpeechThread = new Thread(textToSpeech);
        textToSpeechThread.start();
    }

}
