package com.easypan.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author VectorX
 * @version V1.0
 * @description 缩略图实用程序
 * @date 2024-07-29 21:32:52
 */
@Slf4j
public class ThumbnailUtils
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThumbnailUtils.class);

    /**
     * 生成图片缩略图
     *
     * @param sourceFile     源文件
     * @param thumbnailWidth 宽度
     * @param targetFile     目标文件
     * @param delSource      是否删除源文件
     * @return 是否成功
     */
    public static Boolean generatePictureThumbnail(File sourceFile, int thumbnailWidth, File targetFile, Boolean delSource) {
        try {
            // 读取图片
            BufferedImage src = ImageIO.read(sourceFile);

            // 源图片宽度
            int srcWidth = src.getWidth();

            // 小于指定高宽不压缩
            if (srcWidth <= thumbnailWidth) {
                return false;
            }

            // 压缩图片
            compressImage(sourceFile, thumbnailWidth, targetFile, delSource);
            return true;
        }
        catch (Exception e) {
            log.error("压缩图片失败：{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 压缩图片
     *
     * @param sourceFile 源文件
     * @param width      宽度
     * @param targetFile 目标文件
     * @param delSource  是否删除源文件
     */
    private static void compressImage(File sourceFile, Integer width, File targetFile, Boolean delSource) {
        try {
            final StringBuilder cmd = new StringBuilder("ffmpeg ")
                    // -i：输入文件，可以是任何媒体文件（视频、音频等）。
                    .append("-i %s ")
                    // -y：如果输出文件已经存在，FFmpeg 将自动覆盖它而不提示用户。这可以避免交互式确认。
                    .append("-y ")
                    // -vf：表示使用视频过滤器（video filter）。
                    // scale=%d:-1：是用来缩放图像的过滤器。%d是占位符，表示将被替换为目标宽度，用于控制输出图像的尺寸。
                    .append("-vf scale=%d:-1 ")
                    // 占位符，用于指定输出文件的名称和路径。
                    .append("%s");
            ProcessUtils.executeCommand(String.format(cmd.toString(), sourceFile.getAbsoluteFile(), width, targetFile.getAbsoluteFile()), false);
            if (delSource) {
                FileUtils.forceDelete(sourceFile);
            }
        }
        catch (Exception e) {
            log.error("压缩图片失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 生成视频缩略图
     *
     * @param sourceFile 源文件
     * @param width      宽度
     * @param targetFile 目标文件
     */
    public static void generateVideoThumbnail(File sourceFile, Integer width, File targetFile) {
        try {
            // 从视频文件中提取一帧并进行缩放操作的典型命令，适用于图像处理和视频制作等工作，可以自定义输入、输出路径及格式等。
            final StringBuilder cmd = new StringBuilder("ffmpeg ")
                    // -i：输入文件，可以是任何媒体文件（视频、音频等）。
                    .append("-i %s ")
                    // -y：如果输出文件已经存在，FFmpeg 将自动覆盖它而不提示用户。这可以避免交互式确认。
                    .append("-y ")
                    // -vframes 1：指定 FFmpeg 只输出一帧（frame）。可以用于提取视频中的单个画面。
                    .append("-vframes 1 ")
                    // -vf：表示使用视频过滤器（video filter）。
                    // scale=%d:%d/a：是用来缩放图像的过滤器。%d:%d：是两个占位符，分别将被替换为目标宽度和高度的数值，用于控制输出图像的尺寸。/a 的用法在此上下文中可能说明了一个特定的文件或目录结构，或者是某个参数，不同的用户或环境可能有不同的解释。
                    .append("-vf scale=%d:%d/a ")
                    // 占位符，用于指定输出文件的名称和路径。
                    .append("%s");
            ProcessUtils.executeCommand(String.format(cmd.toString(), sourceFile.getAbsoluteFile(), width, width, targetFile.getAbsoluteFile()), false);
        }
        catch (Exception e) {
            log.error("生成视频封面失败：{}", e.getMessage(), e);
        }
    }
}
