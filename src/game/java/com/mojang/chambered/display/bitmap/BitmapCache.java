package com.mojang.chambered.display.bitmap;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class BitmapCache {
   public static final BitmapCache instance = new BitmapCache();
   private Map<String, Bitmap> textures = new HashMap<>();

   private BitmapCache() {
   }

   public static Bitmap get(String name) {
      return instance.getTexture(name);
   }

   public Bitmap getTexture(String name) {
      if (this.textures.containsKey(name)) {
         return this.textures.get(name);
      } else {
         Bitmap texture = null;

         try {
            texture = load(name);
         } catch (Exception var4) {
            System.out.println("Failed to load " + name + ":");
            var4.printStackTrace();
         }

         this.textures.put(name, texture);
         return texture;
      }
   }

   private static Bitmap load(String resourceName) {
      try {
         BufferedImage image = ImageIO.read(AbstractBitmap.class.getResource(resourceName));
         int width = image.getWidth();
         int height = image.getHeight();
         int[] pixels = new int[width * height];
         image.getRGB(0, 0, width, height, pixels, 0, width);
         return new Bitmap(pixels, width, height);
      } catch (Exception var6) {
         throw new RuntimeException(var6);
      }
   }
}
