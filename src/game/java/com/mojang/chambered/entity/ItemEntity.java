package com.mojang.chambered.entity;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.item.ItemInstance;

public class ItemEntity extends Entity {
   private ItemInstance itemInstance;
   private boolean removed = false;

   public ItemEntity(ItemInstance itemInstance, double x, double y, double z) {
      this.itemInstance = itemInstance;
      this.setPos(x, y, z);
   }

   @Override
   public boolean tick() {
      return !this.removed;
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      if (!this.removed) {
         SpriteImage img = this.itemInstance.getEntitySprite().setPos(this.x, this.y, this.z);
         if (this.isPicked()) {
            viewport.renderSprite(img.move(camera).scale(1.2, 1.2), this);
         } else {
            viewport.renderSprite(img.move(camera), this);
         }
      }
   }

   @Override
   public boolean mouseDown(int x, int y, int button) {
      if (this.removed) {
         return false;
      } else if (this.itemInstance.take()) {
         this.removed = true;
         return true;
      } else {
         return false;
      }
   }
}
