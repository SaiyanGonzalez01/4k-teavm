package com.mojang.chambered.level.surface;

public class NicheSurface extends Surface {
   public NicheSurface() {
      double x0 = 0.25;
      double x1 = 0.75;
      double y0 = 0.16666666666666666;
      double y1 = 0.5;
      double z1 = 0.0;
      this.addWall(0.0, 0.0, 1.0, x0, 1.0, 1.0, true, true, true);
      this.addWall(x1, 0.0, 1.0, 1.0, 1.0, 1.0, true, true, true);
      this.addWall(x0, 0.0, 1.0, x1, y0, 1.0, false, true, true);
      this.addWall(x0, y1, 1.0, x1, 1.0, 1.0, true, true, false);
      this.addWall(x0, y0, 1.0, x0, y1, z1, true, true, true);
      this.addWall(x1, y0, z1, x1, y1, 1.0, true, true, true);
      this.addWall(x0, y0, z1, x1, y1, z1, true, true, true);
   }
}
