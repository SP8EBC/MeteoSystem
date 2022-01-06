package cc.pogoda.mobile.meteosystem.file;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CopyLog {

    public static class CopyLogRunner implements Runnable {

        InputStreamReader streamReader;
        OutputStreamWriter streamWriter;

        int logFileLn;

        public CopyLogRunner(InputStreamReader _stream_reader, OutputStreamWriter _stream_writer, int _log_file_ln) {
            streamReader = _stream_reader;
            streamWriter = _stream_writer;
            logFileLn = _log_file_ln;
        }

        @Override
        public void run() {
            try {
                char buffer[] = new char[logFileLn];

                streamReader.read(buffer);

                streamWriter.write(buffer);

                streamReader.close();
                streamWriter.flush();
                streamWriter.close();

                Logger.info("[CopyLog][CopyLogRunner][run][log file copied succesfully]");
            }
            catch (IOException e) {
                Logger.error("[CopyLog][CopyLogRunner][run][IOException e = " + e.getLocalizedMessage() +"]");
            }
        }
    }

    public static void forDay(FileNames _file_names, LocalDateTime _date, OutputStream _out) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        File baseDir = _file_names.getDirectory();

        File logfile = new File(baseDir.getAbsolutePath() + "/logs/log_" + _date.format(formatter) + ".txt");

        Logger.debug("[CopyLog][forDay][logfile.getAbsolutePath() = " + logfile.getAbsolutePath() +"][logfile.length() = " + logfile.length() +"]");

        try {
            // create an input stream to load log file
            FileInputStream fns = new FileInputStream(logfile);
            InputStreamReader streamReader = new InputStreamReader(fns);

            // create output stream writer to copy log file into
            OutputStreamWriter writer = new OutputStreamWriter(_out);

            CopyLogRunner runner = new CopyLogRunner(streamReader, writer, (int)logfile.length());

            Thread t = new Thread(runner);

            t.start();


        }
        catch (IOException e) {
            Logger.error("[CopyLog][forDay][IOException e = " + e.getLocalizedMessage() +"]");
        }

        // log_{date:yyyy-MM-dd}.txt

    }
}
