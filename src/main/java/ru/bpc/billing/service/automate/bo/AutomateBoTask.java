package ru.bpc.billing.service.automate.bo;

public interface AutomateBoTask extends Runnable {
    void run();
    boolean isRunning();
}
