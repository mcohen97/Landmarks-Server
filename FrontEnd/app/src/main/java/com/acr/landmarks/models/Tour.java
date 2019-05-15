package com.acr.landmarks.models;

import java.util.List;

public class Tour {
    private List<Integer> tourLandmarksIds;
    private String iconBase64;
    private int id;
    private String name;
    private String descripcion;
    private String tematica;
    private String zona;

    public Tour() {
    }


    public Tour(List<Integer> tourLandmarks, int id, String descripcion, String tematica, String zona) {
        this.tourLandmarksIds = tourLandmarks;
        this.id = id;
        this.descripcion = descripcion;
        this.tematica = tematica;
        this.zona = zona;
    }

    public String getIconBase64() {
        return iconBase64;
    }

    public void setIconBase64(String iconBase64) {
        this.iconBase64 = iconBase64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getTourLandmarks() {
        return tourLandmarksIds;
    }

    public int getId() {
        return id;
    }

    public void setTourLandmarks(List<Integer> tourLandmarks) {
        this.tourLandmarksIds = tourLandmarks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
