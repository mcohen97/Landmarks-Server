package com.acr.landmarks.models;

import java.util.List;

public class Tour {
    private List<Landmark> tourLandmarks;
    private String iconBase64;
    private int id;
    private String name;
    private String descripcion;
    private String tematica;
    private String zona;

    public Tour() {
    }


    public Tour(List<Landmark> tourLandmarks, int id, String descripcion, String tematica, String zona) {
        this.tourLandmarks = tourLandmarks;
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

    public List<Landmark> getTourLandmarks() {
        return tourLandmarks;
    }

    public int getId() {
        return id;
    }

    public void setTourLandmarks(List<Landmark> tourLandmarks) {
        this.tourLandmarks = tourLandmarks;
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
