package com.kalix.exam.manage.export.utils;

import com.kalix.framework.core.util.ConfigUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FtpUtils {
    private static final String examFtpConfigName = "config.exam.ftp";
    private static String FTP_HOSTNAME = null;
    private static String FTP_PORT = null;
    private static String FTP_USERNAME = null;
    private static String FTP_PASSWORD = null;
    private static String FTP_BASEPATH = null;

    /**
     * Ftp登录连接
     * @return
     */
    public static FTPClient connect() {
        // String hostname, Integer port, String username, String password
        initConfig();
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            System.out.println("ftp服务器:" + FTP_HOSTNAME + ":" + FTP_PORT);
            Integer ftpPort = (FTP_PORT == null || "".equals(FTP_PORT))?21:Integer.parseInt(FTP_PORT.trim());
            ftpClient.connect(FTP_HOSTNAME, ftpPort); //连接ftp服务器
            ftpClient.login(FTP_USERNAME, FTP_PASSWORD); //登录ftp服务器
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 验证是否连接成功
     * @param ftpClient
     * @return
     */
    public static Boolean checkConnected(FTPClient ftpClient) {
        if (ftpClient == null) {
            return false;
        }
        int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            return false;
        }
        return true;
    }

    public static List<String> getFtpFileNames(FTPClient ftpClient,String path) {
        if (ftpClient == null) {
            return null;
        }
        try {
            String[] fileNames = ftpClient.listNames(path);
            return Arrays.asList(fileNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getFtpFileNames() {
        List<String> ftpFileNames = null;
        FTPClient ftpClient = null;
        try {
            // ftpClient = connect("172.18.135.183", 21, "stu", "123");
            ftpClient = connect();
            Boolean isConnected = checkConnected(ftpClient);
            if (isConnected) {
                ftpFileNames = getFtpFileNames(ftpClient, FTP_BASEPATH);
            }
            return ftpFileNames;
        } finally {
            disConnect(ftpClient);
        }
    }

    /**
     * 断开Ftp连接
     * @param ftpClient
     */
    public static void disConnect(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }
        try {
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void initConfig() {
        FTP_HOSTNAME = (String) ConfigUtil.getConfigProp("FTP_HOSTNAME", examFtpConfigName);
        FTP_PORT = (String) ConfigUtil.getConfigProp("FTP_PORT", examFtpConfigName);
        FTP_USERNAME = (String) ConfigUtil.getConfigProp("FTP_USERNAME", examFtpConfigName);
        FTP_PASSWORD = (String) ConfigUtil.getConfigProp("FTP_PASSWORD", examFtpConfigName);
        FTP_BASEPATH = (String) ConfigUtil.getConfigProp("FTP_BASEPATH", examFtpConfigName);
    }
}
