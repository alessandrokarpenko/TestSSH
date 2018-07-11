package com;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Base {


    public static void main(String[] args) {

        //doShell();
        doExec();

    }

    public static void doShell(){
        String user = "ubuntu";
        String password = "12345";
        String host = "192.168.151.199";
        int port=22;

        try{
            JSch jsch=new JSch();

            Session session=jsch.getSession(user, host, 22);

            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.setConfig("PreferredAuthentications",
                    "publickey,keyboard-interactive,password");
            session.connect(30000);   // making a connection with timeout.

            Channel channel=session.openChannel("shell");

            channel.setInputStream(System.in,false);
            channel.setOutputStream(System.out);
            channel.connect(3*1000);

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void doExec(){
        String user = "ubuntu";
        String password = "12345";
        String host = "192.168.151.199";
        int port = 22;

        try {
            JSch jsch = new JSch();


            Session session = jsch.getSession(user, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "password");
            session.setPassword(password);
            session.connect(30000);

            doCommands(session,"ls; cd Documents; ./test2 AA");

            session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void doCommands(Session session, String command) {

        try {
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.setInputStream(null);

            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
        } catch (Exception e) {

        }

    }

}
