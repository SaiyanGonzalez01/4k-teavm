package com.mojang.chambered.item;

import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.entity.Player;
import com.mojang.chambered.hero.Hero;
import com.mojang.chambered.particle.Particle;

public class Weapon extends Item {
   @Override
   public boolean useFrom(ItemInstance source, Hero hero, int slot) {
      if (slot != 3 && slot != 5) {
         return false;
      } else if (hero.isLocked()) {
         return false;
      } else if (!hero.usePower(6)) {
         return false;
      } else {
         hero.lock(30);
         SpriteImage sprite = new SpriteImage(BitmapCache.get("/gui/slash.png"), 0.5, 0.5, 16.0);
         sprite.yo = 0.0;
         Player player = source.world.player;
         double xa = Math.cos(player.rot) * (hero.position - 1.5);
         double za = Math.sin(player.rot) * (hero.position - 1.5);
         double x = player.x + xa * 6.0;
         double z = player.z + za * 6.0;
         Particle particle = new Particle(sprite, x, source.world.player.y - 50.0, z);
         particle.ya = 0.0;
         particle.xa = 0.0;
         particle.za = 0.0;
         particle.lifeSpan = 5;
         particle.gravity = 0.0;
         particle.sizeo = 0.0;
         particle.size = 0.5;
         source.world.particleEngine.addParticle(particle);
         source.world.level.hurtMonsters(player.x, player.z, player.getCamera(1.0));
         return true;
      }
   }
}
