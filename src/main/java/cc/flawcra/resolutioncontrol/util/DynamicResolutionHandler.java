package cc.flawcra.resolutioncontrol.util;

import cc.flawcra.resolutioncontrol.ResolutionControlMod;
import cc.flawcra.resolutioncontrol.util.Config;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

public class DynamicResolutionHandler {
    public static final DynamicResolutionHandler INSTANCE = new DynamicResolutionHandler();
    private final FloatList scales = new FloatArrayList();
    private int baseScale;
    private int timer = 10;
    private int currentScale;
    private long lastTickTime = System.nanoTime();
    private float tickTimeSum = 0;
    private int tickCount = 0;

    private DynamicResolutionHandler() {
        reset();
    }

    public void tick() {
        long now = System.nanoTime();
        float tickDuration = (now - lastTickTime) / 1_000_000.0f; // Tick duration in milliseconds
        lastTickTime = now;

        tickTimeSum += tickDuration;
        tickCount++;

        if (tickCount >= Config.getInstance().drFpsSmoothAmount) {
            float averageTickTime = tickTimeSum / tickCount;
            update(averageTickTime);
            tickTimeSum = 0;
            tickCount = 0;
        }

        if (--timer <= 0) {
            timer = 10;
        }
    }

    private void update(float averageTickTime) {
        // Calculate the equivalent FPS
        float fps = 1000.0f / averageTickTime;

        if (fps > Config.getInstance().drMaxFps && currentScale < scales.size() - 1) {
            setCurrentScale(currentScale + 1);
            timer = 15;
        } else if (fps < Config.getInstance().drMinFps && currentScale > 0) {
            setCurrentScale(currentScale - 1);
            timer = 5;
        } else {
            timer = 3;
        }
    }

    private void setCurrentScale(int index) {
        if (this.currentScale != index) {
            this.currentScale = index;
            ResolutionControlMod.getInstance().updateFramebufferSize();
        }
    }

    public double getCurrentScale() {
        return scales.getFloat(currentScale);
    }

    public void reset() {
        scales.clear();
        for (float i = Config.getInstance().drMinScale; i <= Config.getInstance().drMaxScale; i += Config.getInstance().drResStep) {
            scales.add(i);
            if (i == 1.0f) {
                baseScale = scales.size() - 1;
                currentScale = baseScale;
            }
        }
    }
}
