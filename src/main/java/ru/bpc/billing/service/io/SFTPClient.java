package ru.bpc.billing.service.io;

import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SFTPClient {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Session session;
    private final ChannelSftp sftpChannel;


    public SFTPClient(String sftpUsername, String sftpPassword, String sftpHost, int sftpPort) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(sftpUsername, sftpHost, sftpPort);
        session.setPassword(sftpPassword);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();

        Channel channel = session.openChannel("sftp");
        channel.connect();

        sftpChannel = (ChannelSftp) channel;
    }

    public List<String> getFileNames(String path) throws SFTPClientException {
        List<String> fileNames = new ArrayList<>();
        try {
            Vector<ChannelSftp.LsEntry> v = sftpChannel.ls(path);
            for (ChannelSftp.LsEntry e : v) {
                String remoteFileName = e.getFilename();
                if (remoteFileName.equals(".") || remoteFileName.equals(".."))
                    continue;
                fileNames.add(remoteFileName);
            }
        } catch (SftpException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    public List<File> getFiles(List<String> fileNames, String path, String localPath) throws SFTPClientException {
        try {
            List<File> fileList = new ArrayList<>();

            for (String remoteFileName : fileNames) {
                InputStream is = sftpChannel.get(path + remoteFileName);
                File file = new File(localPath + remoteFileName);
                fileList.add(file);
                OutputStream outputStream = new FileOutputStream(file);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();
                outputStream.close();
            }

            return fileList;
        } catch (Exception e) {
            throw new SFTPClientException(e);
        }

    }

    public void close() {
        sftpChannel.exit();
        session.disconnect();
    }
}
