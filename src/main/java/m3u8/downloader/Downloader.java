package m3u8.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Downloader {

    public static int CONNECT_TIMEOUT_MILLIS = 60000;
    public static int READ_TIMEOUT_MILLIS = 60000;

    public static File download(String resourceAddress) throws IOException {
        URL resourceURL = new URL(resourceAddress);
        File result = File.createTempFile(FilenameUtils.getBaseName(resourceURL.getPath()), FilenameUtils.getExtension(resourceURL.getPath()));
        result.deleteOnExit();
        FileUtils.copyURLToFile(resourceURL, result, CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);
        return result;
    }

}
