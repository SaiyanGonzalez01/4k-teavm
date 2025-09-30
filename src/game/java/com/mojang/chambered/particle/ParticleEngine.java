package com.mojang.chambered.particle;

import com.mojang.chambered.World;
import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.Renderable;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.level.Level;
import java.util.ArrayList;
import java.util.List;

public class ParticleEngine implements Renderable {
   public List<Particle> particles = new ArrayList<>();
   private World world;
   private SpriteImage sprite;

   public ParticleEngine(World world) {
      this.world = world;
      this.sprite = new SpriteImage(BitmapCache.get("/particle/ball.png"), -8.0);
   }

   public void tick(int currentTick) {
      for (int i = 0; i < this.particles.size(); i++) {
         boolean alive = this.particles.get(i).tick();
         if (!alive) {
            this.particles.remove(i--);
         }
      }
   }

   public void testEmittor(int tick) {
      Level level = this.world.level;
      double dir = tick / 60.0 * Math.PI * 2.0;
      double pow = Math.sin(tick * 0.02) * 0.5 + 0.5;
      pow *= pow;
      pow *= 60.0;
      int a = (int)pow;
      pow *= 0.5;

      for (int i = 0; i < a; i++) {
         double xCenter = level.wallWidth * 13.0;
         double zCenter = level.wallWidth * 3.0;
         BounceParticle bounceParticle = new BounceParticle(level, this.sprite, xCenter, -i * 64 / a, zCenter);
         bounceParticle.xa *= 0.1;
         bounceParticle.ya *= 0.1;
         bounceParticle.za *= 0.1;
         bounceParticle.xa = bounceParticle.xa + Math.sin(dir) * pow;
         bounceParticle.ya += 0.0;
         bounceParticle.za = bounceParticle.za + Math.cos(dir) * pow;
         this.particles.add(bounceParticle);
      }
   }

   @Override
   public void render(Viewport viewport, Camera camera, int currentTick, double a) {
      for (int i = 0; i < this.particles.size(); i++) {
         this.particles.get(i).render(viewport, camera, currentTick, a);
      }
   }

   public void addParticle(Particle particle) {
      this.particles.add(particle);
   }
}
