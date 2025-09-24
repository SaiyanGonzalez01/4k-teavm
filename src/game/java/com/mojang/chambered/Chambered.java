package com.mojang.chambered;

import com.mojang.chambered.display.Camera;
import com.mojang.chambered.display.LevelRenderer;
import com.mojang.chambered.display.PickResult;
import com.mojang.chambered.display.Pickable;
import com.mojang.chambered.display.Viewport;
import com.mojang.chambered.gui.CursorManager;
import com.mojang.chambered.gui.Gui;
import com.mojang.chambered.util.GameTimer;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Chambered extends Canvas implements Runnable {
   private Viewport viewport;
   public int width;
   public int height;
   public int scale;
   public int guiScale;
   private World world;
   private Image image;
   private Thread thread;
   private boolean stopped;
   public boolean focused = false;
   public Object inputLock = new Object();
   private InputHandler inputHandler;
   private GameTimer gameTimer = new GameTimer(30);
   public LevelRenderer levelRenderer;

   public Chambered(int width, int height, int scale, int guiScale) {
      this.width = width;
      this.height = height;
      this.scale = scale;
      this.guiScale = guiScale;
      this.world = new World();
      this.viewport = new Viewport(width, height - 72 * guiScale);
      this.world.gui = new Gui(this.world, new CursorManager(this), width / guiScale, height / guiScale);
      this.levelRenderer = new LevelRenderer(this.world.level);
      this.setFocusable(true);
      this.setFocusTraversalKeysEnabled(false);
      this.setEnabled(true);
      this.requestFocus();
      this.requestFocusInWindow();
      this.setBackground(Color.BLACK);
      this.setPreferredSize(new Dimension(width * scale, height * scale));
      this.setMinimumSize(new Dimension(width * scale, height * scale));
      this.setMaximumSize(new Dimension(width * scale, height * scale));
      this.inputHandler = new InputHandler(this, this.world);
   }

   @Override
   public void run() {
      while (!this.stopped) {
         int ticks = this.gameTimer.advanceTime();
         synchronized (this.inputLock) {
            for (int i = 0; i < ticks; i++) {
               this.world.tick();
            }

            this.world.alpha = this.gameTimer.alpha;
            this.render(this.gameTimer.alpha);
         }

         try {
            if (this.focused) {
               Thread.sleep(2L);
            } else {
               Thread.sleep(50L);
            }
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }
      }
   }

   public PickResult pick(int xMouse, int yMouse) {
      double alpha = this.gameTimer.alpha;
      PickResult guiPick = this.world.gui.pick(xMouse, yMouse);
      return guiPick != null
         ? guiPick
         : this.levelRenderer
            .pick(
               this.inputHandler.xMouse, this.inputHandler.yMouse, this.viewport, this.world.player.getCamera(alpha), this.world.currentTick, this.world.alpha
            );
   }

   public boolean checkThrow(int x, int y) {
      if (y < this.viewport.height / 2 && this.world.gui.carried != null) {
         double rotOffs = this.viewport.getAngle(x);
         this.world.player.throwItem(this.world.gui.carried, rotOffs);
         this.world.gui.carried = null;
         return true;
      } else {
         return false;
      }
   }

   public void render(double alpha) {
      if (this.image == null) {
         this.image = this.createImage(this.width, this.height);
      }

      Pickable.currentPick = null;
      PickResult pickResult = this.levelRenderer
         .pick(this.inputHandler.xMouse, this.inputHandler.yMouse, this.viewport, this.world.player.getCamera(alpha), this.world.currentTick, alpha);
      if (pickResult != null && pickResult.source != null) {
         Pickable.currentPick = pickResult.source;
      }

      Camera playerCamera = this.world.player.getCamera(alpha);
      this.levelRenderer.render(this.viewport, playerCamera, this.world.currentTick, alpha);
      this.world.particleEngine.render(this.viewport, playerCamera, this.world.currentTick, alpha);
      this.world.gui.render(this.inputHandler.xMouse / this.guiScale, this.inputHandler.yMouse / this.guiScale, this.world.currentTick, alpha);
      this.world.gui.renderFps(this.gameTimer.fps);
      Graphics g = this.image.getGraphics();
      BufferedImage viewportImage = this.viewport.render();
      g.drawImage(viewportImage, 0, 0, null);
      if (this.guiScale == 1) {
         g.drawImage(this.world.gui.getImage(), 0, 0, null);
      } else {
         g.drawImage(this.world.gui.getImage(), 0, 0, this.width * this.guiScale, this.height * this.guiScale, 0, 0, this.width, this.height, null);
      }

      g.dispose();
      g = this.getGraphics();
      if (this.scale == 1) {
         g.drawImage(this.image, 0, 0, null);
      } else {
         g.drawImage(this.image, 0, 0, this.width * this.scale, this.height * this.scale, 0, 0, this.width, this.height, null);
      }

      g.dispose();
   }

   @Override
   public void paint(Graphics arg0) {
   }

   @Override
   public void update(Graphics arg0) {
   }

   public synchronized void start() {
      if (this.thread != null) {
         throw new IllegalStateException("Already running!");
      } else {
         this.thread = new Thread(this);
         this.thread.start();
      }
   }

   public synchronized void stop() {
      if (this.thread == null) {
         throw new IllegalStateException("Not running!");
      } else {
         this.stopped = true;

         try {
            this.thread.join();
            this.thread = null;
         } catch (InterruptedException var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      final Chambered chambered = new Chambered(320, 240, 2, 1);
      JFrame frame = new JFrame("Chambered test");
      frame.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent arg0) {
            chambered.stop();
            System.exit(0);
         }
      });
      frame.add(chambered);
      frame.pack();
      frame.setResizable(false);
      frame.setVisible(true);
      chambered.start();
   }
}
