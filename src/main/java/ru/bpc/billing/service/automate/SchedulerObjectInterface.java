package ru.bpc.billing.service.automate;

public interface SchedulerObjectInterface {
    void start();
    void stop();
    void restart();
}