package com.mojang.chambered.display;

public class Pickable {
   public static Pickable currentPick;
   public static PickResult pickResult;

   public boolean isPicked() {
      return currentPick == this;
   }

   public boolean mouseDown(int xMouse, int yMouse, int button) {
      return false;
   }

   public void mouseClicked(int xMouse, int yMouse, int button) {
   }
}
