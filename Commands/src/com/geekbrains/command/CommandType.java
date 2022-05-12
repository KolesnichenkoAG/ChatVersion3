package com.geekbrains.command;

import java.io.Serializable;

public enum CommandType implements Serializable {
    AUTH,
    PRIVATE_MESSAGE,
    PUBLIC_MESSAGE,
    ERROR,
    CLIENT_MESSAGE,
    AUTH_OK,
    UPDATE_USERS_LIST

}
