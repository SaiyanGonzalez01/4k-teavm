package com.mojang.chambered.entity;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Camera;
import com.mojang.chambered.item.ItemInstance;

public class Player extends Mob {
   public static final int KEY_MOVE_FORWARD = 1;
   public static final int KEY_MOVE_LEFT = 2;
   public static final int KEY_MOVE_BACK = 3;
   public static final int KEY_MOVE_RIGHT = 4;
   public static final int KEY_TURN_LEFT = 5;
   public static final int KEY_TURN_RIGHT = 6;
   public static final int KEY_RUN = 7;
   public boolean[] keys = new boolean[16];
   private Camera camera = new Camera();
   private World world;
   public double xa;
   public double ya;
   public double za;
   public double rota;
   public double heightA;
   public double xLast;
   public double yLast;
   public double zLast;
   public double rotLast;
   public double bobSpeed;
   public double bobSpeedA;
   public double bobSpeedLast;
   public double bobSpeedLastALast;
   private int walkTime = 0;
   private int runTime = 0;
   private boolean isWalking = false;
   public double height;
   public double heightLast;

   public Player(World world) {
      super(24.0);
      this.collideType = 4L;
      this.x = world.level.wallWidth;
      this.y = 0.0;
      this.z = world.level.wallWidth;
      this.setPos(this.x, this.y, this.z);
      this.rot = Math.PI * 3.0 / 4.0;
      this.xLast = this.x;
      this.yLast = this.y;
      this.zLast = this.z;
      this.rotLast = this.rot;
      this.heightLast = this.height = 63.966;
      this.heightA = 0.0;
      this.world = world;
   }

   public boolean tick(int currentTick) {
      boolean alive = this.world.party.hasLiveHero();
      this.heightLast = this.height;
      this.xLast = this.x;
      this.yLast = this.y;
      this.zLast = this.z;
      this.rotLast = this.rot;
      this.bobSpeedLast = this.bobSpeed;
      this.bobSpeedLastALast = this.bobSpeedA;
      double moveSpeed = 6.0;
      double turnSpeed = 0.07;
      double moveInertia = 0.1;
      double turnInertia = 0.2;
      double xd = 0.0;
      double zd = 0.0;
      if (alive) {
         if (this.keys[1]) {
            zd++;
         }

         if (this.keys[2]) {
            xd--;
         }

         if (this.keys[4]) {
            xd++;
         }

         if (this.keys[3]) {
            zd--;
         }

         this.heightA = 0.0;
      } else {
         this.height = this.height + this.heightA;
         this.heightA -= 0.4;
         if (this.height <= 8.0) {
            this.height = 8.0;
            this.heightA *= -0.2;
            if (this.heightA < 0.4) {
               this.heightA = 0.0;
            }
         }
      }

      this.bobSpeed = this.bobSpeed + this.bobSpeedA;
      this.bobSpeedA *= 0.3;
      this.isWalking = false;
      if (xd != 0.0 || zd != 0.0) {
         double d = Math.sqrt(xd * xd + zd * zd);
         this.bobSpeedA += 0.18;
         double speed = moveSpeed;
         if (this.keys[7] && this.world.party.run(this.runTime)) {
            this.runTime++;
            speed = moveSpeed * 1.5;
            this.bobSpeedA += 0.08;
         } else {
            this.runTime = 0;
         }

         this.move(xd / d * speed, zd / d * speed);
         this.walkTime++;
         this.isWalking = true;
      }

      if (this.keys[5]) {
         this.rota -= turnSpeed;
      }

      if (this.keys[6]) {
         this.rota += turnSpeed;
      }

      this.attemptMove(this.world.level, this.xa, this.za);
      this.rot = this.rot + this.rota;
      this.xa *= moveInertia;
      this.ya *= moveInertia;
      this.za *= moveInertia;
      this.rota *= turnInertia;
      return true;
   }

   public Camera getCamera(double alpha) {
      double xx = this.xLast + (this.x - this.xLast) * alpha;
      double zz = this.zLast + (this.z - this.zLast) * alpha;
      double rr = this.rotLast + (this.rot - this.rotLast) * alpha;
      this.camera.setPos(-xx, 0.0, -zz, rr);
      double h = this.heightLast + (this.height - this.heightLast) * alpha;
      double bs = this.bobSpeedLastALast + (this.bobSpeedA - this.bobSpeedLastALast) * alpha;
      double yo = Math.abs(Math.sin(this.bobSpeedLast + (this.bobSpeed - this.bobSpeedLast) * alpha) * 10.0 * bs);
      this.camera.moveRelative(0.0, h + yo, -16.0);
      return this.camera;
   }

   public void move(double xd, double zd) {
      double sin = Math.sin(this.rot);
      double cos = Math.cos(this.rot);
      this.xa += cos * xd + sin * zd;
      this.za += -cos * zd + sin * xd;
   }

   public boolean isWalking() {
      return this.isWalking;
   }

   public int getDirTo(double x, double z) {
      double xd = this.x - x;
      double zd = this.z - z;
      double cos = Math.cos(this.rot);
      double sin = Math.sin(this.rot);
      double d = Math.sqrt(xd * xd + zd * zd);
      double xx = (cos * xd + sin * zd) / d;
      double zz = (-cos * zd + sin * xd) / d;
      int dir = 0;
      byte var20;
      if (zz > 0.7) {
         var20 = 0;
      } else if (zz < -0.7) {
         var20 = 2;
      } else if (xx < 0.0) {
         var20 = 1;
      } else {
         var20 = 3;
      }

      return var20;
   }

   public void throwItem(ItemInstance carried, double rotOffs) {
      this.world.level.addMob(new ThrownItem(this.world, carried, this.x, this.y, this.z, this.rot + rotOffs, 20.0));
   }

   public boolean isRunning() {
      return this.runTime > 0;
   }
}
