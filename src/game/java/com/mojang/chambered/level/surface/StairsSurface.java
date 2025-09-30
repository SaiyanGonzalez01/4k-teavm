package com.mojang.chambered.level.surface;

import com.mojang.chambered.display.Wall;
import com.mojang.chambered.display.bitmap.BitmapCache;

public class StairsSurface extends Surface {
   public StairsSurface(int dir) {
      this.floorTexture = BitmapCache.instance.getTexture("/floor/stone.png");
      int steps = 32;
      double stepSize = 0.16666666666666666;

      for (int i = 0; i < steps; i++) {
         double x0 = 0.0;
         double x1 = 1.0;
         double y0 = (i + 1 - dir) * stepSize * dir;
         double y1 = (i + 0 - dir) * stepSize * dir;
         double h = 1.0 + stepSize;
         double z0 = 1.0 - i / 16.0 * 4.0;
         double z1 = 1.0 - (i + 1) / 16.0 * 4.0;
         if (dir == 1) {
            Wall wall = this.addWall(x0, y1, z0, x1, y0, z0, false, true, true);
            wall.wallTexture = BitmapCache.instance.getTexture("/wall/stairs.png");
            this.addWall(x0, y0 + h, z1, x1, y0 + h, z1, true, false, false);
         } else {
            Wall wall = this.addWall(x0, y0 + h, z0, x1, y1 + h, z0, true, true, false);
            wall.wallTexture = BitmapCache.instance.getTexture("/wall/stairs.png");
            this.addWall(x0, y0, z1, x1, y0, z1, false, false, true);
         }

         this.addWall(x0, y0, z0, x0, y0 + h, z1, true, true, true);
         this.addWall(x1, y0, z1, x1, y0 + h, z0, true, true, true);
         if (i == steps - 1) {
            this.addWall(x0, y0, z1, x1, y0 + h, z1, true, true, true);
         }
      }
   }
}
