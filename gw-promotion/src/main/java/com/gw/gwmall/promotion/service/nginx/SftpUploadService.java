package com.gw.gwmall.promotion.service.nginx;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class SftpUploadService {

    /** 获取连接 */
    public ChannelSftp getChannel(String host,String userName,int port,String password) throws Exception{
        JSch jsch = new JSch();
        //->ssh root@host:port
        Session sshSession = jsch.getSession(userName,host,port);
        //密码
        sshSession.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        sshSession.connect();
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        log.info("已连接服务器：{}，准备上传....",host);
        return (ChannelSftp) channel;
    }

    /**
     * sftp上传文件
     * @param sftp
     * @param inputStream
     * @param fileName 服务器上存放的文件名
     */
    public void putFile(ChannelSftp sftp,InputStream inputStream, String path, String fileName){
        try {
            //上传文件
            log.info("准备上传{}.....",path + fileName);
            sftp.put(inputStream, path + fileName);
            log.info("上传{}成功！",path + fileName);

        } catch (Exception e) {
            log.error("上传{}失败：",path + fileName,e);
        }
    }

    /**
     * 创建目录
     */
    public static void createDir(String path,ChannelSftp sftp) throws SftpException {
        String[] folders = path.split("/");
        sftp.cd("/");
        for ( String folder : folders ) {
            if ( folder.length() > 0 ) {
                try {
                    sftp.cd( folder );
                }catch ( SftpException e ) {
                    sftp.mkdir( folder );
                    sftp.cd( folder );
                }
            }
        }
    }

}
