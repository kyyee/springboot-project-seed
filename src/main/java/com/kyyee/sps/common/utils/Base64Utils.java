package com.kyyee.sps.common.utils;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.framework.common.utils.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

@Slf4j
public class Base64Utils {
    public static final int IMAGE_BASE64_BUFFER_SIZE = 8 * 1024;

    public static final int IMAGE_URL_BUFFER_SIZE = 16 * 1024;

    public static final String NO_SPACE_LEFT_EN = "[\\s\\S]*No space left on device[\\s\\S]*";

    public static boolean isBase64(String base64) {
        if (StringUtils.isEmpty(base64)) {
            return false;
        }
        if (base64.length() % 4 != 0) {
            return false;
        }
        char[] chars = base64.toCharArray();
        for (char aChar : chars) {
            if ((aChar < 'a' || aChar > 'z') && (aChar < 'A' || aChar > 'Z') && (aChar < '0' || aChar > '9') && aChar != '+' && aChar != '/' && aChar != '=') {
                return false;
            }
        }
        return true;
    }

    public static String file2Base64Str(File file) throws IOException {
        String base64Str = FileUtils.readFileToString(file, "utf-8");
        return isBase64(base64Str) ? base64Str : null;
    }

    public static void imageBase64Str2File(String base64) {
        String tmp = System.getProperty("java.io.tmpdir");
        long id = SnowFlake.getId();
        String filePath = tmp + id;
        // 远程图片base64编码不为空
        if (StringUtils.isEmpty(base64)) {
            log.warn("image file:{} convert failed, base64 is empty", filePath);
            return;
        }

        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            log.warn("image file:{} has exists.", filePath);
        }

        // 自动去掉data:image/jpg;base64、data:image/jpeg;base64等，将" "替换为+
        String regex = "^data:image/\\w+;base64,";
        base64 = base64.replaceFirst(regex, "").replace(" ", "+");

        // 保存图片到文件目录
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(base64)));
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(imageFile.toPath()))) {

            // 缓冲字节数组
            byte[] buffer = new byte[IMAGE_BASE64_BUFFER_SIZE];

            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }

            compressFile(imageFile);

        } catch (Exception e) {
            log.error("imageBase64Str: {} [filename:{}] convert to file(image) failed, message:{}", base64, filePath, e.getMessage(), e);
            if (e.getMessage().matches(NO_SPACE_LEFT_EN)) {
                throw BaseException.of(BaseErrorCode.FILE_CREATE_ERROR, "剩余空间不足，导出失败，请清理磁盘空间后重试");
            }
        }
    }

    public static String url2Base64Str(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String tmp = System.getProperty("java.io.tmpdir");
        long id = SnowFlake.getId();
        String filePath = tmp + id;
        URLConnection connection;
        if (url.startsWith("http://")) {
            connection = getHttpURLConnection(url);
        } else if (url.startsWith("https://")) {
            connection = getHttpsURLConnection(url);
        } else {
            log.warn("imageUrl: {} [filename:{}] convert to file(image) failed.", url, filePath);
            throw BaseException.of(BaseErrorCode.FILE_ADDRESS_NOT_EXIST.of());
        }

        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            log.warn("image file:{} has exists.", filePath);
        }
        // 保存图片到文件目录
        try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(imageFile.toPath()))) {
            // 缓冲字节数组
            byte[] buffer = new byte[IMAGE_URL_BUFFER_SIZE];

            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }

            return file2Base64Str(imageFile);
        } catch (Exception e) {
            log.error("imageUrl: {} [filename:{}] convert to file(image) failed, message:{}", url, filePath, e.getMessage(), e);
            if (e.getMessage().matches(NO_SPACE_LEFT_EN)) {
                throw BaseException.of(BaseErrorCode.FILE_CREATE_ERROR, "剩余空间不足，导出失败，请清理磁盘空间后重试");
            }
            return null;
        }
    }


    private static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.connect();
        return connection;
    }

    private static HttpsURLConnection getHttpsURLConnection(String url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());

        connection.setHostnameVerifier((s, sslSession) -> true);
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.connect();
        return connection;
    }

    private static void compressFile(File file) throws IOException {
        if (file.length() > 50 * 1024f) {
            File compressFile = new File(".compress");
            FileUtils.copyFile(file, compressFile);
            Thumbnails.of(compressFile).scale(1f).outputQuality(0.5f).toFile(file);
        }
    }
}
