package com.mojang.chambered.level.surface;

import com.mojang.chambered.display.Wall;
import com.mojang.chambered.display.bitmap.AbstractBitmap;
import com.mojang.chambered.display.bitmap.BitmapProxy;
import com.mojang.chambered.level.tile.ColumnTile;
import com.mojang.chambered.level.tile.DoorTile;
import com.mojang.chambered.level.tile.PitTile;
import com.mojang.chambered.level.tile.PressurePlateTile;
import com.mojang.chambered.phys.Collideable;
import java.util.ArrayList;
import java.util.List;

public class Surface {
   public static double ceilingHeight = 96.0;
   public static double wallWidth = 128.0;
   public static final BitmapProxy defaultCeilingTexture = new BitmapProxy();
   public static final BitmapProxy defaultWallTexture = new BitmapProxy();
   public static final BitmapProxy defaultFloorTexture = new BitmapProxy();
   public static final Surface flat = new FlatSurface();
   public static final Surface door = new DoorTile();
   public static final Surface column = new ColumnTile();
   public static final Surface pressurePlate = new PressurePlateTile();
   public static final Surface pit = new PitTile();
   public static final Surface niche = new NicheSurface();
   public static final Surface stairsUp = new StairsSurface(-1);
   public static final Surface stairsDown = new StairsSurface(1);
   public static final Surface floor = new FloorSurface(true, false);
   public static final Surface ceiling = new FloorSurface(false, true);
   public static final Surface floorAndCeiling = new FloorSurface(true, true);
   public Wall clipWall;
   public List<Wall> walls = new ArrayList<>();
   protected AbstractBitmap ceilingTexture = defaultCeilingTexture;
   protected AbstractBitmap wallTexture = defaultWallTexture;
   protected AbstractBitmap floorTexture = defaultFloorTexture;
   protected double uOffs;
   protected double vOffs;

   protected Surface() {
      this.clipWall = this.getWall(0.0, 0.0, 1.0, 1.0, 1.0, 1.0, true, true, true);
   }

   public Surface setWallTexture(AbstractBitmap newTexture) {
      Surface newSurface = new Surface();

      for (int i = 0; i < this.walls.size(); i++) {
         Wall wall = Wall.copy(this.walls.get(i));
         wall.wallTexture = newTexture;
         newSurface.walls.add(wall);
      }

      return newSurface;
   }

   public Surface setCeilingTexture(AbstractBitmap newTexture) {
      Surface newSurface = new Surface();

      for (int i = 0; i < this.walls.size(); i++) {
         Wall wall = Wall.copy(this.walls.get(i));
         wall.ceilingTexture = newTexture;
         newSurface.walls.add(wall);
      }

      return newSurface;
   }

   private Wall getWall(double x0, double y0, double z0, double x1, double y1, double z1, boolean hasFloor, boolean hasWall, boolean hasCeiling) {
      double xx0 = (x0 - 0.5) * wallWidth;
      double yy0 = (y0 - 1.0) * ceilingHeight;
      double zz0 = (z0 - 0.5) * wallWidth;
      double xx1 = (x1 - 0.5) * wallWidth;
      double yy1 = (y1 - 1.0) * ceilingHeight;
      double zz1 = (z1 - 0.5) * wallWidth;
      Wall wall = new Wall(xx0, yy0, zz0, xx1, yy1, zz1, hasFloor, hasWall, hasCeiling);
      wall.u0 = wall.u0 + this.uOffs;
      wall.v0 = wall.v0 + this.vOffs;
      wall.u1 = wall.u1 + this.uOffs;
      wall.v1 = wall.v1 + this.vOffs;
      wall.ceilingTexture = this.ceilingTexture;
      wall.wallTexture = this.wallTexture;
      wall.floorTexture = this.floorTexture;
      return wall;
   }

   public Wall addWall(double x0, double y0, double z0, double x1, double y1, double z1, boolean hasFloor, boolean hasWall, boolean hasCeiling) {
      Wall wall = this.getWall(x0, y0, z0, x1, y1, z1, hasFloor, hasWall, hasCeiling);
      this.walls.add(wall);
      return wall;
   }

   public void getCollideables(List<Collideable> collideables, double xo, double yo) {
   }

   public void update() {
   }
}
