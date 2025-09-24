package com.mojang.chambered.particle;

import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.level.Level;

public class BounceParticle extends Particle implements Renderable {
   public double bounce = 0.97;
   public double slideInertia = 0.9;
   protected Level level;
   public int radius = 16;

   public BounceParticle(Level level, SpriteImage sprite, double x, double y, double z) {
      super(sprite, x, y, z);
      this.level = level;
   }

   @Override
   public boolean tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      double tileWidth = this.level.wallWidth;
      double halfTileWidth = this.level.wallWidth / 2.0;
      int xTile = (int)(this.x / tileWidth + 0.5);
      int zTile = (int)(this.z / tileWidth + 0.5);
      this.x = this.x + this.xa;
      boolean hasBounced = false;
      double w = this.radius;
      if (this.x < xTile * tileWidth - halfTileWidth + w && this.level.getTile(xTile - 1, zTile).isParticleSolid()) {
         this.x = xTile * tileWidth - halfTileWidth + w;
         hasBounced = true;
         this.xa = this.xa * -this.bounce;
      }

      if (this.x > xTile * tileWidth + halfTileWidth - w && this.level.getTile(xTile + 1, zTile).isParticleSolid()) {
         this.x = xTile * tileWidth + halfTileWidth - w;
         this.xa = this.xa * -this.bounce;
         hasBounced = true;
      }

      xTile = (int)(this.x / tileWidth + 0.5);
      this.z = this.z + this.za;
      if (this.z < zTile * tileWidth - halfTileWidth + w && this.level.getTile(xTile, zTile - 1).isParticleSolid()) {
         this.z = zTile * tileWidth - halfTileWidth + w;
         this.za = this.za * -this.bounce;
         hasBounced = true;
      }

      if (this.z > zTile * tileWidth + halfTileWidth - w && this.level.getTile(xTile, zTile + 1).isParticleSolid()) {
         this.z = zTile * tileWidth + halfTileWidth - w;
         this.za = this.za * -this.bounce;
         hasBounced = true;
      }

      this.y = this.y + this.ya;
      if (this.y >= 0.0) {
         this.y = 0.0;
         this.ya = this.ya * -this.bounce;
         hasBounced = true;
      }

      if (hasBounced) {
         this.xa = this.xa * this.slideInertia;
         this.ya = this.ya * this.slideInertia;
         this.za = this.za * this.slideInertia;
      }

      this.ya = this.ya + this.gravity;
      this.xa = this.xa * this.inertia;
      this.ya = this.ya * this.inertia;
      this.za = this.za * this.inertia;
      return this.life++ < this.lifeSpan;
   }
}
