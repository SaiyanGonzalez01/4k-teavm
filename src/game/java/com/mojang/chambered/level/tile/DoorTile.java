package com.mojang.chambered.level.tile;

import com.mojang.chambered.display.bitmap.AbstractBitmap;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.NullBitmap;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import com.mojang.chambered.phys.CollisionLine;
import java.util.List;

public class DoorTile extends Surface {
   private AbstractBitmap frontCeiling = defaultCeilingTexture;
   private AbstractBitmap backCeiling = defaultCeilingTexture;
   private double openness;

   public DoorTile() {
      this.addWalls();
   }

   public void addWalls() {
      this.uOffs = this.vOffs = 0.0;
      this.walls.clear();
      this.openness = Math.sin(System.currentTimeMillis() / 1000.0) * 0.5 + 0.5;
      double h = 0.16666666666666666;
      double w = 0.16666666666666666;
      double z0 = 0.4;
      double z1 = 0.6;
      double dz0 = 0.45;
      double dz1 = 0.55;
      this.frontCeiling = new NullBitmap(128, 128);
      this.ceilingTexture = this.frontCeiling;
      this.addWall(0.0 + w, 0.0, z1, 1.0 - w, h, z1, false, true, true);
      this.addWall(0.0, 0.0, z1, 0.0 + w, 1.0, z1, true, true, true);
      this.addWall(1.0 - w, 0.0, z1, 1.0, 1.0, z1, true, true, true);
      this.ceilingTexture = this.backCeiling;
      this.addWall(0.0 + w, 0.0, z0, 0.0, 1.0, z0, true, true, true);
      this.addWall(1.0 - w, 0.0, z0, 0.0 + w, h, z0, false, true, true);
      this.addWall(1.0, 0.0, z0, 1.0 - w, 1.0, z0, true, true, true);
      this.ceilingTexture = defaultCeilingTexture;
      this.wallTexture = BitmapCache.get("/wall/wood.png");
      int mode = 3;
      if (mode == 0) {
         double doorHeight = (1.0 - h) * this.openness + h;
         this.vOffs = -doorHeight;
         this.addWall(0.0 + w, h, dz1, 1.0 - w, doorHeight, dz1, false, true, true);
         this.addWall(1.0 - w, h, dz0, 0.0 + w, doorHeight, dz0, false, true, true);
         this.addWall(1.0 - w, doorHeight, dz1, 0.0 + w, doorHeight, dz1, false, false, true);
         this.addWall(0.0 + w, doorHeight, dz0, 1.0 - w, doorHeight, dz0, false, false, true);
         this.addWall(1.0 - w, doorHeight, dz0, 1.0 - w, doorHeight, dz1, false, false, true);
         this.addWall(0.0 + w, doorHeight, dz1, 0.0 + w, doorHeight, dz0, false, false, true);
         this.vOffs = 0.0;
      } else if (mode == 1) {
         double doorGap = this.openness * (1.0 - w * 2.0);
         this.uOffs = -doorGap;
         this.addWall(1.0 - w, h, dz0, 1.0 - w - doorGap, 1.0, dz0, true, true, true);
         this.uOffs = doorGap;
         this.addWall(1.0 - w - doorGap, h, dz1, 1.0 - w, 1.0, dz1, true, true, true);
         this.addWall(1.0 - w - doorGap, h, dz0, 1.0 - w - doorGap, 1.0, dz1, true, true, true);
         this.uOffs = 0.0;
      } else if (mode == 2) {
         double doorGap = this.openness * (1.0 - w * 2.0);
         this.uOffs = -doorGap;
         this.addWall(0.0 + w + doorGap, h, dz0, 0.0 + w, 1.0, dz0, true, true, true);
         this.addWall(0.0 + w, h, dz1, 0.0 + w + doorGap, 1.0, dz1, true, true, true);
         this.addWall(0.0 + w + doorGap, h, dz1, 0.0 + w + doorGap, 1.0, dz0, true, true, true);
         this.uOffs = 0.0;
      } else if (mode == 3) {
         double doorGap = this.openness * 0.5 * (1.0 - w * 2.0);
         this.uOffs = doorGap;
         this.addWall(1.0 - w - doorGap, h, dz1, 1.0 - w, 1.0, dz1, true, true, true);
         this.uOffs = -doorGap;
         this.addWall(1.0 - w, h, dz0, 1.0 - w - doorGap, 1.0, dz0, true, true, true);
         this.addWall(0.0 + w + doorGap, h, dz0, 0.0 + w, 1.0, dz0, true, true, true);
         this.addWall(0.0 + w, h, dz1, 0.0 + w + doorGap, 1.0, dz1, true, true, true);
         this.addWall(0.0 + w + doorGap, h, dz1, 0.0 + w + doorGap, 1.0, dz0, true, true, true);
         this.uOffs = doorGap;
         this.addWall(1.0 - w - doorGap, h, dz0, 1.0 - w - doorGap, 1.0, dz1, true, true, true);
         this.uOffs = 0.0;
      }

      this.wallTexture = defaultWallTexture;
      this.addWall(0.0 + w, h, z1, 0.0 + w, 1.0, z0, true, true, true);
      this.addWall(1.0 - w, h, z0, 1.0 - w, 1.0, z1, true, true, true);
      this.addWall(1.0 - w, h, z1, 0.0 + w, h, z1, false, false, true);
      this.addWall(0.0 + w, h, z0, 1.0 - w, h, z0, false, false, true);
      this.ceilingTexture = this.frontCeiling;
      this.addWall(0.0, 0.0, 1.0, 0.0, 1.0, z1, true, false, true);
      this.addWall(1.0, 0.0, z1, 1.0, 1.0, 1.0, true, false, true);
      this.addWall(1.0, 0.0, 1.0, 0.0, 1.0, 1.0, true, false, true);
      this.ceilingTexture = this.backCeiling;
      this.addWall(0.0, 0.0, z0, 0.0, 1.0, 0.0, true, false, true);
      this.addWall(1.0, 0.0, 0.0, 1.0, 1.0, z0, true, false, true);
      this.addWall(0.0, 0.0, 0.0, 1.0, 1.0, 0.0, true, false, true);
   }

