package m3u8.downloader;

import lombok.Data;
import m3u8.downloader.log.LogFactory;
import m3u8.downloader.utility.random.RandomUtility;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static m3u8.downloader.Core.FILE_EXTENSION;
import static m3u8.downloader.Core.SAVE_DIRECTORY_PATH;
import static m3u8.downloader.M3u8Processor.ParserMode.CONCAT_CONTENT_WITH_BASE_URL;
import static m3u8.downloader.M3u8Processor.ParserMode.RAW_CONTENT;

@Data
public class M3u8Processor {

    private String fileUrl;
    private String baseUrlPart;
    private Queue<String> resourceQueue = new ConcurrentLinkedQueue<>();
    private ParserMode mode;

    private int downloadProcessPercentage = 0;

    public M3u8Processor(String fileUrl, ParserMode mode) throws IOException {
        this.fileUrl = fileUrl;
        this.mode = mode;
        parse();
    }

    public File download() throws IOException {
        String finalFileName = RandomUtility.getRandomUUID();
        File result = new File(SAVE_DIRECTORY_PATH + finalFileName + FILE_EXTENSION);

        int totalItems = this.resourceQueue.size();
        LogFactory.getLogger(this.getClass()).info(String.format("[%d] files will be downloading...", totalItems));
        while (!this.resourceQueue.isEmpty()) {
            String item = this.resourceQueue.poll();
            LogFactory.getLogger(this.getClass()).debug(String.format("Downloading item: %s", item));
            LogFactory.getLogger(this.getClass()).info(String.format("Downloading item [%d of %d]: %s",
                    totalItems - this.resourceQueue.size(),
                    totalItems,
                    calculateProcessPercentage(totalItems, totalItems - this.resourceQueue.size())));

            File tsTemporaryFile = Downloader.download(item);

            IOCopier.joinFiles(result, tsTemporaryFile);
        }
        return result;
    }

    private void parse() throws IOException {
        this.resourceQueue.clear();
        if (this.mode == CONCAT_CONTENT_WITH_BASE_URL) {
            extractBaseUrlPart();
        }

        //Download m3u8 file
        File m3u8File = Downloader.download(this.fileUrl);

        //Read content of m3u8 file
        List<String> lines = FileUtils.readLines(m3u8File, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (!line.startsWith("#")) {
                if (this.mode == CONCAT_CONTENT_WITH_BASE_URL) {
                    this.resourceQueue.offer(this.baseUrlPart + line);
                } else if (this.mode == RAW_CONTENT) {
                    this.resourceQueue.offer(line);
                }
            }

        }

        Downloader.download(this.fileUrl);
    }

    private void extractBaseUrlPart() {
        this.baseUrlPart = this.fileUrl.substring(0, this.fileUrl.lastIndexOf('/') + 1);
    }

    private String calculateProcessPercentage(int totalItems, int currentSate) {
        return Double.valueOf((currentSate * 100) / totalItems).intValue() + "%";
    }

    public enum ParserMode {
        RAW_CONTENT,
        CONCAT_CONTENT_WITH_BASE_URL
    }
}
