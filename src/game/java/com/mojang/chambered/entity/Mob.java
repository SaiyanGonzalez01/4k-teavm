package com.mojang.chambered.entity;

import com.mojang.chambered.level.Level;
import com.mojang.chambered.level.Tile;
import com.mojang.chambered.phys.CollisionSphere;
import com.mojang.chambered.phys.Vec;

public class Mob extends Entity {
   private CollisionSphere sphere;
   private Tile lastTile;

   public Mob(double radius) {
      this.sphere = new CollisionSphere(0.0, 0.0, radius);
      this.addCollideable(this.sphere);
   }

   public void attemptMove(Level level, double xd, double zd) {
      this.attemptMove(level, xd, zd, 39L);
   }

   public boolean attemptMove(Level level, double xd, double zd, long collideMask) {
      Vec pos = this.sphere.move(this.x, this.z, this.x + xd, this.z + zd, level.getCollideables(this.x, this.z, collideMask));
      this.x = pos.x;
      this.z = pos.y;
      Tile tile = level.getTileAt(this.x, this.z);
      if (tile != this.lastTile) {
         if (this.lastTile != null) {
            this.lastTile.removeBlocker(this);
         }

         if (tile != null) {
            tile.addBlocker(this);
         }

         this.lastTile = tile;
      }

      this.setPos(this.x, this.y, this.z);
      this.sphere.pos.set(this.x, this.z);
      return this.sphere.hasCollided;
   }

   public void setRadius(double radius) {
      this.sphere.radius = radius;
   }

   @Override
   public void setPos(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.sphere.pos.set(x, z);
   }

   public void removed() {
      if (this.lastTile != null) {
         this.lastTile.removeBlocker(this);
      }
   }
}
