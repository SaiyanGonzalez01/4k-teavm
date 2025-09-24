package com.mojang.chambered.display;

public class PickResult {
   public Pickable source;
   public int u;
   public int v;
   public boolean isFloor = false;

   public PickResult(Pickable source, int u, int v) {
      this.source = source;
      this.u = u;
      this.v = v;
   }

   public PickResult setSprite() {
      return this;
   }

   public PickResult setCeiling() {
      return this;
   }

   public PickResult setFloor() {
      this.isFloor = true;
      return this;
   }

   public PickResult setWall() {
      return this;
   }
}
