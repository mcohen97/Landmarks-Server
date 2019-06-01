package com.acr.landmarks.services;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.acr.landmarks.services.contracts.IAudioService;

import java.io.IOException;

public class AudioStreamPlayer implements IAudioService {

    private String url;
    private MediaPlayer mediaPlayer;

    public AudioStreamPlayer(String audioUrl){
        this.url = audioUrl;
        this.mediaPlayer = new MediaPlayer();
        audioAttributesConfig();
    }

    private void audioAttributesConfig() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        this.mediaPlayer.setAudioAttributes(attributes);
    }

    @Override
    public void play(String fileName) {
        if(this.mediaPlayer.isPlaying())
            this.mediaPlayer.release();
        
        setMediaPlayerTarget(fileName);
        this.mediaPlayer.prepareAsync();
        this.mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());


    }

    @Override
    public void stop() {
        this.mediaPlayer.release();
    }

    private void setMediaPlayerTarget(String filename){
        try {
            this.mediaPlayer.setDataSource(this.url+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
