package com.mojang.chambered.level;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Pickable;
import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.bitmap.AbstractBitmap;
import com.mojang.chambered.entity.Entity;
import com.mojang.chambered.level.surface.Surface;
import com.mojang.chambered.phys.Collideable;
import com.mojang.chambered.phys.CollisionLine;
import com.mojang.chambered.phys.CollisionSphere;
import java.util.ArrayList;
import java.util.List;

public class Tile extends Pickable {
   private List<Renderable> renderables = new ArrayList<>();
   private List<Entity> blockers = new ArrayList<>();
   private boolean solid;
   private Level level;
   public final int x;
   public final int y;
   private World world;
   public Surface wallType = null;
   public AbstractBitmap ceilingTexture = Surface.defaultCeilingTexture;
   public AbstractBitmap floorTexture = Surface.defaultFloorTexture;

   public Tile(World world, Level level, int x, int y, boolean solid) {
      this.world = world;
      this.level = level;
      this.solid = solid;
      this.x = x;
      this.y = y;
   }

   public List<Renderable> getRenderables() {
      return this.renderables;
   }

   public boolean isSolid() {
      return this.solid;
   }

   public boolean isParticleSolid() {
      return this.isSolid();
   }

   public Surface getWall(int dir) {
      if (this.wallType != null) {
         this.wallType.update();
      }

      return this.wallType;
   }

   public void getCollideables(List<Collideable> collideables, long mask) {
      if ((mask & 1L) > 0L) {
         if (this.isSolid()) {
            double x0 = (this.x - 0.5) * this.level.wallWidth;
            double x1 = (this.x + 0.5) * this.level.wallWidth;
            double y0 = (this.y - 0.5) * this.level.wallWidth;
            double y1 = (this.y + 0.5) * this.level.wallWidth;
            this.addWall(collideables, x0, y0, x1, y0);
            this.addWall(collideables, x1, y0, x1, y1);
            this.addWall(collideables, x1, y1, x0, y1);
            this.addWall(collideables, x0, y1, x0, y0);
         } else if (this.wallType != null) {
            this.wallType.getCollideables(collideables, this.x * this.level.wallWidth, this.y * this.level.wallWidth);
         }
      }

      for (int i = 0; i < this.blockers.size(); i++) {
         this.blockers.get(i).getCollideables(collideables, mask);
      }
   }

   private void addWall(List<Collideable> collideables, double x0, double y0, double x1, double y1) {
      CollisionLine wall = new CollisionLine(x0, y0, x1, y1);
      collideables.add(wall);
      collideables.add(wall.sphere0);
   }

   public void addEntity(Entity entity) {
      this.addBlocker(entity);
      this.renderables.add(entity);
   }

   public void addBlocker(Entity entity) {
      this.blockers.add(entity);
   }

   public void removeBlocker(Entity entity) {
      this.blockers.remove(entity);
   }

   @Override
   public boolean mouseDown(int x, int y, int button) {
      if (pickResult.isFloor && this.world.gui.carried != null) {
         CollisionSphere s = new CollisionSphere(x, y, 16.0);
         if (this.world.level.isFree(s, 39L)) {
            this.world.level.addItem(this.world.gui.carried, x, 0.0, y);
            this.world.gui.carried = null;
            return true;
         }
      }

      return false;
   }
}
