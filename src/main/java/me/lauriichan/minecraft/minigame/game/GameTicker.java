package me.lauriichan.minecraft.minigame.game;

import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class GameTicker {

    private static final int FAILED_CONSOLE_SPAM = 3;

    private static final long SECOND_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final long MINUTE_IN_NANO = TimeUnit.MINUTES.toNanos(1);
    private static final long MILLIS_IN_NANO = TimeUnit.MILLISECONDS.toNanos(1);

    private static enum TickState {
        RUNNING,
        PAUSED,
        STOPPED;
    }

    private final Thread tickThread;

    private final LongConsumer tick;
    private final Logger logger;

    private TickState state = TickState.PAUSED;
    private int failed = FAILED_CONSOLE_SPAM;

    private long averageTickTime;
    private int tps, tpm;

    private long targetTickLength;

    public GameTicker(final Logger logger, final LongConsumer tick) {
        setTargetTps(20);
        this.tickThread = new Thread(this::tick);
        tickThread.setName("GameTick");
        tickThread.setDaemon(true);
        tickThread.start();
        this.logger = logger;
        this.tick = tick;
    }

    private void tick() {
        long waitTime = 0, waitMillis = 0;
        int cycles = 0;
        long lastTick = System.nanoTime(), time = 0;
        long sCount = 0, mCount = 0;
        int sTicks = 0, mTicks = 0;
        long delta = 0;
        while (state != TickState.STOPPED) {
            sCount = sTicks = 0;
            while (state == TickState.RUNNING) {
                time = System.nanoTime();
                delta = Math.max(time - lastTick, 0);
                lastTick = time;
                sCount += delta;
                mCount += delta;
                sTicks++;
                mTicks++;
                executeTick(delta);
                if (sCount >= SECOND_IN_NANO) {
                    this.averageTickTime = Math.floorDiv(sCount, sTicks);
                    sCount -= SECOND_IN_NANO;
                    this.tps = sTicks;
                    sTicks = 0;
                }
                if (mCount >= MINUTE_IN_NANO) {
                    mCount -= MINUTE_IN_NANO;
                    this.tpm = mTicks;
                    mTicks = 0;
                }
                waitTime = targetTickLength - (System.nanoTime() - lastTick);
                if (waitTime <= 0) {
                    continue;
                }
                waitMillis = Math.floorDiv(waitTime, MILLIS_IN_NANO);
                if (waitMillis > 5) {
                    try {
                        Thread.sleep(waitMillis - 3);
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                waitTime -= waitMillis * MILLIS_IN_NANO;
                cycles = (int) Math.floorDiv(waitTime, 2);
                while (cycles-- > 0) {
                    Thread.yield();
                }
            }
            if (state == TickState.PAUSED) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }

    private void executeTick(long delta) {
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
    }

    public Thread getTickThread() {
        return tickThread;
    }

    public void setTargetTps(int targetTps) {
        this.targetTickLength = MILLIS_IN_NANO * Math.max((long) Math.ceil(1000d / targetTps), 1);
    }

    public int getTargetTps() {
        return (int) Math.floorDiv(1000 * MILLIS_IN_NANO, targetTickLength);
    }
    
    public void setTargetTickLength(long targetLength) {
        this.targetTickLength = Math.max(targetLength, 1);
    }

    public long getTargetTickLength() {
        return targetTickLength;
    }
    
    public long getAverageTickTime() {
        return averageTickTime;
    }

    public int getTps() {
        return tps;
    }
    
    public int getTpm() {
        return tpm;
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

    public void startNonInt() {
        if (state == TickState.STOPPED) {
            return;
        }
        state = TickState.RUNNING;
    }

    public void pause() {
        if (state == TickState.STOPPED) {
            return;
        }
        state = TickState.PAUSED;
        tickThread.interrupt();
    }

    public void pauseNonInt() {
        if (state == TickState.STOPPED) {
            return;
        }
        state = TickState.PAUSED;
    }

    public void stop() {
        state = TickState.STOPPED;
        tickThread.interrupt();
    }

}
