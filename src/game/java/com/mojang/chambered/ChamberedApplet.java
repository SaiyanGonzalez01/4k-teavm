package com.mojang.chambered;

import javax.swing.JApplet;

public class ChamberedApplet extends JApplet {
   private Chambered chambered;

   @Override
   public void init() {
      this.chambered = new Chambered(320, 240, 2, 1);
      this.add(this.chambered);
   }

   @Override
   public void start() {
      this.chambered.start();
   }

   @Override
   public void stop() {
      this.chambered.stop();
   }
}
