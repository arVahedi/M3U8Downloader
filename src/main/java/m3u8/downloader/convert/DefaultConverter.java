package m3u8.downloader.convert;

import m3u8.downloader.assets.FileExtension;
import org.apache.commons.io.FilenameUtils;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.encode.enums.X264_PROFILE;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.util.Collections;

public class DefaultConverter extends Converter {

    @Override
    public File convert(File source, FileExtension targetExtension) throws EncoderException {
        /* Step 1. Set Audio Attrributes for conversion*/
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("aac");
        // here 64kbit/s is 64000
        audio.setBitRate(64000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        /* Step 2. Set Video Attributes for conversion*/
        VideoAttributes video = new VideoAttributes();
        video.setCodec("h264");
        video.setX264Profile(X264_PROFILE.BASELINE);
        // Here 4000 kbps video is 4_000_000
        video.setBitRate(4_000_000);
        // More the frames more quality and size, but keep it low based on devices like mobile
        video.setFrameRate(30);
        video.setSize(new VideoSize(1920, 1080));

        /* Step 3. Set Encoding Attributes*/
        EncodingAttributes attributes = new EncodingAttributes();
        attributes.setOutputFormat(targetExtension.toString().toLowerCase());
        attributes.setAudioAttributes(audio);
        attributes.setVideoAttributes(video);

        File targetFile = new File(FilenameUtils.removeExtension(source.getAbsolutePath()) + targetExtension.getPostfix());
        Encoder encoder = new Encoder();
        encoder.encode(Collections.singletonList(new MultimediaObject(source)), targetFile, attributes);
        return targetFile;
    }
}
