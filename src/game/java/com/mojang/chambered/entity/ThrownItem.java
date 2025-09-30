package com.mojang.chambered.entity;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.item.ItemInstance;

public class ThrownItem extends Mob {
   private World world;
   private double xLast;
   private double yLast;
   private double zLast;
   private double xd;
   private double yd;
   private double zd;
   private ItemInstance itemInstance;

   public ThrownItem(World world, ItemInstance itemInstance, double x, double y, double z, double rot, double power) {
      super(16.0);
      this.collideType = 8L;
      this.world = world;
      this.itemInstance = itemInstance;
      this.rot = rot;
      this.setPos(x, y - 48.0, z);
      this.xLast = x;
      this.yLast = this.y;
      this.zLast = z;
      this.xd = Math.sin(rot) * power * 1.0;
      this.zd = -Math.cos(rot) * power * 1.0;
      this.yd = -5.0;
   }

   @Override
   public boolean tick() {
      this.xLast = this.x;
      this.yLast = this.y;
      this.zLast = this.z;
      boolean collided = this.attemptMove(this.world.level, this.xd, this.zd, 33L);
      if (collided) {
         this.xd *= 0.5;
         this.zd *= 0.5;
      }

      this.y = this.y + this.yd;
      this.xd *= 0.98;
      this.yd *= 0.98;
      this.zd *= 0.98;
      this.yd++;
      if (this.y >= 0.0) {
         this.y = 0.0;
         this.world.level.addItem(this.itemInstance, this.x, this.y, this.z);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      double xx = this.xLast + (this.x - this.xLast) * a;
      double yy = this.yLast + (this.y - this.yLast) * a;
      double zz = this.zLast + (this.z - this.zLast) * a;
      SpriteImage img = this.itemInstance.getEntitySprite().setPos(xx, yy, zz);
      viewport.renderSprite(img.move(camera), this);
   }
}
