

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import java.awt.*;

public class Server{
    private HashMap<String, DataOutputStream> client;
    private ServerSocket serverSocket;

    public static void main(String args[]) {
        new Server().start();
    }

    public Server() {
        client = new HashMap<String, DataOutputStream>();

        Collections.synchronizedMap(client);
    }

    public void start() {

        try {
            Socket socket;
            serverSocket = new ServerSocket(8000);

            System.out.println("Server on");

            while(true) {
                socket = serverSocket.accept();
                ServerReceiver receiver = new ServerReceiver(socket);
                receiver.start();
            }

        }

        catch(IOException e) {
            e.printStackTrace();
        }

    }

    class ServerReceiver extends Thread{
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        public ServerReceiver(Socket socket) {
            this.socket = socket;

            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }

            catch(IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {
            String name = "";
            String data;

            try {
                name = in.readUTF();

                send("[" + name + "] connected.");
                client.put(name, out);

                while(in != null) {
                    send(in.readUTF());
                }

            }

            catch(IOException e) {

            }

            finally {
                client.remove(name);
                send("[" + name + "] disconnected.");
            }

        }

        public void send(String data) {
            Iterator<String> it = client.keySet().iterator();

            while(it.hasNext()) {

                try {
                    DataOutputStream dos = client.get(it.next());
                    dos.writeUTF(data);
                }

                catch(Exception e) {

                }

            }

        }

    }

}
