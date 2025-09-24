package com.mojang.chambered.entity;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Pickable;
import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.phys.Collideable;
import java.util.ArrayList;
import java.util.List;

public class Entity extends Pickable implements Renderable {
   public double x;
   public double y;
   public double z;
   public double rot;
   private List<Collideable> collideables;
   protected long collideType = 16L;

   public void addCollideable(Collideable collideable) {
      if (this.collideables == null) {
         this.collideables = new ArrayList<>();
      }

      this.collideables.add(collideable);
   }

   public void getCollideables(List<Collideable> collideables, long mask) {
      if ((mask & this.collideType) > 0L && this.collideables != null) {
         collideables.addAll(this.collideables);
      }
   }

   public void setPos(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public boolean tick() {
      return true;
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double alpha) {
   }
}
