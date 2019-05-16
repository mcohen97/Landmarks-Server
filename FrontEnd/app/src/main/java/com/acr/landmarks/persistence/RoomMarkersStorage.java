package com.acr.landmarks.persistence;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;



import com.acr.landmarks.models.Landmark;

import java.util.ArrayList;
import java.util.List;

public class RoomMarkersStorage implements LandmarkMarkersStorage {

    AppDatabase db;
    public  RoomMarkersStorage(Context c) {
    db = Room.databaseBuilder(c,
                AppDatabase.class, "landmarks").build();
    }
    @Override
    public List<Landmark> getSavedLandmarks(Location location, double radius) {
        ArrayList<Landmark> markers = new ArrayList<>();
        Cursor rawData = db.landmarksDao().getStoredLandmarks();
        while(rawData.moveToNext()){

            double lat = getDouble(rawData,"latitude");
            double lng = getDouble(rawData, "longitude");
            Location actual = new Location(new String());
            actual.setLatitude(lat);
            actual.setLongitude(lng);
            double distance = location.distanceTo(actual)/1000;

            if(distance<= radius){
                Landmark built = buildMarker(rawData,lat,lng);
                markers.add(built);
            }
        }
        return markers;
    }

    private Landmark buildMarker(Cursor cursor, double lat, double lng) {
        int id = getInt(cursor,"id");
        String title = getString(cursor,"title");
        byte [] image = getBlob(cursor, "image");
        String iconBase64 = new String(image);

        return new Landmark(id,title,lat,lng,iconBase64);
    }

    private double getDouble(Cursor cursor ,String name){
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getDouble(index);
    }

    private int getInt(Cursor cursor ,String name){
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getInt(index);
    }

    private String getString(Cursor cursor, String name){
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getString(index);
    }

    private byte[] getBlob(Cursor cursor, String name){
        int index = cursor.getColumnIndexOrThrow(name);
        return cursor.getBlob(index);
    }

    @Override
    public void deleteStorage() {
        db.landmarksDao().clear();
    }

    @Override
    public void insertLandmarks(List<Landmark> landmarks) {
        List<LandmarkMarkerEntity> converted = convertLandmarks(landmarks);
        db.landmarksDao().insertAll(converted);
    }

    private List<LandmarkMarkerEntity> convertLandmarks(List<Landmark> landmarks) {
        List<LandmarkMarkerEntity> converted = new ArrayList<>();
        for(Landmark l: landmarks) {
            LandmarkMarkerEntity entity = new LandmarkMarkerEntity();
            entity.id = l.getId();
            entity.latitude = l.getLat();
            entity.longitude = l.getLon();
            entity.title = l.getName();
            entity.image = l.getImg().getBytes();
            converted.add(entity);
        }
        return converted;
    }


}


