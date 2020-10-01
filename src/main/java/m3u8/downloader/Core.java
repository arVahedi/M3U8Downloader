package m3u8.downloader;

import m3u8.downloader.log.LogFactory;

import java.io.File;
import java.io.IOException;

import static m3u8.downloader.M3u8Processor.ParserMode.CONCAT_CONTENT_WITH_BASE_URL;

public class Core {

    public static final String SAVE_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
    public static final String FILE_EXTENSION = ".mpeg";

    private static String m3u8FileAddress =
            ".m3u8";

    public static void main(String[] args) throws IOException {
        M3u8Processor m3U8Processor = new M3u8Processor(m3u8FileAddress, CONCAT_CONTENT_WITH_BASE_URL);
        File downloadedFile = m3U8Processor.download();

        LogFactory.getLogger(Core.class).info(String.format("Download complete and File saved in path: %s", downloadedFile.getAbsolutePath()));
    }
}
