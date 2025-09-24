package com.mojang.chambered.display.bitmap;

public class NullBitmap extends AbstractBitmap {
   private int width;
   private int height;

   public NullBitmap(int width, int height) {
      this.width = width;
      this.height = height;
   }

   @Override
   public int[] getPixels() {
      return null;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }
}
