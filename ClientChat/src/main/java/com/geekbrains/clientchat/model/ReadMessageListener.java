package com.geekbrains.clientchat.model;

import com.geekbrains.command.Command;

public interface ReadMessageListener {

    void processReceivedCommand(Command command);

}
