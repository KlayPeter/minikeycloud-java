package com.easypan.utils;

import com.easypan.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author VectorX
 * @version 1.0.0
 * @description 进程实用程序
 * @date 2024/07/29
 */
@Slf4j
public class ProcessUtils
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProcessUtils.class);

    public static String executeCommand(String cmd, Boolean outPrintLog) throws BusinessException {
        if (StringUtils.isEmpty(cmd)) {
            log.error("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
            return null;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(cmd);

            // 执行 ffmpeg 指令
            // 取出输出流和错误流的信息
            // 注意：必须要取出 ffmpeg 在执行命令过程中产生的输出信息，如果不取的话当输出流信息填满 jvm 存储输出留信息的缓冲区时，线程就会阻塞
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();

            // 等待 ffmpeg 命令执行完
            process.waitFor();

            // 获取执行结果字符串
            String result = errorStream.stringBuffer
                    .append(inputStream.stringBuffer)
                    .append("\n")
                    .toString();

            // 输出执行的命令信息
            if (outPrintLog) {
                log.info("执行命令:{}，已执行完毕,执行结果:{}", cmd, result);
            }
            else {
                log.info("执行命令:{}，已执行完毕", cmd);
            }
            return result;
        }
        catch (Exception e) {
            log.error("执行命令失败：{} ", e.getMessage(), e);
            throw new BusinessException("视频转换失败");
        }
        finally {
            if (null != process) {
                // 添加一个钩子，在程序退出前结束已有的 FFmpeg 进程
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }

    /**
     * 在程序退出前结束已有的FFmpeg进程
     */
    private static class ProcessKiller extends Thread
    {
        private Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    /**
     * 用于取出ffmpeg线程执行过程中产生的各种输出和错误流的信息
     */
    private static class PrintStream extends Thread
    {
        InputStream inputStream;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            if (null == inputStream) {
                return;
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            }
            catch (Exception e) {
                log.error("读取输入流出错了！错误信息：{}", e.getMessage(), e);
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    log.error("调用PrintStream读取输出流后，关闭流时出错！");
                }
            }
        }
    }
}
