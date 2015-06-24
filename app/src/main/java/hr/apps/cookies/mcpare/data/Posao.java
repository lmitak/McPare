package hr.apps.cookies.mcpare.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lmita_000 on 23.6.2015..
 */
public class Posao implements Parcelable, Comparable {

    private int id;
    private int pozicija_id;
    private long pocetak, kraj;
    private Pozicija pozicija;


    public Posao(){};

    public Posao(String pozicija,int pozicija_id, long pocetak, long kraj){
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.pozicija_id = pozicija_id;
        this.pozicija = new Pozicija(pozicija_id, pozicija);
    }

    public Posao(String pozicija, int id, int pozicija_id, long pocetak, long kraj){
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.pozicija_id = pozicija_id;
        this.id = id;
        this.pozicija = new Pozicija(pozicija_id, pozicija);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPozicija_id() {
        return pozicija_id;
    }

    public void setPozicija_id(int pozicija_id) {
        this.pozicija_id = pozicija_id;
    }

    public long getPocetak() {
        return pocetak;
    }

    public void setPocetak(long pocetak) {
        this.pocetak = pocetak;
    }

    public long getKraj() {
        return kraj;
    }

    public void setKraj(long kraj) {
        this.kraj = kraj;
    }

    public Pozicija getPozicija() {
        return pozicija;
    }

    public void setPozicija(String pozicija, int pozicija_id) {
        this.pozicija = new Pozicija(pozicija_id, pozicija);
    }


    @Override
    public int describeContents() {
        int result = id;
        result = 31 * result + pozicija_id;
        result = 31 * result + (int) (pocetak ^ (pocetak >>> 32));
        result = 31 * result + (int) (kraj ^ (kraj >>> 32));
        result = 31 * result + (pozicija != null ? pozicija.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.pozicija_id);
        dest.writeLong(this.pocetak);
        dest.writeLong(this.kraj);
        dest.writeParcelable(this.pozicija, 0);
    }

    protected Posao(Parcel in) {
        this.id = in.readInt();
        this.pozicija_id = in.readInt();
        this.pocetak = in.readLong();
        this.kraj = in.readLong();
        this.pozicija = in.readParcelable(Pozicija.class.getClassLoader());
    }

    public static final Creator<Posao> CREATOR = new Creator<Posao>() {
        public Posao createFromParcel(Parcel source) {
            return new Posao(source);
        }

        public Posao[] newArray(int size) {
            return new Posao[size];
        }
    };

    @Override
    public int compareTo(Object o) {

        Posao p = (Posao) o;
        if (pocetak > p.pocetak){
            return 1;
        }else if (pocetak == p.pocetak){
            return 0;
        }else {
            return -1;
        }
    }
}
