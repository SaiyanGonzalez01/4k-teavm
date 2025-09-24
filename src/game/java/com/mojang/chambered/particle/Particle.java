package com.mojang.chambered.particle;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.SpriteImage;
import java.util.Random;

public class Particle {
   protected SpriteImage sprite;
   public double x;
   public double y;
   public double z;
   public double xa;
   public double ya;
   public double za;
   public double xo;
   public double yo;
   public double zo;
   public double gravity = 1.0;
   public double inertia = 0.99;
   public double size = 1.0;
   public double sizeo = 1.0;
   public static Random random = new Random();
   public int life = 0;
   public int lifeSpan = 0;
   public boolean scaleDeath = true;

   public Particle(SpriteImage sprite, double x, double y, double z) {
      this.sprite = sprite;
      double dir = random.nextDouble() * Math.PI * 2.0;
      double pow = random.nextDouble() * 4.0;
      this.xa = Math.sin(dir) * pow;
      this.ya = 0.0 - random.nextDouble() * 4.0;
      this.za = Math.cos(dir) * pow;
      this.x = this.xo = x;
      this.y = this.yo = y;
      this.z = this.zo = z;
      this.lifeSpan = random.nextInt(100);
   }

   public boolean tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.sizeo = this.size;
      this.x = this.x + this.xa;
      this.y = this.y + this.ya;
      this.z = this.z + this.za;
      this.ya = this.ya + this.gravity;
      this.xa = this.xa * this.inertia;
      this.ya = this.ya * this.inertia;
      this.za = this.za * this.inertia;
      return this.life++ < this.lifeSpan;
   }

   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      double size = (this.life + a) / this.lifeSpan;
      size = 1.0 - size * size * size * size;
      if (!this.scaleDeath) {
         size = 1.0;
      }

      size *= this.sizeo + (this.size - this.sizeo) * a;
      double xx = this.xo + (this.x - this.xo) * a;
      double yy = this.yo + (this.y - this.yo) * a;
      double zz = this.zo + (this.z - this.zo) * a;
      this.sprite.setPos(xx, yy, zz).scale(size, size);
      viewport.renderSprite(this.sprite.move(camera), null);
   }
}
