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

                parcel.writeBoolean(s.getAvailableParameters().humidity);
                parcel.writeBoolean(s.getAvailableParameters().qnh);
                parcel.writeBoolean(s.getAvailableParameters().windDirection);
                parcel.writeBoolean(s.getAvailableParameters().windGusts);
                parcel.writeBoolean(s.getAvailableParameters().windSpeed);
                parcel.writeBoolean(s.getAvailableParameters().rain);
                parcel.writeBoolean(s.getAvailableParameters().waterTemperature);
                parcel.writeBoolean(s.getAvailableParameters().airTemperature);

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

            params.humidity = in.readBoolean();
            params.qnh = in.readBoolean();
            params.windDirection = in.readBoolean();
            params.windGusts = in.readBoolean();
            params.windSpeed = in.readBoolean();
            params.rain = in.readBoolean();
            params.waterTemperature = in.readBoolean();
            params.airTemperature = in.readBoolean();

            wx.setAvailableParameters(params);

            list.add(wx);
        }

    }

    public ParceableStationsList() {

    }
}
