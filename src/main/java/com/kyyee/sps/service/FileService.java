package com.kyyee.sps.service;

import com.kyyee.sps.dto.request.FileReqDto;
import com.kyyee.sps.dto.response.FileResDto;

public interface FileService {

    /**
     * 直接上传
     */
    FileResDto upload(FileReqDto resDto);

    /**
     * 分片上传
     */
    FileResDto sliceUpload(FileReqDto reqDto);

    /**
     * 删除图片
     */
    void delete(String filename);
}
