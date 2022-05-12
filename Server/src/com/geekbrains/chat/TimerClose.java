package com.geekbrains.chat;

import java.util.Timer;
import java.util.TimerTask;
import com.geekbrains.chat.ClientHandler;

public class TimerClose {
    Timer timer;

    public TimerClose(int seconds) {
        timer = new Timer();
        timer.schedule(new StopTask(), seconds * 1000);
    }

    public static void main(String[] args) {
        new TimerClose(10);
        System.out.println("StopWatch Started.");
    }

    class StopTask extends TimerTask {
        public void run() {

            System.out.println("Time Up!");
            timer.cancel();
        }
    }

}
