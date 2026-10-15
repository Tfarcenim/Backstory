package tfar.backstory.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.backstory.MultiLineEditBoxDuck;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractScrollWidget implements MultiLineEditBoxDuck {

    @Unique
    private int textColor = 0xffe0e0e0;
    @Unique
    private int cursorColor = 0xffe0e0e0;
    @Unique
    private boolean shadow = true;
    @Unique
    private boolean showBackground = true;

    public MultiLineEditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void setTextColor(int color) {
        this.textColor = color;
    }

    @Override
    public void setCursorColor(int color) {
        this.cursorColor = color;
    }

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Override
    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    @Redirect(method = "renderContents",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private int renderWithHighlight(GuiGraphics instance, Font font, String text, int x, int y, int color) {
        return instance.drawString(font, text, x, y, textColor,shadow);
    }


    @Override
    protected void renderBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
       if (showBackground) {
           super.renderBorder(guiGraphics, x, y, width, height);
       }
    }
}
