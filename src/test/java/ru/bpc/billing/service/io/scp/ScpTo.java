package ru.bpc.billing.service.io.scp;

import com.jcraft.jsch.*;

import java.awt.*;
import javax.swing.*;
import java.io.*;

public class ScpTo {
    private final String address;
    private final String path;
    private final String login;
    private final String password;

    public ScpTo(String address, String path, String login, String password) {
        this.address = address;

        if (!path.endsWith(File.pathSeparator))
            this.path = path + File.pathSeparator;
        else
            this.path = path;

        this.login = login;
        this.password = password;
    }

    public void uploadToRemote(File localFile) {


        FileInputStream fis = null;
        try {

            String remoteFileName = localFile.getName();

            JSch jsch = new JSch();
            Session session = jsch.getSession(login, address, 22);

            // username and password will be given via UserInfo interface.
            UserInfo ui = new UserInfo() {
                public String getPassphrase() {
                    return null;
                }

                public String getPassword() {
                    return password;
                }

                public boolean promptPassword(String message) {
                    return true;
                }

                public boolean promptPassphrase(String message) {
                    return false;
                }

                public boolean promptYesNo(String message) {
                    return true;
                }

                public void showMessage(String message) {
                }
            };
            session.setUserInfo(ui);
            session.connect();

            boolean ptimestamp = true;

            // exec 'scp -t remoteFileName' remotely
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + path + remoteFileName;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if (checkAck(in) != 0) {
                exit();

            }


            if (ptimestamp) {
                command = "T " + (localFile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command += (" " + (localFile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    exit();

                }
            }

            // send "C0644 filesize filename", where filename should not include '/'
            command = "C0644 " + localFile.length() + " ";
//            if (localFileName.lastIndexOf('/') > 0) {
//                command += localFileName.substring(localFileName.lastIndexOf('/') + 1);
//            } else {
            command += localFile.getName();
//            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                exit();

            }

            // send a content of localFileName
            fis = new FileInputStream(localFile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) break;
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                exit();
            }
            out.close();

            channel.disconnect();
            session.disconnect();

            exit();
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fis != null) fis.close();
            } catch (Exception ee) {
            }
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    private void exit() {
        System.out.println("EXIT111111111111111111");
    }


}