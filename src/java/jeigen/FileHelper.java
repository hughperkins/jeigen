package jeigen;

import java.io.*;

final class FileHelper {
    public static void copyBetweenStreams( InputStream inputStream, OutputStream outputStream ) throws Exception {
        byte[] buffer = new byte[1000000];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }
}

