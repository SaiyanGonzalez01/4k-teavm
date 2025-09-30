package com.mojang.chambered.util;

public class GameTimer {
   public static final int MAX_TICKS_PER_UPDATE = 10;
   public float alpha;
   private int ticksPerSecond;
   private int msPerTick;
   private long lastTime = -1L;
   private int passedTime = 0;
   private int frames = 0;
   private int ticks = 0;
   public int fps = 0;
   private float averageFrameTime = 0.0F;
   private boolean useAverageFrameTime = false;

   public GameTimer(int ticksPerSecond) {
      this.ticksPerSecond = ticksPerSecond;
      this.msPerTick = 1000 / ticksPerSecond;
   }

   public int advanceTime() {
      long now = System.nanoTime() / 1000000L;
      if (this.lastTime == -1L) {
         this.lastTime = now;
      }

      int frameTime = (int)(now - this.lastTime);
      if (this.useAverageFrameTime) {
         this.averageFrameTime = this.averageFrameTime + (frameTime - this.averageFrameTime) * 0.1F;
         frameTime = (int)this.averageFrameTime;
      } else if (frameTime < 0) {
         System.out.println("WARNING: Negative frame time detected, switching to average frame times.");
         this.useAverageFrameTime = true;
         frameTime = 0;
      }

      this.passedTime += frameTime;
      this.lastTime = now;
      this.frames++;
      int ticksToProcess = this.passedTime / this.msPerTick;
      this.passedTime = this.passedTime - ticksToProcess * this.msPerTick;

      for (this.ticks += ticksToProcess; this.ticks >= this.ticksPerSecond; this.ticks = this.ticks - this.ticksPerSecond) {
         this.fps = this.frames;
         this.frames = 0;
      }

      this.alpha = (float)this.passedTime / this.msPerTick;
      if (ticksToProcess > 10) {
         ticksToProcess = 10;
      }

      return ticksToProcess;
   }
}
