package ru.bpc.billing.service.automate.bsp;

public interface AutomateBspTask extends Runnable {
    void run();
    boolean isRunning();
}
