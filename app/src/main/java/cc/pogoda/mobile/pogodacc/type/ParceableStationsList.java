package cc.pogoda.mobile.pogodacc.type;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ParceableStationsList implements Parcelable {

    ArrayList<WeatherStation> list;

    int listSize;

    public ArrayList<WeatherStation> getList() {
        return list;
    }

    public static ParceableStationsList createFromStdList(List<WeatherStation> stationList) {
        ParceableStationsList out = new ParceableStationsList();

        out.list = new ArrayList<>(stationList);
        out.listSize = stationList.size();

        return out;

    }

    public static final Creator<ParceableStationsList> CREATOR = new Creator<ParceableStationsList>() {
        @Override
        public ParceableStationsList createFromParcel(Parcel in) {
            return new ParceableStationsList(in);
        }

        @Override
        public ParceableStationsList[] newArray(int size) {
            return new ParceableStationsList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (list != null) {
            listSize = list.size();

            parcel.writeInt(listSize);

            for (WeatherStation s : list) {
                parcel.writeString(s.getSystemName());
                parcel.writeString(s.getDisplayedName());
                parcel.writeString(s.getDisplayedLocation());
                parcel.writeString(s.getSponsorUrl());
                parcel.writeString(s.getImageUrl());

                parcel.writeInt(s.getImageAlign());
                parcel.writeInt(s.getStationNameTextColor());

                parcel.writeFloat(s.getLat());
                parcel.writeFloat(s.getLon());

                parcel.writeString(s.getMoreInfo());

                parcel.writeInt(s.getAvailableParameters().humidity ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().qnh ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().windDirection ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().windGusts ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().windSpeed ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().rain ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().waterTemperature ? 1 : 0);
                parcel.writeInt(s.getAvailableParameters().airTemperature ? 1 : 0);

            }
        }
    }

    protected ParceableStationsList(Parcel in) {
        list = new ArrayList<>();
        listSize = in.readInt();

        for (int i = 0; i < listSize; i++) {
            WeatherStation wx = new WeatherStation();

            wx.systemName = in.readString();
            wx.displayedName = in.readString();
            wx.displayedLocation = in.readString();
            wx.sponsorUrl = in.readString();
            wx.imageUrl = in.readString();

            wx.imageAlign = in.readInt();
            wx.stationNameTextColor = in.readInt();

            wx.lat = in.readFloat();
            wx.lon = in.readFloat();

            wx.moreInfo = in.readString();

            AvailableParameters params = new AvailableParameters();

            params.humidity = (in.readInt() > 0) ? true : false;
            params.qnh = (in.readInt() > 0) ? true : false;
            params.windDirection = (in.readInt() > 0) ? true : false;
            params.windGusts = (in.readInt() > 0) ? true : false;
            params.windSpeed = (in.readInt() > 0) ? true : false;
            params.rain = (in.readInt() > 0) ? true : false;
            params.waterTemperature = (in.readInt() > 0) ? true : false;
            params.airTemperature = (in.readInt() > 0) ? true : false;

            wx.setAvailableParameters(params);

            list.add(wx);
        }

    }

    public ParceableStationsList() {

    }
}
