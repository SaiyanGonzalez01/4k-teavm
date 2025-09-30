package com.mojang.chambered.gui;

import com.mojang.chambered.World;
import com.mojang.chambered.display.PickResult;
import com.mojang.chambered.display.Pickable;
import com.mojang.chambered.display.bitmap.Bitmap;
import java.util.ArrayList;
import java.util.List;

public class Component extends Pickable {
   private Component parent;
   private List<Component> children = new ArrayList<>();
   private boolean dirty = true;
   private boolean hasDirtyChildren = true;
   public int x;
   public int y;
   public int width;
   public int height;
   public World world;

   public Component(World world, int x, int y, int width, int height) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void addChild(Component child) {
      child.parent = this;
      this.children.add(child);
      this.setDirty();
   }

   public void removeChild(Component child) {
      child.parent = null;
      this.children.remove(child);
      this.setDirty();
   }

   public void setDirty() {
      this.dirty = true;
      if (this.parent != null) {
         this.parent.setHasDirtyChildren();
      }
   }

   private void setHasDirtyChildren() {
      this.hasDirtyChildren = true;
      if (this.parent != null) {
         this.parent.setHasDirtyChildren();
      }
   }

   public void renderDirtyChildren(Bitmap target) {
      if (this.hasDirtyChildren) {
         for (int i = 0; i < this.children.size(); i++) {
            Component child = this.children.get(i);
            if (child.dirty) {
               child.render(target);
               child.dirty = false;
            } else if (child.hasDirtyChildren) {
               child.renderDirtyChildren(target);
            }
         }

         this.hasDirtyChildren = false;
      }
   }

   public void tick(int currentTick) {
      for (int i = 0; i < this.children.size(); i++) {
         Component child = this.children.get(i);
         child.tick(currentTick);
      }
   }

   public void renderChildren(Bitmap target) {
      for (int i = 0; i < this.children.size(); i++) {
         Component child = this.children.get(i);
         child.render(target);
         child.dirty = false;
      }

      this.hasDirtyChildren = false;
   }

   public void render(Bitmap target) {
   }

   public final PickResult pick(int x, int y) {
      if (x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height) {
         for (int i = 0; i < this.children.size(); i++) {
            Component child = this.children.get(i);
            PickResult result = child.pick(x, y);
            if (result != null) {
               return result;
            }
         }

         Component component = this.getComponentAt(x, y);
         return component != null ? new PickResult(component, x, y) : null;
      } else {
         return null;
      }
   }

   public Component getComponentAt(int x, int y) {
      return this;
   }

   @Override
   public boolean mouseDown(int xMouse, int yMouse, int button) {
      return true;
   }
}
