package com.geekbrains.timer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFinish {
    private static long timerStart = 0;
    private static long timerFinish = 0;
    private static long timerDuration = 50;

    public static void main(String[] args) throws IOException {
        timer3();
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
             @Override
             public void run() {
                 System.out.println("счетчик запущен");
             }
         },1*10*1000);

    }

    public static void timer () throws IOException {
        if (timerStart == 0) {
            timerStart = System.currentTimeMillis();
            timerFinish = timerStart + timerDuration;
            System.out.println("счетчик запущен");
        }
        if(timerFinish <= System.currentTimeMillis()){
            System.out.println("время вышло");
        }
    }
    public static void timer3() {
        long startTime = System.currentTimeMillis();
        long expectationTime = 7;
        long finishTime = startTime + expectationTime;
        if (finishTime <= System.currentTimeMillis()) {
            System.out.println("время вышло");
        }
    }

}
