package com.mojang.chambered.hero;

import com.mojang.chambered.World;
import com.mojang.chambered.display.bitmap.BitmapCache;
import com.mojang.chambered.display.bitmap.SpriteImage;
import com.mojang.chambered.gui.particle.SpriteParticle;
import com.mojang.chambered.gui.particle.TextParticle;
import com.mojang.chambered.item.Food;
import com.mojang.chambered.item.ItemInstance;
import java.util.ArrayList;
import java.util.List;

public class Hero {
   private static final int FOOD_TICK_INTERVAL = 1000;
   private static final int WATER_TICK_INTERVAL = 800;
   private static final int POWER_REGEN_TICK_INTERVAL = 10;
   private static final int HP_REGEN_TICK_INTERVAL = 40;
   public static final int MAX_FOOD = 100;
   public static final int MAX_WATER = 100;
   public static final int SLOT_AMULET = 0;
   public static final int SLOT_HEAD = 1;
   public static final int SLOT_BACK = 2;
   public static final int SLOT_LEFT_HAND = 3;
   public static final int SLOT_UPPER_BODY = 4;
   public static final int SLOT_RIGHT_HAND = 5;
   public static final int SLOT_RING_1 = 6;
   public static final int SLOT_LOWER_BODY = 7;
   public static final int SLOT_UTIL_1 = 8;
   public static final int SLOT_RING_2 = 9;
   public static final int SLOT_FEET = 10;
   public static final int SLOT_UTIL_2 = 11;
   public String name;
   public int portraitId;
   private ItemInstance[] inventory = new ItemInstance[30];
   private List<InventoryListener> inventoryListeners = new ArrayList<>();
   private List<StatListener> statListeners = new ArrayList<>();
   public int hp;
   public int hpMax;
   public int power;
   public int powerMax;
   public int food;
   public int water;
   public int level = 1;
   public int xpLeft = 200;
   public int attack;
   public int defense;
   public int baseAttack;
   public int baseDefense;
   public String description;
   public int position;
   private int lockTime = 0;
   private World world;

   public Hero(World world, String name, String description, int position, int portraitId, int hpMax, int staminaMax, int baseAttack, int baseDefense) {
      this.world = world;
      this.name = name;
      this.description = description;
      this.position = position;
      this.portraitId = portraitId;
      this.hp = this.hpMax = hpMax;
      this.power = this.powerMax = staminaMax;
      this.attack = this.baseAttack = baseAttack;
      this.defense = this.baseDefense = baseDefense;
      this.food = 50;
      this.water = 80;
   }

   public ItemInstance getInventory(int slot) {
      return this.inventory[slot];
   }

   public void setInventory(int slot, ItemInstance itemInstance) {
      this.inventory[slot] = itemInstance;

      for (int i = 0; i < this.inventoryListeners.size(); i++) {
         this.inventoryListeners.get(i).slotChanged(this, slot);
      }
   }

   public void addStatListener(StatListener statListener) {
      this.statListeners.add(statListener);
   }

   public void addInventoryListener(InventoryListener inventoryListener) {
      this.inventoryListeners.add(inventoryListener);
   }

   private void statsChanged() {
      for (int i = 0; i < this.statListeners.size(); i++) {
         this.statListeners.get(i).statsChanged(this);
      }
   }

   public void tick(int currentTick) {
      if (this.lockTime > 0) {
         this.lockTime--;
         if (this.lockTime == 0) {
            this.statsChanged();
         }
      }

      if (currentTick % 40 == 0 && this.hp < this.hpMax && this.hp > 0) {
         this.hp++;
         this.statsChanged();
      }

      if (currentTick % 1000 == 0 && this.food > 0 && this.hp > 0) {
         this.food--;
         this.statsChanged();
      }

      if (currentTick % 10 == 0 && this.power < this.powerMax && this.hp > 0 && !this.world.player.isRunning()) {
         if (this.world.player.isWalking()) {
            if (currentTick / 10 % 4 == 0) {
               this.power++;
               this.statsChanged();
            }
         } else {
            this.power++;
            this.statsChanged();
         }
      }

      if (currentTick % 800 == 0 && this.water > 0 && this.hp > 0) {
         this.water--;
         this.statsChanged();
      }
   }

   public boolean eat(ItemInstance foodInstance, Food foodItem) {
      if (this.hp <= 0) {
         return false;
      } else {
         this.hp = this.hp + foodItem.hpGain;
         if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
         }

         this.power = this.power + foodItem.powerGain;
         if (this.power > this.powerMax) {
            this.power = this.powerMax;
         }

         this.food = this.food + foodItem.foodGain;
         if (this.food > 100) {
            this.food = 100;
         }

         this.water = this.water + foodItem.waterGain;
         if (this.water > 100) {
            this.water = 100;
         }

         this.statsChanged();
         return true;
      }
   }

   public boolean usePower(int cost) {
      if (this.power > 0 && this.hp > 0) {
         this.power -= cost;
         this.statsChanged();
         return true;
      } else {
         return false;
      }
   }

   public void lock(int lockTime) {
      this.lockTime = lockTime;
      this.statsChanged();
   }

   public boolean isLocked() {
      return this.lockTime > 0 || this.power <= 0 || this.hp <= 0;
   }

   public void hurt(int damage) {
      if (this.hp > 0) {
         SpriteImage blastSprite = new SpriteImage(BitmapCache.get("/gui/hud.png"), 0, 96, 24, 16, 0.5, 0.5, 1.0);
         this.world.gui.addParticleOnPortrait(this, new SpriteParticle(blastSprite, 0, 0), 2, 0);
         this.world.gui.addParticleOnPortrait(this, new TextParticle("" + damage, 16777215), 2, -4);
         this.hp -= damage;
         if (this.hp < 0) {
            this.hp = 0;
            this.power = 0;
         }

         this.statsChanged();
      }
   }

   public boolean isDead() {
      return this.hp <= 0;
   }

   public void run() {
      this.usePower(1);
   }
}
