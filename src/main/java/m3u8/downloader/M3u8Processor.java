package m3u8.downloader;

import lombok.Data;
import m3u8.downloader.assets.FileExtension;
import m3u8.downloader.convert.Converter;
import m3u8.downloader.convert.DefaultConverter;
import m3u8.downloader.log.LogFactory;
import m3u8.downloader.utility.random.RandomUtility;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static m3u8.downloader.Core.FILE_EXTENSION;
import static m3u8.downloader.Core.SAVE_DIRECTORY_PATH;
import static m3u8.downloader.M3u8Processor.ParserMode.CONCAT_CONTENT_WITH_BASE_URL;
import static m3u8.downloader.M3u8Processor.ParserMode.RAW_CONTENT;

@Data
public class M3u8Processor {

    private String fileUrl;
    private String baseUrlPart;
    private Queue<String> resourceQueue = new ConcurrentLinkedQueue<>();
    private NavigableMap<Integer, File> resultSegmentsMap = new TreeMap<>();
    private ParserMode mode;
    private Converter converter = new DefaultConverter();

    private int downloadProcessPercentage = 0;

    public M3u8Processor(String fileUrl, ParserMode mode) throws IOException {
        this.fileUrl = fileUrl;
        this.mode = mode;
        parse();
    }

    public File download(int segmentPerDownload) throws Exception {
        String finalFileName = RandomUtility.getRandomUUID();
        File result = new File(SAVE_DIRECTORY_PATH + finalFileName + FileExtension.MPEG.getPostfix());

        ThreadPoolExecutor downloaderThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(segmentPerDownload);
        int totalItems = this.resourceQueue.size();
        LogFactory.getLogger(this.getClass()).info(String.format("[%d] files will be downloading...", totalItems));
        int index = 0;
        final boolean[] downlaodSegmentChecksum = {true};
        while (!this.resourceQueue.isEmpty()) {
            index++;
            String item = this.resourceQueue.poll();

            final int finalIndex = index;
            final String finalItem = item;
            downloaderThreadPool.execute(new Runnable() {
                int localIndex = finalIndex;
                String localItem = finalItem;
                int maxTries = 10;
                int currentTries = 1;

                @Override
                public void run() {
                    while (this.currentTries <= this.maxTries) {
                        try {
                            LogFactory.getLogger(this.getClass()).debug(String.format("Downloading item: %s", this.localItem));

                            if (this.currentTries > 1) {
                                LogFactory.getLogger(this.getClass()).warn(String.format("Retry %d-nd times for download : %s", this.currentTries, this.localItem));
                            }

                            File tsTemporaryFile = Downloader.download(this.localItem);
                            resultSegmentsMap.put(this.localIndex, tsTemporaryFile);
                            LogFactory.getLogger(this.getClass()).info(String.format("Downloading item [%d of %d]: %s",
                                    resultSegmentsMap.size(),
                                    totalItems,
                                    calculateProcessPercentage(totalItems, resultSegmentsMap.size())));
                            break;
                        } catch (Exception e) {
                            LogFactory.getLogger(this.getClass()).error(e.getMessage(), e);
                            if (++this.currentTries == this.maxTries) {
                                downlaodSegmentChecksum[0] = false;
                            }
                        }
                    }
                }
            });
        }

        downloaderThreadPool.shutdown();
        try {
            downloaderThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            LogFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }

        if (!downlaodSegmentChecksum[0]) {
            LogFactory.getLogger(this.getClass()).error("Download Failed!!!");
            throw new Exception("Download failed!!");
        }

        this.resultSegmentsMap.forEach((key, segment) -> {
            try {
                IOCopier.joinFiles(result, segment);
            } catch (IOException e) {
                LogFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        });


        File finalResult = result;
        if (FILE_EXTENSION != FileExtension.MPEG) {
            LogFactory.getLogger(this.getClass()).info(String.format("Starting convert file to %s...", FILE_EXTENSION.toString()));
            finalResult = converter.convert(result, FILE_EXTENSION);
            Files.delete(result.toPath());
        }

        return finalResult;
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
