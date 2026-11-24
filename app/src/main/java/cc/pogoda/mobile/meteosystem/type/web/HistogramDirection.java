package cc.pogoda.mobile.meteosystem.type.web;

/**
 * Histogram of wind direction. Each bin holds a number of measurements with wind direction
 * within the range given in its description, lower bound inclusive and upper bound exclusive.
 */
public class HistogramDirection {

    /** How many measurements have been used to create this histogram */
    public int totalRecords;

    /** UTC timestamp in UNIX epoch of the oldest measurement used */
    public long oldestRecordTimestamp;

    /** UTC timestamp in UNIX epoch of the newest measurement used */
    public long newestRecordTimestamp;

    /** N to NNE, 0 - 22.5 degrees */
    public int nToNne;

    /** NNE to NE, 22.5 - 45 degrees */
    public int nneToNe;

    /** NE to ENE, 45 - 67.5 degrees */
    public int neToEne;

    /** ENE to E, 67.5 - 90 degrees */
    public int eneToE;

    /** E to ESE, 90 - 112.5 degrees */
    public int eToEse;

    /** ESE to SE, 112.5 - 135 degrees */
    public int eseToSe;

    /** SE to SSE, 135 - 157.5 degrees */
    public int seToSse;

    /** SSE to S, 157.5 - 180 degrees */
    public int sseToS;

    /** S to SSW, 180 - 202.5 degrees */
    public int sToSsw;

    /** SSW to SW, 202.5 - 225 degrees */
    public int sswToSw;

    /** SW to WSW, 225 - 247.5 degrees */
    public int swToWsw;

    /** WSW to W, 247.5 - 270 degrees */
    public int wswToW;

    /** W to WNW, 270 - 292.5 degrees */
    public int wToWnw;

    /** WNW to NW, 292.5 - 315 degrees */
    public int wnwToNw;

    /** NW to NNW, 315 - 337.5 degrees */
    public int nwToNnw;

    /** NNW to N, 337.5 - 360 degrees (360 inclusive) */
    public int nnwToN;
}
