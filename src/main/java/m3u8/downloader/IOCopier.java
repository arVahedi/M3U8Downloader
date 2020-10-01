package m3u8.downloader;

import lombok.Cleanup;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class IOCopier {

    public static void joinFiles(File destination, File source) throws IOException {
        @Cleanup OutputStream output = createAppendableStream(destination);
        appendFile(output, source);
    }

    private static BufferedOutputStream createAppendableStream(File destination) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(destination, true));
    }

    private static void appendFile(OutputStream output, File source) throws IOException {
        @Cleanup InputStream input = new BufferedInputStream(new FileInputStream(source));
        IOUtils.copy(input, output);
    }
}
