package com.eskdr.eskandar;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStorageDirectory;

public class VoiceRecorder extends Fragment {

    private static final String filename = "voice.mp4";

    private MediaRecorder recorder = null;
    private File voice = null;
    private MediaPlayer player = null;
    private String path = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_recorder, container, false);

        Button start = (Button) view.findViewById(R.id.start_record);
        Button stop  = (Button) view.findViewById(R.id.stop_record);
        Button play  = (Button) view.findViewById(R.id.play_record);

        start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecord();
            }
        });
        stop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecord();
            }
        });
        play.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                playRecord();
            }
        });

        return view;
    }

    private void startRecord() {
        voice = new File(getExternalStorageDirectory(), filename);

        if (voice.exists()) {
            voice.delete();
        } else {
        }

        path = voice.getAbsolutePath();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(path);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void stopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void playRecord(){
        try{
            player = new MediaPlayer();
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}