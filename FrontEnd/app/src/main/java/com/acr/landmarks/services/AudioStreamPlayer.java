package com.acr.landmarks.services;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.acr.landmarks.services.contracts.IAudioService;

import java.io.IOException;

public class AudioStreamPlayer implements IAudioService {

    private String url;
    private MediaPlayer mediaPlayer;
    private boolean isAudioLoaded;

    public AudioStreamPlayer(String audioUrl){
        this.url = audioUrl;
        this.mediaPlayer = new MediaPlayer();
        this.isAudioLoaded = false;
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
    public void load(String fileName) {
        setMediaPlayerTarget(fileName);
        this.mediaPlayer.prepareAsync();
        isAudioLoaded = true;
    }

    public void reset(){
        this.mediaPlayer.reset();
        audioAttributesConfig();
        this.isAudioLoaded = false;
    }

    @Override
    public void play() {
        if(isAudioLoaded) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void stop() {
        this.mediaPlayer.stop();
    }

    @Override
    public void pause(){
        this.mediaPlayer.pause();
    }

    @Override
    public boolean isAudioLoaded(){
        return  this.isAudioLoaded;
    }

    private void setMediaPlayerTarget(String filename){
        try {
            this.mediaPlayer.setDataSource(this.url+filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}