package com.mojang.chambered.level.tile;

import com.mojang.chambered.display.Wall;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import java.util.List;

public class PressurePlateTile extends Surface {
   public PressurePlateTile() {
      double radius = 0.5;
      double h = 0.03125;
      int steps = 6;
      double rotOffs = Math.PI / 4;

      for (int i = 0; i < steps; i++) {
         int j = (i + 1) % steps;
         double x0 = Math.sin(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y0 = Math.cos(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double x1 = Math.sin(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y1 = Math.cos(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         this.floorTexture = defaultFloorTexture;
         Wall wall = this.addWall(x0, 1.0 - h, y0, x1, 1.0, y1, true, true, false);
         wall.u0 = (double)i / steps;
         wall.u1 = (double)(i + 1) / steps;
         wall.wallTexture = BitmapCache.get("/wall/stairs.png");
      }

      this.floorTexture = BitmapCache.get("/floor/stone.png");

      for (int i = 0; i < steps; i++) {
         int j = (i + 1) % steps;
         double x0 = Math.sin(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y0 = Math.cos(i * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double x1 = Math.sin(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         double y1 = Math.cos(j * Math.PI * 2.0 / steps + rotOffs) * radius + 0.5;
         Wall wall = this.addWall(x1, 1.0 - h, y1, x0, 1.0 - h, y0, true, false, false);
         wall.u0 = (double)i / steps;
         wall.u1 = (double)(i + 1) / steps;
         wall.wallTexture = BitmapCache.get("/wall/stairs.png");
      }
   }

   @Override
   public void getCollideables(List<Collideable> collideables, double xo, double yo) {
   }
}
