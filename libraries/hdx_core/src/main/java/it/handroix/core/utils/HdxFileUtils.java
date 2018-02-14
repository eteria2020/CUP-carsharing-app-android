package it.handroix.core.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HdxFileUtils {

    /**
     * Read file and convert its content to a Base64 string.
     * 
     * @param path File path
     * @param flags android.util.Base64 flags
     * @return
     */
    public static String encodeFileToBase64(String path, int flags) {
        File originalFile = new File(path);
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int)originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encode(bytes, flags));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return encodedBase64;
    }
    
}
