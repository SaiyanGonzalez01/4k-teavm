package com.mojang.chambered.level.surface;

class FlatSurface extends Surface {
   public FlatSurface() {
      this.addWall(0.0, 0.0, 1.0, 1.0, 1.0, 1.0, true, true, true);
   }
}
