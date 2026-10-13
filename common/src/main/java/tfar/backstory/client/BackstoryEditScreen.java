package tfar.backstory.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tfar.backstory.Backstory;

public class BackstoryEditScreen extends Screen {

    protected static final int IMAGE_WIDTH = 192;
    protected static final int IMAGE_HEIGHT = 160;

    protected static final int TEXTURE_WIDTH = 1330;
    protected static final int TEXTURE_HEIGHT = 160;

    protected static final int[] OPENING_FRAME_SEQUENCE = new int[]{0,1,2,3};

    protected int frame = 3;//there are 7 frames, 0 - 6

    long tickCount;

    public static final ResourceLocation BOOK_LOCATION = Backstory.id("textures/gui/image.png");

    String name;

    protected BackstoryEditScreen(Component title) {
        super(title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        //frame = (int)((tickCount / 10)%7);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        int u = frame * 190;
        guiGraphics.blit(BOOK_LOCATION, (this.width - 188) / 2, 2, u, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
                TEXTURE_WIDTH,TEXTURE_HEIGHT);
    }
}
