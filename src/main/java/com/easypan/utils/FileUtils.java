package com.easypan.utils;

import com.easypan.entity.constants.VerificationCodeConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author VectorX
 * @version V1.0
 * @description 文件实用程序
 * @date 2024-07-28 15:13:20
 */
@Slf4j
public class FileUtils
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileUtils.class);
    /**
     * 重命名
     *
     * @param fileName 文件名
     * @return {@link String }
     */
    public static String rename(final String fileName) {
        return getFileNameNoSuffix(fileName)
                .concat("_")
                .concat(RandomUtils.getRandomString(VerificationCodeConstants.LENGTH_CODE_5))
                .concat(getFileSuffix(fileName));
    }

    /**
     * 获取无后缀文件名
     *
     * @param fileName 文件名
     * @return {@link String }
     */
    public static String getFileNameNoSuffix(final String fileName) {
        final int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * 获取文件后缀
     *
     * @param fileName 文件名
     * @return {@link String }
     */
    public static String getFileSuffix(final String fileName) {
        final int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index);
    }

    /**
     * 路径是否正常
     *
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean pathIsOk(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return true;
        }
        if (filePath.contains("../") || filePath.contains("..\\")) {
            return false;
        }
        return true;
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     */
    public static void deleteDirectory(String dirPath) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new File(dirPath));
        }
        catch (IOException e) {
            log.error("删除临时目录失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 强制删除
     *
     * @param file 文件
     * @throws IOException ioException
     */
    public static void forceDelete(File file) throws IOException {
        org.apache.commons.io.FileUtils.forceDelete(file);
    }

    /**
     * 复制文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @throws IOException ioException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        org.apache.commons.io.FileUtils.copyFile(sourceFile, targetFile);
    }

    /**
     * 文件不能存在
     *
     * @param realFilePath 真实文件路径
     * @return boolean
     */
    public static boolean notExists(String realFilePath) {
        return !new File(realFilePath).exists();
    }
}
