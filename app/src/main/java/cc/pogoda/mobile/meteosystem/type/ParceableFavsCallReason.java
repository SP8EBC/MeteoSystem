package cc.pogoda.mobile.meteosystem.type;

import android.os.Parcel;
import android.os.Parcelable;

public class ParceableFavsCallReason  implements Parcelable {

    public enum Reason {
        FAVOURITES,
        EXPORT_SELECT,
        ALL_STATIONS
    }

    public Reason getReason() {
        return reason;
    }

    private Reason reason;

    public ParceableFavsCallReason(Reason r) {
        this.reason = r;
    }

    protected ParceableFavsCallReason(Parcel in) {
        int reasonInt = in.readInt();

        switch (reasonInt) {
            case 1: reason = Reason.FAVOURITES; break;
            case 2: reason = Reason.EXPORT_SELECT; break;
            case 3: reason = Reason.ALL_STATIONS; break;
            default: reason = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        switch (reason) {
            case FAVOURITES: dest.writeInt(1); break;
            case EXPORT_SELECT: dest.writeInt(2); break;
            case ALL_STATIONS: dest.writeInt(3); break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParceableFavsCallReason> CREATOR = new Creator<ParceableFavsCallReason>() {
        @Override
        public ParceableFavsCallReason createFromParcel(Parcel in) {
            return new ParceableFavsCallReason(in);
        }

        @Override
        public ParceableFavsCallReason[] newArray(int size) {
            return new ParceableFavsCallReason[size];
        }
    };

}
