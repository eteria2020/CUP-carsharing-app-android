package it.handroix.core.filesutils;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
/**
 * Created by luca on 04/07/14.
 */
public class HdxFileUtility {

    public static final String TAG = "HdxFileUtility";

    /**
     *
     * @param sourceZipFile
     * @param baseInstallFolder
     * @return -1 se errori, total size se ok
     */
    public static Long unpackZip(File sourceZipFile,
                                 File baseInstallFolder) {
        return unpackZip(sourceZipFile,baseInstallFolder, null, new HdxCancelChecker() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
    }

    /**
     * Unpack zip file into given directory
     * @param sourceZipFile
     * @param baseInstallFolder
     * @param progressPublisher
     * @param cancelChecker
     * @return -1 if errors, total size if ok
     */
    public static Long unpackZip(File sourceZipFile,
                                 File baseInstallFolder,
                                 HdxProgressPublisher progressPublisher,
                                 HdxCancelChecker cancelChecker) {
        ZipInputStream zis = null;
        FileOutputStream fout = null;
        Long totalCount = 0L;

        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceZipFile)));
            ZipEntry ze;

            while (!cancelChecker.isCancelled() && (ze = zis.getNextEntry()) != null) {
                // Update the size
                if (progressPublisher!=null && ze.getSize() != -1 && ze.getCompressedSize() != -1) {
                    progressPublisher.updateTotal(ze.getCompressedSize(), ze.getSize());
                }

                File fmd = new File(baseInstallFolder, ze.getName());

                if (ze.isDirectory()) {
                    if (!fmd.mkdirs()) throw new IOException("Cannot create " + fmd.getAbsolutePath());
                } else {
                    byte[] buffer = new byte[1024 * 4]; // 4K is ideal, see Commons IO

                    fmd.getParentFile().mkdirs(); // make what I need
                    fout = new FileOutputStream(fmd);
                    int count;

                    while (!cancelChecker.isCancelled() && (count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                        totalCount += count;
                        if(progressPublisher!=null) {
                            progressPublisher.publish((long) count);
                        }
                    }
                    fout.close();
                    zis.closeEntry();
                }
            }
        } catch(IOException e) {
            Log.e(TAG,e.getMessage());
            totalCount = -1L;
        } finally {
            if (zis != null) {
                IOUtils.closeQuietly(zis);
            }
            if (fout != null) {
                IOUtils.closeQuietly(fout);
            }
        }
        return totalCount;
    }

    /**
     * Delete recursively from given file/directory
     * @param fileOrDirectory
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if(fileOrDirectory.exists()) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }
            if (fileOrDirectory.delete()) {
                Log.d(TAG,fileOrDirectory.getAbsolutePath() + " cleaned!");
            }
        }
    }

    /**
     *
     * @param sourceFile
     * @param destinationFile
     * @return -1 if errors, total size if ok
     */
    public static Long copyFile (File sourceFile,
                                 File destinationFile) {
        Long totalSize = 0L;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destinationFile);
            totalSize = (long) IOUtils.copy(in, out);

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            totalSize = -1L;
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
        return totalSize;
    }

    /**
     *
     * @param sourceFile
     * @param destinationFile
     * @param progressPublisher
     * @param cancelChecker
     * @return -1 if errors, total size if ok
     */
    public static Long copyFile (File sourceFile,
                                 File destinationFile,
                                 HdxProgressPublisher progressPublisher,
                                 HdxCancelChecker cancelChecker) {
        Long totalSize = 0L;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(sourceFile);
            out = new FileOutputStream(destinationFile);

            byte[] buf = new byte[1024 * 4];
            int len;
            while (!cancelChecker.isCancelled() && (len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                if(progressPublisher!=null) {
                    progressPublisher.publish((long) len);
                }
                totalSize += len;
            }

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            totalSize = -1L;
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
        return totalSize;
    }

    /**
     *
     * @param srcBaseUrl
     * @param destDirectory
     * @param fileName
     * @return
     */
    public static boolean downloadFile(String srcBaseUrl,
                                 String destDirectory,
                                 String fileName) {

        return downloadFile(srcBaseUrl,destDirectory,fileName,null,new HdxCancelChecker() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        });
    }

    /**
     *
     * @param srcBaseUrl
     * @param destDirectory
     * @param fileName
     * @param progressPublisher
     * @param cancelChecker
     * @return
     */
    public static boolean downloadFile(String srcBaseUrl,
                                 String destDirectory,
                                 String fileName,
                                 HdxProgressPublisher progressPublisher,
                                 HdxCancelChecker cancelChecker) {
        boolean res= true;

        OutputStream output = null;
        InputStream input = null;
        try {

            URL url = new URL(srcBaseUrl+fileName);
            URLConnection conection = url.openConnection();
            conection.connect();

            // input stream to read file - with 8k buffer
            input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream to write file

            File file = new File(destDirectory + fileName);
            File dir = new File(file.getParent());
            dir.mkdirs();
            output = new FileOutputStream(file);

            int count;
            byte data[] = new byte[1024];
            while (!cancelChecker.isCancelled() && (count = input.read(data)) != -1) {
                // writing data to file
                output.write(data, 0, count);
                if(progressPublisher!=null) {
                    //notify activity of progress
                    progressPublisher.publish((long) count);
                }
            }
            // flushing output
            output.flush();
            Log.v(TAG,"Completed download of file: " + destDirectory + fileName);
        } catch (Exception e) {
            Log.e(TAG,"Error: "+ e.getMessage());
            res = false;
        } finally {
            if (output != null) {
                IOUtils.closeQuietly(output);
            }
            if (input != null) {
                IOUtils.closeQuietly(input);
            }
        }
        return res;
    }
}
