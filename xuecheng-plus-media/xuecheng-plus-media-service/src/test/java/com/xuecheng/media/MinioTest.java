package com.xuecheng.media;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

public class MinioTest {

  MinioClient minioClient = MinioClient.builder().endpoint("http://localhost:9000")
      .credentials("minioadmin", "minioadmin").build();

  @Test
  public void test_upload()
      throws Exception {

    UploadObjectArgs testbucket = UploadObjectArgs.builder().bucket("testbucket")
        .filename("C:\\project\\apex.png").object("apex.png")
        .build();

    minioClient.uploadObject(testbucket);
  }

  @Test
  public void test_get() throws Exception {

    GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("apex.png")
        .build();
    FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
    FileOutputStream outputStream = new FileOutputStream(
        new File("C:\\minio\\upload\\apex1.png"));
    IOUtils.copy(inputStream, outputStream);

    // 校验md5值
    String source_md5 = DigestUtils.md5Hex(inputStream);
    String local_md5 = DigestUtils.md5Hex(new FileInputStream("C:\\minio\\upload\\apex1.png"));
    if (source_md5.equals(local_md5)) {
      System.out.println("下载成功");
    }
  }
}
