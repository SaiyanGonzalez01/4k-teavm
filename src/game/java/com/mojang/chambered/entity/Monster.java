package com.mojang.chambered.entity;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.item.Item;
import com.mojang.chambered.item.ItemInstance;
import com.mojang.chambered.particle.BounceParticle;
import java.util.Random;

public class Monster extends Mob {
   public static final int HURT_DURATION = 6;
   private World world;
   public double xa;
   public double za;
   public double rota;
   public double xLast;
   public double yLast;
   public double zLast;
   public double rotLast;
   private Random random = new Random();
   private SpriteImage walkImage;
   private SpriteImage attackImage;
   private int tickOffs;
   private boolean alive = true;
   public int hurtTime = 0;
   public int hp = 10;
   public int attackTime = 0;

   public Monster(World world, SpriteImage walkImage, SpriteImage attackImage, double x, double y, double z) {
      super(24.0);
      this.collideType = 2L;
      this.world = world;
      this.walkImage = walkImage;
      this.attackImage = attackImage;
      this.setPos(x, y, z);
      this.xLast = x;
      this.yLast = y;
      this.zLast = z;
      this.rot = -Math.PI / 4;
      this.tickOffs = (int)(Math.random() * 10.0);
   }

   @Override
   public boolean tick() {
      if (this.attackTime > 0) {
         this.attackTime--;
      } else {
         double xd = this.world.player.x - this.x;
         double zd = this.world.player.z - this.z;
         int attackDistance = 80;
         double d = xd * xd + zd * zd;
         if (d < attackDistance * attackDistance && Math.random() < 0.05) {
            this.attackTime = 10;
            int dir = this.world.player.getDirTo(this.x, this.z);
            this.world.party.hurt(4, dir);
         }
      }

      this.xLast = this.x;
      this.yLast = this.y;
      this.zLast = this.z;
      this.rotLast = this.rot;
      double moveSpeed = 2.0;
      double turnSpeed = 0.07;
      double moveInertia = 0.1;
      double turnInertia = 0.2;
      double xdx = 0.0;
      double zdx = 0.0;
      zdx++;
      if (this.attackTime == 0 && (xdx != 0.0 || zdx != 0.0)) {
         double dx = Math.sqrt(xdx * xdx + zdx * zdx);
         this.move(xdx / dx * moveSpeed, zdx / dx * moveSpeed);
      }

      if (this.random.nextInt(10) < 5) {
         this.rota -= turnSpeed;
      }

      if (this.random.nextInt(10) < 8) {
         this.rota += turnSpeed;
      }

      this.attemptMove(this.world.level, this.xa, this.za);
      this.rot = this.rot + this.rota;
      this.xa *= moveInertia;
      this.za *= moveInertia;
      this.rota *= turnInertia;
      if (this.hurtTime > 0) {
         this.hurtTime--;
      }

      return this.alive;
   }

   public void move(double xd, double zd) {
      double sin = Math.sin(this.rot);
      double cos = Math.cos(this.rot);
      this.xa += cos * xd + sin * zd;
      this.za += -cos * zd + sin * zd;
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      double xx = this.xLast + (this.x - this.xLast) * a;
      double yy = this.yLast + (this.y - this.yLast) * a;
      double zz = this.zLast + (this.z - this.zLast) * a;
      SpriteImage img = (this.attackTime > 0 ? this.attackImage : this.walkImage).setPos(xx, yy, zz);
      if (((currentTick + this.tickOffs) / 10 & 1) == 0) {
         img = img.mirror();
      }

      if (this.hurtTime > 0) {
         img.solidColor = -1;
      }

      viewport.renderSprite(img.move(camera), this);
   }

   public void hurt() {
      if (this.hurtTime <= 0) {
         this.hp--;
         this.hurtTime = 6;
         int bloodParticles = (int)(Math.random() * Math.random() * 5.0);
         if (this.hp == 0) {
            int coinCount = (int)(Math.random() * Math.random() * 7.0);

            for (int i = 0; i < coinCount; i++) {
               ItemInstance item = new ItemInstance(this.world, Item.goldCoin);
               this.world.level.addMob(new ThrownItem(this.world, item, this.x, this.y, this.z, Math.random() * Math.PI * 2.0, Math.random() * 5.0));
            }

            this.alive = false;
            bloodParticles = 100;
         }

         for (int i = 0; i < bloodParticles; i++) {
            int p = (int)(Math.random() * 16.0);
            SpriteImage sprite = new SpriteImage(BitmapCache.get("/particle/blood.png"), p % 4 * 8, p / 4 * 8, 8, 8);
            BounceParticle particle = new BounceParticle(
               this.world.level, sprite, this.x + (Math.random() * 8.0 - 4.0), this.y - (Math.random() * 60.0 + 10.0), this.z + (Math.random() * 8.0 - 4.0)
            );
            particle.bounce = 0.2;
            particle.gravity = 0.5;
            particle.slideInertia = 0.1;
            particle.radius = 4;
            particle.size = particle.sizeo = Math.random() * 0.75 + 0.25;
            particle.lifeSpan = (int)(Math.random() * Math.random() * 100.0 + 5.0);
            this.world.particleEngine.addParticle(particle);
         }
      }
   }

   public boolean isInFront(Camera camera, double angle) {
      SpriteImage img = this.walkImage.setPos(this.x, this.y, this.z).move(camera);
      double x = img.x / img.z;
      return x > -angle && x < angle;
   }
}
