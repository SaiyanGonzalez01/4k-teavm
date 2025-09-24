package com.mojang.chambered.display.sprite;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Pickable;
import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.SpriteImage;

public class Sprite implements Renderable {
   protected SpriteImage spriteImage;
   protected double x;
   protected double y;
   protected double z;
   public boolean mirrored = false;
   public Pickable pickable;

   public Sprite(SpriteImage spriteImage, double x, double y, double z) {
      this.spriteImage = spriteImage;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      viewport.renderSprite(this.spriteImage.setPos(this.x, this.y, this.z).mirror(this.mirrored).move(camera), this.pickable);
   }
}
