package com.mojang.chambered.item;

import com.mojang.chambered.hero.Hero;

public class Food extends Item {
   public int hpGain = 1;
   public int powerGain = 4;
   public int foodGain = 10;
   public int waterGain = 3;

   @Override
   public boolean consume(ItemInstance source, Hero hero) {
      return hero.eat(source, this);
   }
}
