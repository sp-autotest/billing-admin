package ru.bpc.billing.service.automate.bsp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyAutomateTask implements AutomateBspTask {

    private volatile boolean isRunning = false;

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            doit();
        } finally {
            isRunning = false;
        }
    }

    private void doit() {
        log.info("!!!STARTED!!!");


            try {
                Thread.sleep(20 * 1000L);
            } catch (InterruptedException e) {
                log.error("Interrupted sleeping", e);
            }


        log.info("!!!FINISHED!!!");
    }

}
