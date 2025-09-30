package com.mojang.chambered;

import com.mojang.chambered.display.PickResult;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, FocusListener {
   public int xMouse = -1000;
   public int yMouse = -1000;
   private boolean dragging = false;
   private int xDragStart;
   private int yDragStart;
   private int[] keyMappings = new int[256];
   private Chambered chambered;
   private World world;

   public InputHandler(Chambered chambered, World world) {
      this.chambered = chambered;
      this.world = world;
      chambered.addKeyListener(this);
      chambered.setFocusable(true);
      chambered.addMouseListener(this);
      chambered.addMouseMotionListener(this);
      chambered.addFocusListener(this);
      this.keyMappings[49] = -1;
      this.keyMappings[50] = -2;
      this.keyMappings[51] = -3;
      this.keyMappings[52] = -4;
      this.keyMappings[27] = -5;
      this.keyMappings[16] = 7;
      this.keyMappings[90] = 1;
      this.keyMappings[87] = 1;
      this.keyMappings[65] = 2;
      this.keyMappings[83] = 3;
      this.keyMappings[68] = 4;
      this.keyMappings[81] = 5;
      this.keyMappings[69] = 6;
      this.keyMappings[104] = 1;
      this.keyMappings[100] = 2;
      this.keyMappings[101] = 3;
      this.keyMappings[98] = 3;
      this.keyMappings[102] = 4;
      this.keyMappings[103] = 5;
      this.keyMappings[105] = 6;
      this.keyMappings[38] = 1;
      this.keyMappings[37] = 2;
      this.keyMappings[40] = 3;
      this.keyMappings[39] = 4;
      this.keyMappings[36] = 5;
      this.keyMappings[33] = 6;
   }

   @Override
   public void keyPressed(KeyEvent ke) {
      this.toggleKey(ke.getKeyCode(), true);
   }

   private void toggleKey(int keyCode, boolean pressed) {
      synchronized (this.chambered.inputLock) {
         if (keyCode >= 0 && keyCode <= this.keyMappings.length) {
            int key = this.keyMappings[keyCode];
            if (key > 0) {
               this.world.player.keys[key] = pressed;
            } else if (pressed) {
               this.world.gui.performAction(-key);
            }
         }
      }
   }

   @Override
   public void keyReleased(KeyEvent ke) {
      this.toggleKey(ke.getKeyCode(), false);
   }

   @Override
   public void keyTyped(KeyEvent ke) {
   }

   @Override
   public void mouseClicked(MouseEvent e) {
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
      synchronized (this.chambered.inputLock) {
         PickResult pickResult = this.chambered.pick(this.xMouse, this.yMouse);
         if (pickResult != null && pickResult.source != null) {
            pickResult.source.mouseClicked(pickResult.u, pickResult.v, e.getButton());
         }
      }
   }

   @Override
   public void mouseEntered(MouseEvent e) {
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
   }

   @Override
   public void mouseExited(MouseEvent e) {
      this.xMouse = -1000;
      this.yMouse = -1000;
   }

   @Override
   public void mousePressed(MouseEvent e) {
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
      this.chambered.requestFocus();
      this.chambered.requestFocusInWindow();
      synchronized (this.chambered.inputLock) {
         PickResult pickResult = this.chambered.pick(this.xMouse, this.yMouse);
         if (pickResult != null && pickResult.source != null && pickResult.source.mouseDown(pickResult.u, pickResult.v, e.getButton())) {
            return;
         }

         if (e.getButton() == 1 && this.chambered.checkThrow(this.xMouse, this.yMouse)) {
            return;
         }
      }

      this.dragging = true;
      this.xDragStart = this.xMouse;
      this.yDragStart = this.yMouse;
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      this.dragging = false;
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
      if (this.dragging) {
         int xDrag = this.xDragStart - this.xMouse;
         int yDrag = this.yDragStart - this.yMouse;
         this.xDragStart = this.xMouse;
         this.yDragStart = this.yMouse;
         synchronized (this.chambered.inputLock) {
            this.world.player.rot -= xDrag / 100.0;
         }
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      this.xMouse = e.getX() / this.chambered.scale;
      this.yMouse = e.getY() / this.chambered.scale;
   }

   @Override
   public void focusGained(FocusEvent arg0) {
      this.chambered.focused = true;
   }

   @Override
   public void focusLost(FocusEvent arg0) {
      this.chambered.focused = false;
      this.dragging = false;

      for (int i = 0; i < this.world.player.keys.length; i++) {
         this.world.player.keys[i] = false;
      }
   }
}
