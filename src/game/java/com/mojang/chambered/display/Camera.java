package com.mojang.chambered.display;

public class Camera {
   public double x;
   public double y;
   public double z;
   public double rot;
   public double cos;
   public double sin;

   public void setPos(double x, double y, double z, double nrot) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.rot = nrot;
      this.cos = Math.cos(this.rot);
      this.sin = Math.sin(this.rot);
   }

   public void moveRelative(double xd, double yd, double zd) {
      this.x = this.x - (this.sin * zd + this.cos * xd);
      this.y += yd;
      this.z = this.z + (this.cos * zd + this.sin * xd);
   }
}
