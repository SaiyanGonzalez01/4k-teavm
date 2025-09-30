package com.mojang.chambered;

import com.mojang.chambered.hero.Hero;

public class Party {
   public Hero[] heroes = new Hero[4];

   public Party(World world) {
      this.heroes = new Hero[]{
         new Hero(world, "Diana", "Mage", 0, 2, 46, 26, 8, 7),
         new Hero(world, "Green", "Ranger", 1, 0, 86, 55, 13, 14),
         new Hero(world, "Shera", "Warrior", 2, 3, 107, 46, 17, 15),
         new Hero(world, "Teron", "Priest", 3, 1, 31, 34, 5, 9)
      };
   }

   public void tick(int currentTick) {
      for (int i = 0; i < this.heroes.length; i++) {
         this.heroes[i].tick(currentTick);
      }
   }

   public void allDrainPower(int cost) {
      for (int i = 0; i < this.heroes.length; i++) {
         this.heroes[i].usePower(cost);
      }
   }

   public void hurt(int damage, int dir) {
      for (int i = 0; i < this.heroes.length; i++) {
         if ((i + 1 + dir) % 4 <= 1) {
            int dmg = (int)(Math.random() * damage + Math.random() * damage);
            if (dmg > 0) {
               this.heroes[i].hurt(dmg);
            }
         }
      }
   }

   public boolean hasLiveHero() {
      for (int i = 0; i < this.heroes.length; i++) {
         if (!this.heroes[i].isDead()) {
            return true;
         }
      }

      return false;
   }

   public boolean run(int runTime) {
      for (int i = 0; i < this.heroes.length; i++) {
         if (!this.heroes[i].isDead() && this.heroes[i].power == 0) {
            return false;
         }
      }

      if (runTime % 4 == 0) {
         for (int ix = 0; ix < this.heroes.length; ix++) {
            if (!this.heroes[ix].isDead()) {
               this.heroes[ix].run();
            }
         }
      }

      return true;
   }
}
