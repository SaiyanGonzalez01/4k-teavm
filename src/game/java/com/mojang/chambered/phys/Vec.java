package com.mojang.chambered.phys;

public class Vec {
   public double x = 0.0;
   public double y = 0.0;

   public Vec(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public double lengthSqr() {
      return this.x * this.x + this.y * this.y;
   }

   public double length() {
      return Math.sqrt(this.lengthSqr());
   }

   public void normalize() {
      this.scale(1.0 / this.length());
   }

   public double dot(Vec v) {
      return this.x * v.x + this.y * v.y;
   }

   public void add(Vec v) {
      this.x = this.x + v.x;
      this.y = this.y + v.y;
   }

   public void scale(double f) {
      this.x *= f;
      this.y *= f;
   }

   public double distanceSqr(Vec pos) {
      double xd = pos.x - this.x;
      double yd = pos.y - this.y;
      return xd * xd + yd * yd;
   }

   public double distance(Vec pos) {
      return Math.sqrt(this.distanceSqr(pos));
   }

   public void set(double x, double y) {
      this.x = x;
      this.y = y;
   }
}
