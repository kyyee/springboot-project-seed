package com.kyyee.sps.dto.request;

import com.kyyee.sps.common.component.validated.group.SliceUpload;
import com.kyyee.sps.common.component.validated.group.Upload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileReqDto {
    /**
     * 分片之后的文件
     */
    @NotNull(message = "文件不能为空", groups = {Upload.class, SliceUpload.class})
    private MultipartFile file;
    /**
     * 文件的id
     */
    @NotBlank(message = "id不能为空", groups = {Upload.class, SliceUpload.class})
    private String id;
    /**
     * 整体文件的MD5值
     */
    @NotBlank(message = "文件名不能为空", groups = {Upload.class, SliceUpload.class})
    private String filename;
    /**
     * 当前文件是第几片，从0开始
     */
    @NotNull(message = "当前片数不能为空，从0开始", groups = {SliceUpload.class})
    private Integer chunk;
    /**
     * 文件一共分成了几片
     */
    @NotNull(message = "分片总数不能为空", groups = {SliceUpload.class})
    private Integer chunks;
    /**
     * 分片大小，单位为M，默认为10M
     */
    private Long size = 10L * 1024 * 1024;
    /**
     * 整体文件的MD5值
     */
    @NotBlank(message = "md5不能为空", groups = {Upload.class, SliceUpload.class})
    private String md5;

}
