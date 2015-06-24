package hr.apps.cookies.mcpare.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lmita_000 on 23.6.2015..
 */
public class Pozicija implements Parcelable {
    private int id;
    private String ime_pozicija;


    public Pozicija(int id, String ime_pozicija){
        this.id = id;
        this.ime_pozicija = ime_pozicija;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIme_pozicija() {
        return ime_pozicija;
    }

    public void setIme_pozicija(String ime_pozicija) {
        this.ime_pozicija = ime_pozicija;
    }

    @Override
    public int describeContents() {
        int result = id;
        result = 31 * result + (ime_pozicija != null ? ime_pozicija.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.ime_pozicija);
    }

    public Pozicija() {
    }

    protected Pozicija(Parcel in) {
        this.id = in.readInt();
        this.ime_pozicija = in.readString();
    }

    public static final Parcelable.Creator<Pozicija> CREATOR = new Parcelable.Creator<Pozicija>() {
        public Pozicija createFromParcel(Parcel source) {
            return new Pozicija(source);
        }

        public Pozicija[] newArray(int size) {
            return new Pozicija[size];
        }
    };

}
