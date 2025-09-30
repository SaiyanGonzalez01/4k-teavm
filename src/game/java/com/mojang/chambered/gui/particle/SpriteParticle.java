package com.mojang.chambered.gui.particle;

import com.mojang.chambered.display.bitmap.Bitmap;
import com.mojang.chambered.display.bitmap.SpriteImage;

public class SpriteParticle extends GuiParticle {
   private SpriteImage image;
   private int life = 32;

   public SpriteParticle(SpriteImage image, int x, int y) {
      this.image = image;
      this.x = x;
      this.y = y;
   }

   @Override
   public boolean tick(int currentTick) {
      return --this.life > 0;
   }

   @Override
   public void render(Bitmap bitmap, int currentTick, double alpha) {
      bitmap.blit(this.image, this.x, this.y);
   }
}
