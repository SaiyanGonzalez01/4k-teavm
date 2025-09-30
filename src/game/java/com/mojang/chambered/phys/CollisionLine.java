package com.mojang.chambered.phys;

public class CollisionLine extends Collideable {
   public Vec normal;
   public Vec angle1;
   public Vec angle2;
   public double c;
   public double c1;
   public double c2;
   public Vec v0;
   public Vec v1;
   public double length;
   public CollisionSphere sphere0;
   public CollisionSphere sphere1;
   private Vec n = new Vec(0.0, 0.0);

   public CollisionLine(double x0, double y0, double x1, double y1) {
      this.setLocation(x0, y0, x1, y1);
   }

   public void setLocation(double x0, double y0, double x1, double y1) {
      this.v0 = new Vec(x0, y0);
      this.v1 = new Vec(x1, y1);
      this.sphere0 = new CollisionSphere(this.v0, 0.0);
      this.sphere1 = new CollisionSphere(this.v1, 0.0);
      this.angle1 = new Vec(x1 - x0, y1 - y0);
      this.length = this.angle1.length();
      this.angle1.normalize();
      this.angle2 = new Vec(x1 - x0, y1 - y0);
      this.angle2.normalize();
      this.normal = new Vec(this.angle1.y, -this.angle1.x);
      this.c = this.normal.dot(this.v0);
      this.c1 = this.angle1.dot(this.v0);
      this.c2 = this.angle2.dot(this.v1);
   }

   public boolean isFront(double x, double y) {
      return this.normal.x * x + this.normal.y * y - this.c > 0.0;
   }

   @Override
   public double getCollisionTime(CollisionSphere ball, Vec v0, Vec v1) {
      double before = this.normal.x * v0.x + this.normal.y * v0.y - this.c;
      double after = this.normal.x * v1.x + this.normal.y * v1.y - this.c;
      if (before > 0.0) {
         before -= ball.radius;
         after -= ball.radius;
         if (after > 0.0) {
            return 999.0;
         }
      } else {
         before += ball.radius;
         after += ball.radius;
         if (after < 0.0) {
            return 999.0;
         }
      }

      double dist = before - after;
      if (dist != 0.0) {
         dist = before / (before - after);
      } else {
         dist = 0.5;
      }

      double dx = (v1.x - v0.x) * dist + v0.x;
      double dy = (v1.y - v0.y) * dist + v0.y;
      double sd = this.angle1.x * dx + this.angle1.y * dy - this.c1;
      return !(sd < 0.0) && !(sd > this.length) ? dist : 999.0;
   }

   @Override
   public void collide(CollisionSphere b) {
      this.n.set(this.normal.x, this.normal.y);
      double a1 = 0.0;
      double a2 = b.motion.dot(this.n);
      double optimizedP = a1 - a2 + (a1 - a2) * 0.01;
      this.n.scale(optimizedP);
      b.motion.add(new Vec(this.n.x, this.n.y));
   }

   public double distanceTo(double x, double y) {
      return this.normal.x * x + this.normal.y * y - this.c;
   }

   public double sideDistanceTo(double x, double y) {
      return this.angle1.x * x + this.angle1.y * y - this.c1;
   }

   @Override
   public boolean isInside(CollisionSphere p) {
      double x = p.pos.x;
      double y = p.pos.y;
      double d = this.normal.x * x + this.normal.y * y - this.c;
      double r = p.radius;
      if (!(d > r) && !(d < -r)) {
         double sd = this.angle1.x * x + this.angle1.y * y - this.c1;
         return !(sd < 0.0) && !(sd > this.length);
      } else {
         return false;
      }
   }
}
