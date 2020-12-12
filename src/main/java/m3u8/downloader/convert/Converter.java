package m3u8.downloader.convert;

import m3u8.downloader.assets.FileExtension;
import ws.schild.jave.EncoderException;

import java.io.File;

public abstract class Converter {

    public File convert(String sourceAddress, FileExtension targetExtension) throws EncoderException {
        return convert(new File(sourceAddress), targetExtension);
    }

    public abstract File convert(File source, FileExtension targetExtension) throws EncoderException;
}
