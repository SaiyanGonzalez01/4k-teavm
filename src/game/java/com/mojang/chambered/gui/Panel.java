package com.mojang.chambered.gui;

public class Panel {
   public final int x;
   public final int y;
   public final int w;
   public final int h;
   public final boolean solid;

   public Panel(int x, int y, int w, int h) {
      this(x, y, w, h, false);
   }

   public Panel(int x, int y, int w, int h, boolean solid) {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.solid = solid;
   }
}
