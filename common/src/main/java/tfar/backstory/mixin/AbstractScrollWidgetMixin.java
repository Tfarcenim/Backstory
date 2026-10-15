package tfar.backstory.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.backstory.AbstractScrollWidgetDuck;

@Mixin(AbstractScrollWidget.class)
public abstract class AbstractScrollWidgetMixin extends AbstractWidget implements AbstractScrollWidgetDuck{
    @Unique
    private boolean showBackground = true;

    public AbstractScrollWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
    }

    @Inject(method = "renderBorder",at = @At("HEAD"),cancellable = true)
    private void conditionalBorder(GuiGraphics guiGraphics, int x, int y, int width, int height, CallbackInfo ci) {
        if (!showBackground) {ci.cancel();}
    }
}
