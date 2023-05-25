package com.xuecheng.media.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

  @Autowired
  MediaFileService mediaFileService;

  @ApiOperation("媒资列表查询接口")
  @PostMapping("/files")
  public PageResult<MediaFiles> list(PageParams pageParams,
      @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
    Long companyId = 1232141425L;
    return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
  }

  @ApiOperation("上传图片")
  @RequestMapping("/upload/coursefile")
  public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata)
      throws IOException {

    Long companyId = 1232141425L;
    UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
    uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
    uploadFileParamsDto.setFileSize(filedata.getSize());
    uploadFileParamsDto.setFileType("001001");
    // 创建一个临时文件
    File tempFile = File.createTempFile("minio", ".temp");
    filedata.transferTo(tempFile);
    // 文件路径
    String localFilePath = tempFile.getAbsolutePath();

    UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId,
        uploadFileParamsDto, localFilePath);

    return uploadFileResultDto;
  }
}
