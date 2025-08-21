package com.easypan.entity.dto;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author VectorX
 * @version V1.0
 * @description
 * @date 2024-07-17 23:04:31
 */

public class CreateImageCode
{

    private Integer width = 160;

    private Integer height = 40;

    private Integer codeCount = 4;

    private Integer lineCount = 20;

    private String code = null;

    private final Random random = new Random();

    private BufferedImage buffImg;

    public CreateImageCode() {
        createImage();
    }

    public CreateImageCode(Integer width, Integer height) {
        this.width = width;
        this.height = height;
        createImage();
    }

    public CreateImageCode(Integer width, Integer height, Integer codeCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        createImage();
    }

    public CreateImageCode(Integer width, Integer height, Integer codeCount, Integer lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        createImage();
    }

    //生成图片
    private void createImage() {
        //字体宽度
        int fontWidth = width / codeCount;

        //字体高度
        int fontHeight = height - 5;
        int codeY = height - 8;

        //图像buffer
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImg.getGraphics();
        //设置背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);

        //设置字体
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        g.setFont(font);

        //设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }

        //添加噪点
        //噪声率
        float yawpRate = 0.01f;
        int area = (int) (yawpRate + width + height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            buffImg.setRGB(x, y, random.nextInt(255));
        }

        //获取随机字符串
        String str1 = randomStr(codeCount);
        this.code = str1;
        for (int i = 0; i < codeCount; i++) {
            String strRand = str1.substring(i, i + 1);
            g.setColor(getRandColor(1, 255));

            g.drawString(strRand, i * fontWidth + 3, codeY);
        }
    }

    /**
     * 得到一个n位数的随机数(不包括O，o，0)
     *
     * @param n
     * @return
     */
    private String randomStr(Integer n) {
        String str1 = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrstuvwxyz123456789";
        String str2 = "";
        int len = str1.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str2 = str2 + str1.charAt((int) r);
        }
        return str2;
    }

    //随机颜色
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }

        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public String getCode() {
        return code.toUpperCase();
    }

    public void write(OutputStream outputStream) throws IOException {
        ImageIO.write(buffImg, "png", outputStream);
        outputStream.close();
    }
}
