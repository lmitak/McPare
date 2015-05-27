package com.example.antonio.androidappseminar.data.model;

import java.sql.Date;

/**
 * Created by Antonio on 21.5.2015..
 */
public class Zapis {
    int id;
    String pozicija;
    Date datum_od;
    Date datum_do;
    Double osnovica;
    Double koefPlaca;

    public Zapis(){

    }


    public Zapis(String pozicija,Date datum_od,Date datum_do,Double osnovica,Double koefPlaca){
        this.pozicija = pozicija;
        this.datum_od = datum_od;
        this.datum_do = datum_do;
        this.osnovica = osnovica;
        this.koefPlaca = koefPlaca;
    }

    public Zapis(int id,String pozicija,Date datum_od,Date datum_do,Double osnovica,Double koefPlaca){
        this.id = id;
        this.pozicija = pozicija;
        this.datum_od = datum_od;
        this.datum_do = datum_do;
        this.osnovica = osnovica;
        this.koefPlaca = koefPlaca;
    }

    public String getPozicija() {
        return pozicija;
    }

    public void setPozicija(String pozicija) {
        this.pozicija = pozicija;
    }

    public Date getDatum_od() {
        return datum_od;
    }

    public void setDatum_od(Date datum_od) {
        this.datum_od = datum_od;
    }

    public Date getDatum_do() {
        return datum_do;
    }

    public void setDatum_do(Date datum_do) {
        this.datum_do = datum_do;
    }

    public Double getOsnovica() {
        return osnovica;
    }

    public void setOsnovica(Double osnovica) {
        this.osnovica = osnovica;
    }

    public Double getKoefPlaca() {
        return koefPlaca;
    }

    public void setKoefPlaca(Double koefPlaca) {
        this.koefPlaca = koefPlaca;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