   @Override
   public void getCollideables(List<Collideable> collideables, double xo, double yo) {
      double w = 21.333333333333332;
      double z0 = -12.8;
      double z1 = 12.8;
      this.addLine(collideables, xo - 64.0, yo + z0, xo - 64.0 + w, yo + z0, false, false);
      this.addLine(collideables, xo - 64.0 + w, yo + z0, xo - 64.0 + w, yo + z1, true, true);
      this.addLine(collideables, xo - 64.0 + w, yo + z1, xo - 64.0, yo + z1, false, false);
      this.addLine(collideables, xo + 64.0, yo + z0, xo + 64.0 - w, yo + z0, false, false);
      this.addLine(collideables, xo + 64.0 - w, yo + z0, xo + 64.0 - w, yo + z1, true, true);
      this.addLine(collideables, xo + 64.0 - w, yo + z1, xo + 64.0, yo + z1, false, false);
      double w2 = (64.0 - w) * (1.0 - this.openness);
      this.addLine(collideables, xo + 64.0 - w, yo, xo + w2, yo, true, true);
      this.addLine(collideables, xo - 64.0 + w, yo, xo - w2, yo, true, true);
   }

   private void addLine(List<Collideable> collideables, double x0, double y0, double x1, double y1, boolean sphere0, boolean sphere1) {
      CollisionLine line = new CollisionLine(x0, y0, x1, y1);
      collideables.add(line);
      if (sphere0) {
         collideables.add(line.sphere0);
      }

      if (sphere1) {
         collideables.add(line.sphere1);
      }
   }

   @Override
   public void update() {
      this.addWalls();
   }
}
