/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicsdemo_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import simulation.Simulation;

/**
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable {
    
   @FXML
    private TextArea textArea;
   
   private int ClientNo = 0;
   private Simulation sim;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sim = new Simulation(300, 250, 2, 2);
        new Thread(() -> {
            try{
                ServerSocket serverSocket = new ServerSocket(8000);
                while(true){
                    Socket socket = serverSocket.accept();
                    ClientNo ++;
                    if(ClientNo > 2){
                        serverSocket.close();
                        ClientNo --;
                    }
                    Platform.runLater( () -> {
                        // Display the client number
                        textArea.appendText("Starting thread for client " + ClientNo +
                          " at " + new Date() + '\n');
                    });
                    new Thread(new HandleClient(socket, sim, textArea));
                }
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }).start();
    }    
    
}

class HandleClient implements Runnable, Game.GameConstants{
    private Socket socket;
    private Simulation sim;
    private TextArea textArea;
    private String handle;
    private int control = 0;
    
    public HandleClient(Socket socket,Simulation sim,TextArea textArea){
        this.socket = socket;
        this.sim = sim;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try{
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputToClient = new PrintWriter(socket.getOutputStream());
            while(true){
                int request = Integer.parseInt(inputFromClient.readLine());
                 switch(request) {
              case SEND_HANDLE: {
                  handle = inputFromClient.readLine();
                  break;
              }
              case SEND_CONTROL: {
                  int control = Integer.parseInt(inputFromClient.readLine());
                  break;
              }
              case GET_CONTROL: {
                  outputToClient.println(control);
                  outputToClient.flush();
                  control = 0;
                  break;
              }
              
          }
            }
            
        }catch(IOException ex){
            Platform.runLater(()->textArea.appendText("Exception in client thread: "+ex.toString()+"\n"));
        }
        
        
    }
}