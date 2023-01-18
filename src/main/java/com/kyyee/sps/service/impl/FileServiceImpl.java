package com.kyyee.sps.service.impl;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.dto.request.FileReqDto;
import com.kyyee.sps.dto.response.FileResDto;
import com.kyyee.sps.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    // todo 区分操作系统语言
    Locale locale = Locale.getDefault();
    /**
     * No space left on device
     */
    public static final String NO_SPACE_LEFT_EN = "[\\s\\S]*No space left on device[\\s\\S]*";

    /**
     * 磁盘空间不足
     */
    public static final String NO_SPACE_LEFT_CN = "[\\s\\S]*磁盘空间不足[\\s\\S]*";

    @Value("${api-prefix:NA}")
    private String apiPrefix;

    @Value("${kyyee.file.save-dir:/home/file/}")
    private String saveDir;

    @Override
    public FileResDto upload(FileReqDto reqDto) {
        // 此处可以对数据库中的file md5进行比对，如存在则直接返回file uri
        MultipartFile multipartFile = reqDto.getFile();
        // 创建文件保存目录和文件压缩目录
        File file = copyMultipartFileToFile(multipartFile);
        verifyFileMd5(file, reqDto.getMd5());

        return FileResDto.builder().filename(multipartFile.getOriginalFilename())
            .uri(apiPrefix + "/fs/" + multipartFile.getOriginalFilename())
            .build();
    }

    /**
     * 创建文件目录
     *
     * @param dirPath 目录
     */
    public static void mkdir(String dirPath) {
        if (dirPath == null) {
            return;
        }

        File directory = new File(dirPath);
        if (directory.exists()) {
            return;
        }
        try {
            FileUtils.forceMkdir(directory);
        } catch (IOException e) {
            // ignored exception
            log.error("mkdir failed, message:{}:", e.getMessage());
            if (directory.getUsableSpace() <= 0) {
                throw BaseException.of(BaseErrorCode.FILE_CREATE_ERROR, "剩余空间不足，导出失败，请清理磁盘空间后重试");
            }
            if (e.getMessage().matches(NO_SPACE_LEFT_EN)) {
                throw BaseException.of(BaseErrorCode.FILE_CREATE_ERROR, "剩余空间不足，导出失败，请清理磁盘空间后重试");
            }
        }
    }

    /**
     * 将输入流转换为文件
     *
     * @param multipartFile
     * @return
     */
    public File copyMultipartFileToFile(MultipartFile multipartFile) {
        mkdir(saveDir);

        final String fileName = multipartFile.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)) {
            throw BaseException.of(BaseErrorCode.FILE_READ_ERROR.of(), "文件转换异常");
        }
        File file = new File(saveDir.concat(multipartFile.getOriginalFilename()));
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("文件存储到本地失败，{}", e.getMessage());
            throw BaseException.of(BaseErrorCode.FILE_UPLOAD_ERROR.of(), "文件转换异常");
        }
        return file;
    }

    @Override
    public FileResDto sliceUpload(FileReqDto reqDto) {
        // 此处可以对数据库中的file md5进行比对，如存在则直接返回file uri
        mkdir(saveDir);

        File file = new File(saveDir, reqDto.getFilename());

        final String filename = file.getName();
        String baseName = FilenameUtils.getBaseName(filename);
        String fileType = FilenameUtils.getExtension(filename);

        MappedByteBuffer buffer = null;
        try (final RandomAccessFile tmpRaf = new RandomAccessFile(file, "rw");
             final FileChannel channel = tmpRaf.getChannel()) {
            final long offset = (reqDto.getSize()) * reqDto.getChunk();

            final byte[] bytes = reqDto.getFile().getBytes();
            buffer = channel.map(FileChannel.MapMode.READ_WRITE, offset, bytes.length);
            buffer.put(bytes);
        } catch (IOException e) {
            log.warn("upload slice file failed, message:{}", e.getMessage(), e);
            throw BaseException.of(BaseErrorCode.FILE_UPLOAD_ERROR.of(), "文件转换异常");
        } finally {
            unmap(buffer);
        }

        // chunk 从0开始
        if ((reqDto.getChunk() + 1) == reqDto.getChunks()) {
            verifyFileMd5(file, reqDto.getMd5());

            return FileResDto.builder().filename(filename)
                .uri(apiPrefix + "/fs/" + filename)
                .build();

        }

        return FileResDto.builder().filename(filename)
            .build();
    }

    private void unmap(MappedByteBuffer buffer) {
        if (buffer != null) {
            buffer.force();
//            final Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
//            if (cleaner != null) {
//                cleaner.clean();
//            }
        }
    }

    private void verifyFileMd5(File destFile, String md5) {
        //判断文件是否被更改
        try (FileInputStream fileInputStream = new FileInputStream(destFile)) {
            String realMd5 = org.springframework.util.DigestUtils.md5DigestAsHex(fileInputStream);
            if (!md5.equals(realMd5)) {
                throw BaseException.of(BaseErrorCode.FILE_UPLOAD_ERROR.of(), "文件md5不一致");
            }
        } catch (IOException e) {
            throw BaseException.of(BaseErrorCode.FILE_UPLOAD_ERROR.of(), "校验文件md5异常");
        }
    }

    /**
     * v1暂无此功能
     */
    public void delete(String filepath) {
        // 1.删除zip文件
        try {
            File file = new File(saveDir);
            if (!file.exists()) {
                return;
            }

//            File zipDirectory = new File(saveDir + "zip" + File.separator);
//            if (zipDirectory.exists()) {
//                Collection<File> directories = FileUtils.listFiles(zipDirectory, new PrefixFileFilter(filepath), null);
//                for (File directory : directories) {
//                    log.info("delete file:{}", directory.getName());
//                    FileUtils.deleteQuietly(directory);
//                }
//            }
            File directory = new File(saveDir + filepath);
            if (directory.exists()) {
                log.info("delete file:{}", directory.getName());
                if (directory.isDirectory()) {
                    FileUtils.deleteDirectory(directory);
                }
                if (directory.isFile()) {
                    FileUtils.deleteQuietly(directory);
                }
            }
        } catch (IOException e) {
            log.warn("delete filename:{} failed. message:{}", filepath, e.getMessage(), e);
        }
    }

}
