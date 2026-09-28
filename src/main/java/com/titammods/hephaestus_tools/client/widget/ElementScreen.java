package com.titammods.hephaestus_tools.client.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ElementScreen {

    public Identifier texture;
    public final int x;
    public final int y;
    public final int w;
    public final int h;
    public final int texW;
    public final int texH;

    public ElementScreen(Identifier texture, int x, int y, int w, int h, int texW, int texH) {
        this.texture = texture;
        this.x = x; this.y = y;
        this.w = w; this.h = h;
        this.texW = texW; this.texH = texH;
    }

    public ElementScreen move(int x, int y, int width, int height) {
        return new ElementScreen(this.texture, x, y, width, height, this.texW, this.texH);
    }

    public ElementScreen shift(int xd, int yd) {
        return move(x + xd, y + yd, this.w, this.h);
    }

    public void draw(GuiGraphicsExtractor graphics, int xPos, int yPos) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, xPos, yPos,
                (float) this.x, (float) this.y, this.w, this.h, this.texW, this.texH);
    }
}
