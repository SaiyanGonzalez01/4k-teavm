package com.mojang.chambered.level.surface;

public class FloorSurface extends Surface {
   public FloorSurface(boolean floor, boolean ceiling) {
      this.clipWall = null;
      this.addWall(0.0, 0.0, 0.0, 1.0, 1.0, 0.0, floor, false, ceiling);
   }
}
