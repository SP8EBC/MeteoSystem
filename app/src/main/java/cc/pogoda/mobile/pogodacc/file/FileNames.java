package cc.pogoda.mobile.pogodacc.file;

import android.content.Context;

import java.io.File;

public class FileNames {

    public static final String DIRECTORY = "files";

    public static final String FAVS = "favourites.json";
    public static final String CONF = "app_configuration.json";

    private Context ctx;

    public FileNames(Context context) {
        ctx = context;
    }

    public File getDirectory() {
        return ctx.getDir(DIRECTORY, Context.MODE_PRIVATE);
    }

    public File getFavJsonFile() {
        File dir = this.getDirectory();

        return new File(dir.getAbsolutePath() + "/" + FAVS);

    }

    public File getAppConfigurationFile() {
        File dir = this.getDirectory();

        return new File(dir.getAbsolutePath() + "/" + CONF);
    }
}
