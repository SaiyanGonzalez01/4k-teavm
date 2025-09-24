package com.mojang.chambered.gui.particle;

import com.mojang.chambered.display.bitmap.Bitmap;

public class GuiParticle {
   public int x;
   public int y;

   public boolean tick(int currentTick) {
      return true;
   }

   public void render(Bitmap bitmap, int currentTick, double alpha) {
   }
}
