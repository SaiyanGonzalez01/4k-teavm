package com.mojang.chambered.level.tile;

import com.mojang.chambered.display.Wall;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import com.mojang.chambered.phys.CollisionSphere;
import java.util.List;

public class PitTile extends Surface {
   public PitTile() {
      double radius = 0.46875;
      double h = -2.0;
      int steps = 12;
      double rotOffs = Math.PI / 4;

      for (int i = 0; i < steps; i++) {
         int j = (i + 1) % steps;
         double x0 = Math.sin(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y0 = Math.cos(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double x1 = Math.sin(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y1 = Math.cos(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         this.floorTexture = defaultFloorTexture;
         Wall wall = this.addWall(x0, 1.0, y0, x1, 1.0, y1, true, false, false);
         wall.u0 = (double)(i + 1) / steps * 2.0;
         wall.u1 = (double)i / steps * 2.0;
         wall.wallTexture = BitmapCache.get("/wall/stairs.png");
      }

      for (int i = 0; i < steps; i++) {
         int j = (i + 1) % steps;
         double x0 = Math.sin(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y0 = Math.cos(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double x1 = Math.sin(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y1 = Math.cos(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         Wall wall = this.addWall(x1, 1.0, y1, x0, 1.0 - h, y0, true, true, false);
         wall.u0 = (double)(i + 1) / steps * 2.0;
         wall.u1 = (double)i / steps * 2.0;
      }
   }

   @Override
   public void getCollideables(List<Collideable> collideables, double xo, double yo) {
      CollisionSphere sphere = new CollisionSphere(xo, yo, 40.0);
      collideables.add(sphere);
   }
}
