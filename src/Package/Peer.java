package Package;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Socket;
import javax.json.Json;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author G3
 */
public class Peer {
    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ingrese el usuario y puerto: ");
        String[] setupValues = bufferedReader.readLine().split(" ");
        ServerThread serverThread = new ServerThread(setupValues[1]);
        serverThread.start();
        new Peer().updateListenToPeers(bufferedReader, setupValues[0], serverThread);
    }
    public void updateListenToPeers(BufferedReader bufferedReader, String usarname, ServerThread serverThread) throws Exception{
        System.out.println("Puerto: ");
        System.out.println("Punto apra recivir un mensaje de (s to skip): ");
        String input = bufferedReader.readLine();
        String[] inputValues = input.split(" ");
        if (!input.equals("s")) for (int i = 0; i < inputValues.length; i++){
            String[] address = inputValues[i].split(":");
            Socket socket = null;
            try{
                socket = new Socket(address[0], Integer.valueOf(address[1]));
                new PeerThread(socket).start();
            } catch (Exception e){
                if (socket != null) socket.close();
                else System.out.println("Invalida entrada.");
            }
        }
        communicate(bufferedReader, usarname, serverThread);
    }
    public void communicate(BufferedReader bufferedReader, String username, ServerThread serverThread){
            try{
                System.out.println(" puedes comunicarte ahora (e para salir, c para cambiar)");
                boolean flag = true;
                while(flag){
                    String message = bufferedReader.readLine();
                    if (message.equals("E")){
                        flag = false;
                        break;
                    } else if (message.equals("c")){
                        updateListenToPeers(bufferedReader, username, serverThread);
                    } else {
                        StringWriter stringWriter = new StringWriter();
                        Json.createWriter(stringWriter).writeObject(Json.createObjectBuilder().add("username", username).add("message", message).build());
                        serverThread.sendMessage(stringWriter.toString());
                    }
                }
                System.exit(0);
            } catch (Exception e){}
    }
}   