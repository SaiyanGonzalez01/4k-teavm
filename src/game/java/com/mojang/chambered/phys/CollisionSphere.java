package com.mojang.chambered.phys;

import java.util.List;

public class CollisionSphere extends Collideable {
   public static final double NO_COLLISION = 99999.0;
   public Vec pos;
   public Vec lastPos;
   public Vec motion;
   public double radius = 0.0;
   public double mass = 100000.0;
   public boolean fixedPos = true;
   public boolean hasCollided = false;
   private Vec n = new Vec(0.0, 0.0);

   public CollisionSphere(double x, double y, double radius) {
      this.pos = new Vec(x, y);
      this.lastPos = new Vec(x, y);
      this.motion = new Vec(0.0, 0.0);
      this.radius = radius;
   }

   public CollisionSphere(Vec v0, double radius) {
      this(v0.x, v0.y, radius);
      this.pos = v0;
   }

   @Override
   public boolean isInside(CollisionSphere p) {
      if (p == this) {
         return false;
      } else {
         double xd = p.pos.x - this.pos.x;
         double yd = p.pos.y - this.pos.y;
         double d = xd * xd + yd * yd;
         double rs = this.radius + p.radius;
         return d < rs * rs;
      }
   }

   @Override
   public double getCollisionTime(CollisionSphere ball, Vec v0, Vec v1) {
      if (this.isInside(ball)) {
         return 99999.0;
      } else if (ball == this) {
         return 99999.0;
      } else {
         double x1 = ball.pos.x - this.pos.x;
         double y1 = ball.pos.y - this.pos.y;
         double xm = ball.motion.x - this.motion.x;
         double ym = ball.motion.y - this.motion.y;
         if (x1 * xm + y1 * ym >= 0.0) {
            return 99999.0;
         } else {
            double r = ball.radius + this.radius;
            double a = xm * xm + ym * ym;
            double b = (xm * x1 + ym * y1) * 2.0;
            double c = x1 * x1 + y1 * y1 - r * r;
            double xx = b * b - 4.0 * a * c;
            if (xx >= 0.0) {
               double xsqrt = Math.sqrt(xx);
               double asqrt = a * 2.0;
               double u0 = (-b - xsqrt) / asqrt;
               double u1 = (-b + xsqrt) / asqrt;
               if (u1 < u0) {
                  u0 = u1;
               }

               return u0;
            } else {
               return 99999.0;
            }
         }
      }
   }

   @Override
   public void collide(CollisionSphere b) {
      if (b != this) {
         this.n.set(this.pos.x - b.pos.x, this.pos.y - b.pos.y);
         this.n.normalize();
         double a1 = 0.0;
         double a2 = b.motion.dot(this.n);
         double optimizedP = a1 - a2 + (a1 - a2) * 0.01;
         this.n.scale(optimizedP);
         b.motion.add(new Vec(this.n.x, this.n.y));
      }
   }

   public Vec move(double x0, double y0, double x1, double y1, List<Collideable> walls) {
      this.hasCollided = false;
      double remainingTime = 1.0;
      Vec before = new Vec(x0, y0);
      Vec after = new Vec(x1, y1);
      this.pos.set(x0, y0);
      this.motion.set(x1 - x0, y1 - y0);

      while (remainingTime > 0.1) {
         double shortestTime = remainingTime;
         Collideable collided = null;

         for (int i = 0; i < walls.size(); i++) {
            Collideable wall = walls.get(i);
            if (!wall.wasStuck) {
               double time = wall.getCollisionTime(this, before, after);
               if (time < shortestTime) {
                  shortestTime = time;
                  collided = wall;
               }
            }
         }

         if (shortestTime < 0.0) {
            shortestTime = 0.0;
         }

         double orgShortestTime = shortestTime;
         boolean okPos = false;

         while (!okPos) {
            this.pos.set(this.motion.x, this.motion.y);
            this.pos.scale(shortestTime * 0.99);
            this.pos.add(before);
            okPos = true;

            for (int ix = 0; ix < walls.size() && okPos; ix++) {
               Collideable wall = walls.get(ix);
               if (!wall.wasStuck && wall.isInside(this)) {
                  okPos = false;
               }
            }

            if (!okPos) {
               shortestTime -= 0.01;
               if (shortestTime < 0.0) {
                  shortestTime = 0.0;
                  okPos = true;
               }
            }
         }

         remainingTime -= orgShortestTime;
         remainingTime -= 0.1;
         if (collided != null) {
            this.hasCollided = true;
            collided.collide(this);
         }

         before.set(this.pos.x, this.pos.y);
         after.set(this.pos.x + this.motion.x, this.pos.y + this.motion.y);
      }

      return this.pos;
   }
}
