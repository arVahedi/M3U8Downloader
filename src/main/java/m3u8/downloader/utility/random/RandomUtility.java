package m3u8.downloader.utility.random;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Created by gl4di4tor on 5/3/18.
 */
public class RandomUtility {

    private RandomUtility(){}

    public static int getRandomInteger() {
        Random rand = new Random();
        return 1 + rand.nextInt(2147483646);
    }

    public static int getSecureRandomInteger() {
        SecureRandom rand = new SecureRandom();
        return 1 + rand.nextInt(2147483646);
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
