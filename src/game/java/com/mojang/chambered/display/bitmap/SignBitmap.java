package com.mojang.chambered.display.bitmap;

import com.mojang.chambered.display.Font;

public class SignBitmap extends Bitmap {
   public SignBitmap(int width, int height, String message) {
      super(width, height);
      this.blit(BitmapCache.get("/wall/rock.png"), 0, 0);
      Font font = Font.normal;
      int x = (128 - font.width(message)) / 2;
      int y = 32;
      font.drawShadow(this, message, x + 1, y, 16777215);
   }
}
