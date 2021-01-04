package cc.pogoda.mobile.pogodacc.type;

import java.io.Serializable;

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
        return stationNameTextColor;
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

    String systemName;

    String displayedName;

    String displayedLocation;

    String sponsorUrl;

    String imageUrl;

    int imageAlign;

    int stationNameTextColor;

    float lat;

    float lon;


}
