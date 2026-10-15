package tfar.backstory.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.backstory.MultiLineTextWidgetDuck;

@Mixin(MultiLineTextWidget.class)
public abstract class MultiLineTextWidgetMixin  implements MultiLineTextWidgetDuck {

    @Unique
    private boolean shadow = true;

    @Override
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @Redirect(method = "renderWidget",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/MultiLineLabel;renderLeftAligned(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"))
    private void considerShadow(MultiLineLabel instance, GuiGraphics graphics, int i, int i1, int i2, int i3) {
        if (shadow) {
            instance.renderLeftAligned(graphics, i, i1, i2, i3);
        } else {
            instance.renderLeftAlignedNoShadow(graphics, i, i1, i2, i3);
        }
    }
}
