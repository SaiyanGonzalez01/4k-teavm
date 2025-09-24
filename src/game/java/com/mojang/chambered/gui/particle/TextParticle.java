package com.mojang.chambered.gui.particle;

import com.mojang.chambered.display.Font;
import com.mojang.chambered.display.bitmap.Bitmap;

public class TextParticle extends GuiParticle {
   public int life = 32;
   private String text;
   private int color;
   private int width;
   private int time = 0;

   public TextParticle(String text, int x, int y) {
      this(text, x, y, 16777215);
   }

   public TextParticle(String text, int color) {
      this(text, 0, 0, color);
   }

   public TextParticle(String text, int x, int y, int color) {
      this.text = text;
      this.x = x;
      this.y = y;
      this.width = Font.normal.width(text);
      this.color = color;
   }

   @Override
   public boolean tick(int currentTick) {
      this.time++;
      return --this.life > 0;
   }

   @Override
   public void render(Bitmap bitmap, int currentTick, double alpha) {
      Font.normal.drawShadow(bitmap, this.text, this.x - this.width / 2, this.y, this.color);
   }
}
