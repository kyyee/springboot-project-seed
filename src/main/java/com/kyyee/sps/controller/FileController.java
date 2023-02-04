package com.kyyee.sps.controller;

import com.kyyee.sps.common.component.validated.group.SliceUpload;
import com.kyyee.sps.common.component.validated.group.Upload;
import com.kyyee.sps.dto.request.FileReqDto;
import com.kyyee.sps.dto.response.FileResDto;
import com.kyyee.sps.service.FileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api-prefix}/files")
//@ApiVersion(1)
@Slf4j
@Tag(name = "文件管理")
@Validated
public class FileController {

    @Resource
    private FileService service;

    @PostMapping("/upload")
    public FileResDto upload(@Validated({Upload.class}) FileReqDto reqDto) {
        return service.upload(reqDto);
    }

    @PostMapping("/slice-upload")
    public FileResDto sliceUpload(@Validated({SliceUpload.class}) FileReqDto resDto) {
        return service.sliceUpload(resDto);
    }

    @DeleteMapping("{filename}")
    public void delete(@PathVariable(name = "filename", required = false) String filename) {
        service.delete(filename);
    }
}
