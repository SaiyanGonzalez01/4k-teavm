package com.mojang.chambered;

import com.mojang.chambered.entity.Player;
import com.mojang.chambered.gui.Gui;
import com.mojang.chambered.level.Level;
import com.mojang.chambered.particle.ParticleEngine;

public class World {
   public Level level;
   public Player player;
   public ParticleEngine particleEngine;
   public Gui gui;
   public int currentTick = 0;
   public double alpha = 0.0;
   public Party party;

   public World() {
      this.level = new Level(this);
      this.player = new Player(this);
      this.particleEngine = new ParticleEngine(this);
      this.party = new Party(this);
   }

   public void tick() {
      this.currentTick++;
      this.party.tick(this.currentTick);
      this.player.tick(this.currentTick);
      this.level.tick(this.currentTick);
      this.gui.tick(this.currentTick);
      this.particleEngine.tick(this.currentTick);
   }
}
