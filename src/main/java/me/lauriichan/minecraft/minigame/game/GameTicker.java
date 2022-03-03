package me.lauriichan.minecraft.minigame.game;

import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GameTicker {

    private static final int FAILED_CONSOLE_SPAM = 3;
    private static final long SECOND_IN_MILI = TimeUnit.SECONDS.toMillis(1);

    private static enum TickState {
        RUNNING,
        PAUSED,
        STOPPED;
    }

    private final Thread tickThread;
    private TickState state = null;

    private float executed = 0;
    private float tps = 0;
    private int targetTps = 20;

    private long tickLength;
    private long nanoTickLength;
    private long hundrethNanoTickLength;

    private long nanoTime = 0L;
    private long nanoTmpTime = 0L;

    private long miliTime = 0L;
    private long miliNewTime = 0L;

    private long delta = 0L;

    private int failed = FAILED_CONSOLE_SPAM;

    private final LongConsumer tick;
    private final Logger logger;

    public GameTicker(final Logger logger, final LongConsumer tick) {
        this.tickThread = new Thread(this::tick);
        tickThread.setName("GameTick");
        tickThread.setDaemon(true);
        this.logger = logger;
        this.tick = tick;
    }

    private void tick() {
        nanoTime = System.nanoTime();
        nanoTmpTime = nanoTime + nanoTickLength;
        miliTime = System.currentTimeMillis();
        miliNewTime = miliTime + SECOND_IN_MILI;
        while (state != TickState.STOPPED) {
            try {
                while (state == TickState.RUNNING) {
                    delta = miliTime;
                    if ((miliTime = System.currentTimeMillis()) > miliNewTime) {
                        miliNewTime = miliTime + SECOND_IN_MILI;
                        tps = (executed / targetTps);
                        executed = 0;
                    }
                    delta = miliTime - delta;
                    nanoTmpTime = nanoTime + nanoTickLength;
                    try {
                        tick.accept(delta);
                        if (failed != FAILED_CONSOLE_SPAM) {
                            logger.log(Level.FINE, "Recovered from GameTick fail for some reason");
                            failed = FAILED_CONSOLE_SPAM;
                        }
                    } catch (Exception exp) {
                        if (failed > 0) {
                            logger.log(Level.SEVERE, "Failed to run gametick (" + (failed--) + "/" + FAILED_CONSOLE_SPAM + ")!", exp);
                        }
                    }
                    executed++;
                    nanoTime = System.nanoTime();
                    if (tickLength > 20) {
                        Thread.sleep(tickLength - 10);
                    }
                    while (nanoTmpTime - nanoTime < hundrethNanoTickLength) {
                        Thread.yield();
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                continue;
            }
        }
    }

    public Thread getTickThread() {
        return tickThread;
    }

    public void setTargetTps(int targetTps) {
        this.targetTps = Math.min(Math.max(Math.abs(targetTps), 1), 1000);
        this.tickLength = Math.max((long) Math.ceil(1000d / targetTps), 1);
        this.nanoTickLength = TimeUnit.MILLISECONDS.toNanos(tickLength);
        this.hundrethNanoTickLength = Math.floorDiv(nanoTickLength, 100);
    }

    public int getTargetTps() {
        return targetTps;
    }

    public long getTickLength() {
        return tickLength;
    }

    public float getTps() {
        return tps;
    }

    public boolean isStopped() {
        return state == TickState.STOPPED;
    }

    public boolean isPaused() {
        return state == TickState.PAUSED;
    }

    public boolean isRunning() {
        return state == TickState.RUNNING;
    }

    public void start() {
        if (state == TickState.STOPPED) {
            return;
        }
        state = TickState.RUNNING;
        tickThread.interrupt();
    }

    public void pause() {
        if (state == TickState.STOPPED) {
            return;
        }
        state = TickState.PAUSED;
        tickThread.interrupt();
    }

    public void stop() {
        state = TickState.STOPPED;
        tickThread.interrupt();
    }

}
