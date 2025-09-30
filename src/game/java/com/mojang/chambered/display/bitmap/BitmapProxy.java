package com.mojang.chambered.display.bitmap;

public class BitmapProxy extends AbstractBitmap {
   public AbstractBitmap texture;

   @Override
   public int[] getPixels() {
      return this.texture.getPixels();
   }

   @Override
   public int getWidth() {
      return this.texture.getWidth();
   }

   @Override
   public int getHeight() {
      return this.texture.getHeight();
   }
}
