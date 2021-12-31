package m3u8.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
//        FileUtils.copyURLToFile(resourceURL, result, CONNECT_TIMEOUT_MILLIS, READ_TIMEOUT_MILLIS);
        download(resourceURL, result);
        return result;
    }

    private static void download(URL resource, File file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(resource.toString());
        Core.HTTP_HEADERS.forEach(httpGet::addHeader);

        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity responseEntity = httpResponse.getEntity();

        if (responseEntity != null) {
            FileUtils.copyInputStreamToFile(responseEntity.getContent(), file);
        }

        httpGet.releaseConnection();
    }
}
