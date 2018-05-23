package ru.bpc.billing.service.io.scp;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ScpFrom {
    private final String address;
    private final String path;
    private final String login;
    private final String password;

    public ScpFrom(String address, String path, String login, String password) {
        this.address = address;
        this.path = path;
        this.login = login;
        this.password = password;
    }


    public File downloadFromRemoteScp(String remoteFileName) {

        FileOutputStream fos = null;
        try {

            String lfile = null;

            JSch jsch = new JSch();
            Session session = jsch.getSession(login, address, 22);

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

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + remoteFileName;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file = null;
                for (int i = 0; ; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
//                File fff = new File();
                fos = new FileOutputStream("");
                int foo;
                while (true) {
                    if (buf.length < filesize) foo = buf.length;
                    else foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) break;
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

            session.disconnect();

            System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) fos.close();
            } catch (Exception ee) {
            }
        }
        return null;
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


}