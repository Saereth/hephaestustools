package com.titammods.hephaestus_tools.client.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ScalableElementScreen extends ElementScreen {

    public ScalableElementScreen(Identifier texture, int x, int y, int w, int h, int texW, int texH) {
        super(texture, x, y, w, h, texW, texH);
    }

    @Override
    public ScalableElementScreen move(int x, int y, int width, int height) {
        return new ScalableElementScreen(this.texture, x, y, width, height, this.texW, this.texH);
    }

    @Override
    public ScalableElementScreen shift(int xd, int yd) {
        return move(this.x + xd, this.y + yd, this.w, this.h);
    }

    public int drawScaledX(GuiGraphicsExtractor graphics, int xPos, int yPos, int width) {
        for (int i = 0; i < width / this.w; i++) {
            this.draw(graphics, xPos + i * this.w, yPos);
        }
        int remainder = width % this.w;
        if (remainder > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, xPos + width - remainder, yPos,
                    (float) this.x, (float) this.y, remainder, this.h, this.texW, this.texH);
        }
        return width;
    }

    public int drawScaledY(GuiGraphicsExtractor graphics, int xPos, int yPos, int height) {
        for (int i = 0; i < height / this.h; i++) {
            this.draw(graphics, xPos, yPos + i * this.h);
        }
        int remainder = height % this.h;
        if (remainder > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, xPos, yPos + height - remainder,
                    (float) this.x, (float) this.y, this.w, remainder, this.texW, this.texH);
        }
        return this.w;
    }

    public int drawScaled(GuiGraphicsExtractor graphics, int xPos, int yPos, int width, int height) {
        int full = height / this.h;
        for (int i = 0; i < full; i++) {
            this.drawScaledX(graphics, xPos, yPos + i * this.h, width);
        }
        yPos += full * this.h;
        int yRest = height % this.h;
        for (int i = 0; i < width / this.w; i++) {
            this.drawScaledY(graphics, xPos + i * this.w, yPos, yRest);
        }
        int remainder = width % this.w;
        if (remainder > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, texture, xPos + width - remainder, yPos,
                    (float) this.x, (float) this.y, remainder, yRest, this.texW, this.texH);
        }
        return width;
    }
}
