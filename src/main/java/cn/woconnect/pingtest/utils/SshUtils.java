package cn.woconnect.pingtest.utils;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author wjzhang
 * @date 2019/11/6  16:45
 */
public class SshUtils {

    // 初始2口启动
    private static boolean isChange = false;
    private static Logger logger = LoggerFactory.getLogger(SshUtils.class);

    /**
     * 执行单条命令
     * @param username ssh用户
     * @param host ip
     * @param port 端口
     * @param password 密码
     * @param command  命令
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String exeCommand(String host, int port, String username, String password, String command) throws JSchException, IOException {

        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");

        session.setPassword(password);
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String out = IOUtils.toString(in, "UTF-8");

        channelExec.disconnect();
        session.disconnect();

        return out;
    }


    /**
     * 执行多条命令
     * @param username ssh用户
     * @param host ip
     * @param port 端口
     * @param password 密码
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public static String execCommandByShell(String username, String host, int port, String password)throws IOException,JSchException {

        // 连接ssh
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");

        session.setPassword(password);
        session.connect();

        String result = "";

        //2.尝试解决 远程ssh只能执行一句命令的情况
        ChannelShell channelShell = (ChannelShell) session.openChannel("shell");
        InputStream inputStream = channelShell.getInputStream();//从远端到达的数据  都能从这个流读取到
        channelShell.setPty(true);
        channelShell.connect();

        OutputStream outputStream = channelShell.getOutputStream();//写入该流的数据  都将发送到远程端
        //使用PrintWriter 就是为了使用println 这个方法
        //好处就是不需要每次手动给字符加\n
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println("sys");
        if (isChange){
            printWriter.println("int g1/0/2");
            isChange = false;
        }else {
            printWriter.println("int g1/0/3");
            isChange = true;
        }

        printWriter.println("undo shutdown");
        printWriter.println("commit");
        printWriter.println("q");//退出到主界面
        printWriter.println("q");//退出管理
        printWriter.println("q");//为了结束本次交互
//        printWriter.println("exit");//为了结束本次交互
        printWriter.flush();//把缓冲区的数据强行输出

        /**
         shell管道本身就是交互模式的。要想停止，有两种方式：
         一、人为的发送一个exit命令，告诉程序本次交互结束
         二、使用字节流中的available方法，来获取数据的总大小，然后循环去读。
         为了避免阻塞
         */
        byte[] tmp = new byte[1024];
        while (true) {

            while (inputStream.available() > 0) {
                int i = inputStream.read(tmp, 0, 1024);
                if (i < 0) break;
                String s = new String(tmp, 0, i);
                if (s.indexOf("--More--") >= 0) {
                    outputStream.write((" ").getBytes());
                    outputStream.flush();
                }
                logger.info(s);
            }
            if (channelShell.isClosed()) {
                logger.info("exit-status:" + channelShell.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }

        }
        outputStream.close();
        inputStream.close();
        channelShell.disconnect();
        session.disconnect();
        logger.info("完成");

        return result;
    }






}
