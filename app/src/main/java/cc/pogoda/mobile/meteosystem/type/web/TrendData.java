package cc.pogoda.mobile.meteosystem.type.web;

public class TrendData {

    private static final String SPACE = " ";
    private static final String NO_SPACE = "";

    private float current_value;

    private float two_hours_value;

    private float four_hours_value;

    private float six_hours_value;

    private float eight_hours_value;

    public String getCurrentVal(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s", Math.round(current_value), s);
        }
        else {
            out = String.format("%.1f%s", current_value, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getTwoHoursVal(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s", Math.round(two_hours_value), s);
        }
        else {
            out = String.format("%.1f%s", two_hours_value, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getFourHoursVal(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s", Math.round(four_hours_value), s);
        }
        else {
            out = String.format("%.1f%s", four_hours_value, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getSixHoursVal(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s", Math.round(six_hours_value), s);
        }
        else {
            out = String.format("%.1f%s", six_hours_value, s);
        }

        out = out.replace(',', '.');

        return out;
    }

    public String getEightHoursVal(boolean space, boolean round) {
        String out;

        String s;

        if (space) {
            s = SPACE;
        }
        else {
            s = NO_SPACE;
        }

        if (round) {
            out = String.format("%d%s", Math.round(eight_hours_value), s);
        }
        else {
            out = String.format("%.1f%s", eight_hours_value, s);
        }

        out = out.replace(',', '.');

        return out;
    }

}

