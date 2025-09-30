package com.mojang.chambered.display.bitmap;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.level.surface.Surface;

public class SpriteImage {
   public double xPos;
   public double yPos;
   public double zPos;
   public double xo;
   public double yo;
   public double zo;
   public double x;
   public double y;
   public double z;
   public double w;
   public double h;
   public int u0;
   public int v0;
   public int u1;
   public int v1;
   public int brightness = 256;
   public Bitmap texture;
   private double xoOrg;
   private double yoOrg;
   private double wOrg;
   private double hOrg;
   public double normalDistance;
   public boolean mirror;
   public int solidColor = 0;

   public SpriteImage(Bitmap texture) {
      this(texture, -8.0);
   }

   public SpriteImage(Bitmap texture, double zo) {
      this(texture, 0.5, 1.0, zo);
   }

   public SpriteImage(Bitmap texture, double xo, double yo, double zo) {
      this(texture, 0, 0, texture.getWidth(), texture.getHeight(), xo, yo, zo);
   }

   public SpriteImage(Bitmap texture, int x, int y, int w, int h) {
      this(texture, x, y, w, h, 0.5, 1.0, -8.0);
   }

   public SpriteImage(Bitmap texture, int x, int y, int w, int h, double _xo, double _yo, double zo) {
      this.texture = texture;
      this.u0 = x;
      this.v0 = y;
      this.u1 = x + w;
      this.v1 = y + h;
      this.xo = w * _xo;

      for (this.yo = h * _yo; texture.isEmpty(x, y, w, 1) && h > 0; this.yo--) {
         this.v0++;
         y++;
         h--;
      }

      while (texture.isEmpty(x, y, 1, h) && w > 0) {
         this.u0++;
         x++;
         w--;
         this.xo--;
      }

      while (texture.isEmpty(x + w - 1, y, 1, h) && w > 0) {
         this.u1--;
         w--;
      }

      while (texture.isEmpty(x, y + h - 1, w, 1) && h > 0) {
         this.v1--;
         h--;
      }

      this.w = w;
      this.h = h;
      this.hOrg = h;
      this.wOrg = w;
      this.xoOrg = this.xo;
      this.yoOrg = this.yo;
      this.zo = zo;
      this.normalDistance = Surface.wallWidth * 1.25 + zo;
   }

   public SpriteImage scale(double xScale, double yScale) {
      this.w = this.wOrg * xScale;
      this.h = this.hOrg * yScale;
      this.xo = this.xoOrg * xScale;
      this.yo = this.yoOrg * yScale;
      return this;
   }

   public SpriteImage mirror() {
      return this.mirror(true);
   }

   public SpriteImage mirror(boolean mirrored) {
      this.mirror = mirrored;
      return this;
   }

   public SpriteImage setPos(double x, double y, double z) {
      this.xPos = this.x = x;
      this.yPos = this.y = y;
      this.zPos = this.z = z;
      this.w = this.wOrg;
      this.h = this.hOrg;
      this.xo = this.xoOrg;
      this.yo = this.yoOrg;
      this.brightness = 256;
      this.mirror = false;
      this.solidColor = 0;
      return this;
   }

   public SpriteImage move(Camera cam) {
      this.x = cam.cos * (this.xPos + cam.x) + cam.sin * (this.zPos + cam.z);
      this.z = -cam.cos * (this.zPos + cam.z) + cam.sin * (this.xPos + cam.x) + this.zo;
      this.y = this.yPos + cam.y;
      return this;
   }

   public void setBrightness(double d) {
      this.brightness = (int)d;
   }
}
