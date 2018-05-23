package ru.bpc.billing.service.io;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.UUID;

@Slf4j
public class SCPService {

    private final Integer scpPort;

    private final String scpHost;
    private final String scpUsername;
    private final String scpPassword;
    private final String path;

    public SCPService(String address, Integer port, String path, String login, String password) {
        this.scpHost = address;
        this.scpPort = port;
        this.path = path;

        this.scpUsername = login;
        this.scpPassword = password;
    }


    private byte[] fileBytes(File file) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            byte fileContent[] = new byte[(int) file.length()];
            fin.read(fileContent);
            return fileContent;
        } catch (IOException ioe) {
            log.error("Exception while reading file " + ioe);
        } finally {
            // close the streams using close method
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ioe) {
                log.error("Error while closing stream: " + ioe);
            }
        }
        return null;
    }

    public void sendFile(File file) {
        try {

            Session session = getScpSession();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("scp -t " + path);
            OutputStream os = channel.getOutputStream();
            channel.connect();
            os.write(("C7777 " + file.length() + " " + file.getName() + "\n").getBytes());
            os.flush();
            os.write(fileBytes(file));
            os.flush();
            os.write(0);
            os.flush();
            Thread.sleep(100);
            channel.disconnect();
        } catch (Exception e) {
            log.error("Error sending posting file to SCP", e);
        }

    }

    public File getFile(String rfile) {
        FileOutputStream fos = null;
        File lfile = new File(UUID.randomUUID().toString() + "_test.posting");

        try {
            Session session = getScpSession();
            // exec 'scp -f rfile' remotely
            String command = "scp -f " + path + rfile;
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
                    return lfile;
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }


            Thread.sleep(100);
            channel.disconnect();
        } catch (Exception e) {
            log.error("", e);
            try {
                if (fos != null) fos.close();
            } catch (Exception ignore) {
            }
        }
        return lfile;
    }

    private static int checkAck(InputStream in) throws IOException {
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
                log.error(sb.toString());
            }
            if (b == 2) { // fatal error
                log.error(sb.toString());
            }
        }
        return b;
    }


    private Session getScpSession() throws JSchException {
        JSch sch = new JSch();
        JSch.setLogger(new JscLogger());
        Session session = sch.getSession(scpUsername, scpHost, scpPort);
        session.setUserInfo(new JscUserInfo());
        session.connect();
        return session;
    }

    private class JscUserInfo implements UserInfo {
        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return scpPassword;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return false;
        }

        @Override
        public boolean promptYesNo(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {

        }
    }

    private class JscLogger implements Logger {

        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String message) {
            String levelString = "";
            switch (level) {
                case Logger.DEBUG:
                    levelString = "ERROR";
                    break;
                case Logger.INFO:
                    levelString = "INFO";
                    break;
                case Logger.WARN:
                    levelString = "WARN";
                    break;
                case Logger.ERROR:
                    levelString = "ERROR";
                    break;
                case Logger.FATAL:
                    levelString = "FATAL";
                    break;
            }
            log.debug("SCP client log(jsch," + levelString + "): " + message);
        }
    }
}
