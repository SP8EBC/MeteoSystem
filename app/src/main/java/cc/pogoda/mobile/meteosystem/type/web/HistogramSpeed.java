package cc.pogoda.mobile.meteosystem.type.web;

/**
 * Histogram of wind speed or wind gusts. Each bin holds a number of measurements with value
 * within the range given in its description, lower bound inclusive and upper bound exclusive.
 */
public class HistogramSpeed {

    /** How many measurements have been used to create this histogram */
    public int totalRecords;

    /** UTC timestamp in UNIX epoch of the oldest measurement used */
    public long oldestRecordTimestamp;

    /** UTC timestamp in UNIX epoch of the newest measurement used */
    public long newestRecordTimestamp;

    /** 0 - 1 m/s */
    public int lessThanOne;

    /** 1 - 2 m/s */
    public int oneToTwo;

    /** 2 - 3 m/s */
    public int twoToThree;

    /** 3 - 4 m/s */
    public int threeToFour;

    /** 4 - 5 m/s */
    public int fourToFive;

    /** 5 - 6 m/s */
    public int fiveToSix;

    /** 6 - 7 m/s */
    public int sixToSeven;

    /** 7 - 8 m/s */
    public int sevenToEight;

    /** 8 - 9 m/s */
    public int eightToNine;

    /** 9 - 10 m/s */
    public int nineToTen;

    /** 10 m/s and above */
    public int overTen;
}
