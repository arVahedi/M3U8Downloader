package m3u8.downloader;

import m3u8.downloader.assets.FileExtension;
import m3u8.downloader.log.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static m3u8.downloader.M3u8Processor.ParserMode.CONCAT_CONTENT_WITH_BASE_URL;

public class Core {
    public static final Map<String, String> HTTP_HEADERS = new HashMap<>();
    public static final String SAVE_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
    public static final FileExtension FILE_EXTENSION = FileExtension.MPEG;
    public static final int SEGMENT_PER_DOWNLOAD = 16;

    private static String m3u8FileAddress =
            "";

    static {
        HTTP_HEADERS.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36");
    }

    public static void main(String[] args) throws Exception {
        if (FILE_EXTENSION != FileExtension.MPEG) {
            LogFactory.getLogger(Core.class).warn("M3U8 files are originally in MPEG format, converting them to other formats can take a long time!!");
        }

        M3u8Processor m3U8Processor = new M3u8Processor(m3u8FileAddress, CONCAT_CONTENT_WITH_BASE_URL);
        File downloadedFile = m3U8Processor.download(SEGMENT_PER_DOWNLOAD);

        LogFactory.getLogger(Core.class).info(String.format("Download complete and File saved in path: %s", downloadedFile.getAbsolutePath()));
    }
}
