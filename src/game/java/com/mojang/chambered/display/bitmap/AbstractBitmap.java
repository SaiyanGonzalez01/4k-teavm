package com.mojang.chambered.display.bitmap;

public abstract class AbstractBitmap {
   public boolean isEmpty(int x, int y, int w, int h) {
      return false;
   }

   public abstract int[] getPixels();

   public abstract int getWidth();

   public abstract int getHeight();
}
