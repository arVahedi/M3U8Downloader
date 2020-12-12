package m3u8.downloader.assets;

public enum FileExtension {
    MP4(".mp4"),
    MPEG(".mpeg");

    private String postfix;

    FileExtension(String postfix) {
        this.postfix = postfix;
    }


    public String getPostfix() {
        return postfix;
    }
}
