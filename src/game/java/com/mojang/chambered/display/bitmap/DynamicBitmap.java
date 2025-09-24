package com.mojang.chambered.display.bitmap;

import com.mojang.chambered.display.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DynamicBitmap extends Bitmap {
   private DateFormat df = new SimpleDateFormat("hh:mm:ss");

   public DynamicBitmap(int width, int height) {
      super(width, height);
   }

   @Override
   public int[] getPixels() {
      this.blit(BitmapCache.get("/wall/rock.png"), 0, 0);
      Font font = Font.normal;
      font.drawShadow(this, "Dynamic texture!", 0, 32, 16777215);
      font.drawShadow(this, this.df.format(new Date()), 0, 40, 16777215);
      return this.pixels;
   }
}
