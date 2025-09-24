package com.mojang.chambered.display.sprite;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.SpriteImage;

public class BobbingSprite extends Sprite {
   private double bobSpeed = Math.random() * 0.04 + 0.01;
   private double bobOffset = Math.random() * Math.PI;

   public BobbingSprite(SpriteImage spriteImage, double x, double y, double z) {
      super(spriteImage, x, y, z);
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      double now = (currentTick + a) * this.bobSpeed + this.bobOffset;
      double xo = Math.sin(now) * 1.0;
      double yo = Math.sin(now * 0.9833) * 2.0;
      double zo = Math.cos(now * 0.727672) * 1.0;
      viewport.renderSprite(this.spriteImage.setPos(this.x + xo, this.y + yo, this.z + zo).mirror(this.mirrored).move(camera), this.pickable);
   }
}
