package com.geekbrains.chat;

import com.geekbrains.command.Command;
import com.geekbrains.command.CommandType;
import com.geekbrains.command.commands.AuthCommandData;
import com.geekbrains.command.commands.PrivateMessageCommandData;
import com.geekbrains.command.commands.PublicMessageCommandData;


import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ClientHandler {

    private MyServer server;
    private final Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String userName;

    public ClientHandler(MyServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        new Thread(() -> {
            try {
                authenticate();
                //timerAuthenticate();
                readMessages();
            } catch (IOException e) {
                System.err.println("Failed to process message from client");
                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
                }
            }
        }).start();
    }

    private void authenticate() throws IOException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },1*120*1000);

        while (true) {
            Command command = readCommand();

            if (command == null) {
                continue;
            }

            if (command.getType() == CommandType.AUTH) {
                AuthCommandData data = (AuthCommandData) command.getData();
                String login = data.getLogin();
                String password = data.getPassword();
                String userName = this.server.getAuthService().getUsernameByLoginAndPassword(login, password);
                if (userName == null) {
                    sendCommand(Command.errorCommand("������������ ��� ������������ ��� ������"));
                } else if (server.isUserNameBusy(userName)) {
                    sendCommand(Command.errorCommand("����� ������������ ��� ����������"));
                } else {
                    timer.cancel();
                    this.userName = userName;
                    sendCommand(Command.authOkCommand(userName));
                    server.subscribe(this);
                    return;
                }
            }
        }
    }

    private Command readCommand() throws IOException {
        Command command = null;

        try {
            command = (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to read Command class");
            e.printStackTrace();
        }

        return command;
    }

    private void readMessages() throws IOException {

        while (true) {
            Command command = readCommand();

            if (command == null) {
                continue;
            }
            switch (command.getType()) {
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String receiver = data.getReceiver();
                    String privateMessage = data.getMessage();
                    server.sendPrivateMessage(this, receiver, privateMessage);
                    break;
                }
                case PUBLIC_MESSAGE:
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    processMessage(data.getMessage());
                    break;
            }
        }
    }

    private void processMessage(String message) throws IOException {
        this.server.broadcastMessage(message, this);
    }

    public void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public void closeConnection() throws IOException {
        outputStream.close();
        inputStream.close();
        server.unsubscribe(this);
        clientSocket.close();
    }

    public String getUserName() {
        return userName;
    }

    /*public void timerAuthenticate () throws IOException {
         Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },1*5*1000);
    }*/
}
