package cc.pogoda.mobile.pogodacc.type;

import androidx.annotation.Nullable;

import java.io.Serializable;

import cc.pogoda.mobile.pogodacc.activity.trend.pressure.PressureTrendFragment;

public class WeatherStation implements Serializable {

    public WeatherStation() {

    }

    public WeatherStation(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getDisplayedLocation() {
        return displayedLocation;
    }

    public void setDisplayedLocation(String displayedLocation) {
        this.displayedLocation = displayedLocation;
    }

    public String getSponsorUrl() {
        return sponsorUrl;
    }

    public void setSponsorUrl(String sponsorUrl) {
        this.sponsorUrl = sponsorUrl;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStationNameTextColor() {
        if (stationNameTextColor == 0) {
            return -16777216;
        }
        else {
            return stationNameTextColor;
        }
    }

    public void setStationNameTextColor(int stationNameTextColor) {
        this.stationNameTextColor = stationNameTextColor;
    }

    public int getImageAlign() {
        return imageAlign;
    }

    public void setImageAlign(int imageAlign) {
        this.imageAlign = imageAlign;
    }

    public AvailableParameters getAvailableParameters() {
        return availableParameters;
    }

    public void setAvailableParameters(AvailableParameters availableParameters) {
        this.availableParameters = availableParameters;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean out = false;

        if (obj instanceof WeatherStation) {
            if (((WeatherStation) obj).systemName.equals(this.systemName)) {
                out = true;
            }
        }
        else {
            ;
        }

        return out;
    }

    String systemName;

    String displayedName;

    String displayedLocation;

    public String getCallsignSsid() {
        return callsignSsid;
    }

    public void setCallsignSsid(String callsign, int ssid) {
        if (ssid < 16)
            this.callsignSsid = callsign + "-" + ssid;
    }

    public void setCallsignSsid(String callsignSsid) {
        this.callsignSsid = callsignSsid;
    }

    String callsignSsid;

    String sponsorUrl;

    String imageUrl;

    public String getTimezone() {
        return timezone.trim();
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    String timezone;

    int imageAlign;

    int stationNameTextColor;

    float lat;

    float lon;

    AvailableParameters availableParameters;

    String moreInfo;


}
