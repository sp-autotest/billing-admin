package ru.bpc.billing.service.io.scp;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import javax.swing.*;
import java.io.*;

@Slf4j
public class ScpFromTest {
    public static void main(String[] arg) {
//        if (arg.length != 2) {
//            System.err.println("usage: java ScpFrom user@remotehost:file1 file2");
//            System.exit(-1);
//        }

        FileOutputStream fos = null;
        try {

            String user = "user1";
            String host = "localhost";
            String rfile = "CrushFTP8_PC.zip";
            String lfile = "c:\\java\\tools\\out2.zip";


            JSch jsch = new JSch();
            JSch.setLogger(new Logger() {
                public boolean isEnabled(int i) {
                    return true;
                }

                public void log(int i, String s) {
                    log.debug("SCP Log(jsch," + i + "): " + s);
                }
            });

            Session session = jsch.getSession(user, host, 22);

            // username and password will be given via UserInfo interface.
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);
            session.connect(10 * 1000);

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + rfile;
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
                    out.close();
                    in.close();
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
                fos = new FileOutputStream(lfile);
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


            Thread.sleep(100);
            channel.disconnect();

//            session.disconnect();

            System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) fos.close();
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


    private static class MyUserInfo implements UserInfo {
        public String getPassphrase() {
            return null;
        }

        public String getPassword() {
            return "Qwerty1";
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
    }

}