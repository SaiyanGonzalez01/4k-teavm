package com.mojang.chambered.level.tile;

import com.mojang.chambered.display.Wall;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import com.mojang.chambered.phys.CollisionSphere;
import java.util.List;

public class ColumnTile extends Surface {
   public ColumnTile() {
      double radius = 0.1875;
      int steps = 16;

      for (int i = 0; i < steps; i++) {
         int j = (i + 1) % steps;
         double x0 = Math.sin(i * Math.PI * 2.0 / steps) * radius + 0.5;
         double y0 = Math.cos(i * Math.PI * 2.0 / steps) * radius + 0.5;
         double x1 = Math.sin(j * Math.PI * 2.0 / steps) * radius + 0.5;
         double y1 = Math.cos(j * Math.PI * 2.0 / steps) * radius + 0.5;
         Wall wall = this.addWall(x0, 0.0, y0, x1, 1.0, y1, true, true, true);
         wall.u0 = (double)i / steps;
         wall.u1 = (double)(i + 1) / steps;
         wall.wallTexture = BitmapCache.get("/wall/stairs.png");
      }
   }

   @Override
   public void getCollideables(List<Collideable> collideables, double xo, double yo) {
      CollisionSphere sphere = new CollisionSphere(xo, yo, 24.0);
      collideables.add(sphere);
   }
}
