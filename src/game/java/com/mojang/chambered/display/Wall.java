package com.mojang.chambered.display;

import com.mojang.chambered.display.bitmap.AbstractBitmap;
import com.mojang.chambered.level.surface.Surface;

public class Wall {
   public double x0;
   public double y0;
   public double z0;
   public double x1;
   public double y1;
   public double z1;
   public double u0;
   public double v0;
   public double u1;
   public double v1;
   public boolean visible = true;
   public boolean hasFloor;
   public boolean hasWall;
   public boolean hasCeiling;
   public int brightness = 0;
   public AbstractBitmap wallTexture;
   public AbstractBitmap ceilingTexture;
   public AbstractBitmap floorTexture;
   public double xn;
   public double zn;
   double[] COS = new double[]{1.0, 0.0, -1.0, 0.0};
   double[] SIN = new double[]{0.0, 1.0, 0.0, -1.0};
   private static final double CLIP_DISTANCE = 1.0;

   private Wall(Wall wall) {
      this.hasFloor = wall.hasFloor;
      this.hasWall = wall.hasWall;
      this.hasCeiling = wall.hasCeiling;
      this.u0 = wall.u0;
      this.u1 = wall.u1;
      this.v0 = wall.v0;
      this.v1 = wall.v1;
      this.wallTexture = wall.wallTexture;
      this.ceilingTexture = wall.ceilingTexture;
      this.floorTexture = wall.floorTexture;
   }

   public static Wall copy(Wall wall) {
      Wall result = new Wall(wall);
      result.x0 = wall.x0;
      result.y0 = wall.y0;
      result.z0 = wall.z0;
      result.x1 = wall.x1;
      result.y1 = wall.y1;
      result.z1 = wall.z1;
      return result;
   }

   public Wall(double x0, double y0, double z0, double x1, double y1, double z1, boolean hasFloor, boolean hasWall, boolean hasCeiling) {
      this.hasFloor = hasFloor;
      this.hasWall = hasWall;
      this.hasCeiling = hasCeiling;
      this.x0 = x0;
      this.y0 = y0;
      this.z0 = z0;
      this.x1 = x1;
      this.y1 = y1;
      this.z1 = z1;
      double xd = x1 - x0;
      double zd = z1 - z0;
      this.u0 = x0 / 128.0 + z0 / 128.0 + 10.0;
      this.v0 = y0 / 96.0 + 10.0;
      this.u1 = this.u0 + Math.sqrt(xd * xd + zd * zd) / Surface.wallWidth;
      this.v1 = this.v0 + (y1 - y0) / Surface.ceilingHeight;
   }

   public Wall move(double x, double y, double z) {
      Wall wall = new Wall(this);
      wall.x1 = this.x1 + x;
      wall.z0 = this.z0 + z;
      wall.z1 = this.z1 + z;
      wall.y0 = this.y0 + y;
      wall.y1 = this.y1 + y;
      return wall;
   }

   public Wall move(double x, double y, double z, double rotation) {
      double cos = Math.cos(rotation);
      double sin = Math.sin(rotation);
      Wall wall = new Wall(this);
      wall.x0 = cos * (this.x0 + x) + sin * (this.z0 + z);
      wall.x1 = cos * (this.x1 + x) + sin * (this.z1 + z);
      wall.z0 = cos * (this.z0 + z) - sin * (this.x0 + x);
      wall.z1 = cos * (this.z1 + z) - sin * (this.x1 + x);
      wall.y0 = this.y0 + y;
      wall.y1 = this.y1 + y;
      return wall;
   }

   public Wall move(int x, int y, int z, int rotation) {
      double cos = this.COS[rotation & 3];
      double sin = this.SIN[rotation & 3];
      Wall wall = new Wall(this);
      wall.x0 = cos * this.x0 + sin * this.z0 + x * Surface.wallWidth;
      wall.x1 = cos * this.x1 + sin * this.z1 + x * Surface.wallWidth;
      wall.z0 = cos * this.z0 - sin * this.x0 + z * Surface.wallWidth;
      wall.z1 = cos * this.z1 - sin * this.x1 + z * Surface.wallWidth;
      wall.y0 = this.y0 + y * Surface.ceilingHeight;
      wall.y1 = this.y1 + y * Surface.ceilingHeight;
      return wall;
   }

   public Wall clip() {
      Wall wall = new Wall(this);
      wall.x0 = this.x0;
      wall.x1 = this.x1;
      wall.z0 = this.z0;
      wall.z1 = this.z1;
      wall.y0 = this.y0;
      wall.y1 = this.y1;
      wall.performClip();
      return wall;
   }

   private void performClip() {
      if (this.z0 < 1.0 && this.z1 < 1.0) {
         this.visible = false;
      } else {
         if (this.z0 < 1.0) {
            double d = (1.0 - this.z0) / (this.z1 - this.z0);
            this.x0 = this.x0 + (this.x1 - this.x0) * d;
            this.u0 = this.u0 + (this.u1 - this.u0) * d;
            this.z0 = 1.0;
         }

         if (this.z1 < 1.0) {
            double d = (1.0 - this.z1) / (this.z0 - this.z1);
            this.x1 = this.x1 + (this.x0 - this.x1) * d;
            this.u1 = this.u1 + (this.u0 - this.u1) * d;
            this.z1 = 1.0;
         }
      }
   }

   public Wall move(Camera cam) {
      Wall wall = new Wall(this);
      wall.x0 = cam.cos * (this.x0 + cam.x) + cam.sin * (this.z0 + cam.z);
      wall.x1 = cam.cos * (this.x1 + cam.x) + cam.sin * (this.z1 + cam.z);
      wall.z0 = -cam.cos * (this.z0 + cam.z) + cam.sin * (this.x0 + cam.x);
      wall.z1 = -cam.cos * (this.z1 + cam.z) + cam.sin * (this.x1 + cam.x);
      wall.y0 = this.y0 + cam.y;
      wall.y1 = this.y1 + cam.y;
      return wall;
   }

   public void calcNormal() {
      double xd = this.x1 - this.x0;
      double zd = this.z1 - this.z0;
      double d = Math.sqrt(xd * xd + zd * zd);
      xd /= d;
      zd /= d;
      this.xn = zd;
      this.zn = -xd;
      this.brightness = 255;
   }
}
