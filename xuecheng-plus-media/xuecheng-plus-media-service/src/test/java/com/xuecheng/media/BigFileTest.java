package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        // 源文件
        File source = new File("E:\\testChunk\\test.mp4");
        // 分块文件路径
        String chunkFilePath = "E:\\testChunk\\chunk\\";
        //分块文件大小 1M
        int chunkSize = 1024 * 1024 * 5;
        //分块文件数量
        int chunkNum = (int) Math.ceil(source.length() * 1.0 / chunkSize);

        RandomAccessFile r = new RandomAccessFile(source, "r");
        byte[] bytes = new byte[1024];
        for (int i = 0; i < chunkNum; i++) {
            File chunkFile = new File(chunkFilePath + i);
            RandomAccessFile rw = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = r.read(bytes)) != -1) {
                rw.write(bytes, 0, len);
                if (chunkFile.length() >= chunkSize) {
                    break;
                }
            }
        }
    }

    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("E:\\testChunk\\chunk\\");
        //原始文件
        File sourceFile = new File("E:\\testChunk\\test.mp4");
        //合并文件
        File mergeFile = new File("E:\\testChunk\\test_02.mp4");

        //取出所有分块文件
        File[] files = chunkFolder.listFiles();
        //将数组转成list
        List<File> filesList = Arrays.asList(files);
        //对分块文件排序
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        //缓存区
        byte[] bytes = new byte[1024];
        //遍历分块文件，向合并 的文件写
        for (File file : filesList) {
            //读分块的流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1) {
                raf_rw.write(bytes, 0, len);
            }
            raf_r.close();
        }
        raf_rw.close();
        //合并文件完成后对合并的文件md5校验
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_source = new FileInputStream(sourceFile);
        String md5_merge = DigestUtils.md5Hex(fileInputStream_merge);
        String md5_source = DigestUtils.md5Hex(fileInputStream_source);
        if (md5_merge.equals(md5_source)) {
            System.out.println("文件合并成功");
        }
    }
}