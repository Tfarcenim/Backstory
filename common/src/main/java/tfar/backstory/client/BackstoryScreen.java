package tfar.backstory.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tfar.backstory.Backstory;

import java.util.UUID;

public abstract class BackstoryScreen extends Screen {

    protected static final int IMAGE_WIDTH = 285;
    protected static final int IMAGE_HEIGHT = 240;
    protected static final int TEXTURE_WIDTH = 1995;
    protected static final int TEXTURE_HEIGHT = 240;

    public static final ResourceLocation BOOK_LOCATION = Backstory.id("textures/gui/image.png");

    protected static final int[] OPENING_FRAME_SEQUENCE = new int[]{0, 1, 2, 3};
    protected final UUID playerUUID;
    protected int frame = 3;//there are 7 frames, 0 - 6
    protected int currentPage;
    protected Component numberOfPages = CommonComponents.EMPTY;
    protected PageButton forwardButton;
    protected PageButton backButton;
    long tickCount;
    protected final boolean playTurnSound = true;

    protected BackstoryScreen(Component title, UUID playerUUID) {
        super(title);
        this.playerUUID = playerUUID;
    }

    @Override
    protected void init() {
        super.init();
        this.createPageControlButtons();
    }

    protected void createPageControlButtons() {
        int left = backgroundLeft();
        int top = backgroundTop();
        int y = top + 190;
        this.forwardButton = this.addRenderableWidget(new PageButton(left + 225, y, true, button -> this.pageForward(), playTurnSound));
        this.backButton = this.addRenderableWidget(new PageButton(left + 36, y, false, button -> this.pageBack(), playTurnSound));
        this.updateButtonVisibility();
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }

        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            this.currentPage++;
        }

        this.updateButtonVisibility();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        int u = frame * IMAGE_WIDTH;
        guiGraphics.blit(BOOK_LOCATION, (this.width - IMAGE_WIDTH + 16) / 2, 2, u, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }


    protected int backgroundLeft() {
        return (this.width - IMAGE_WIDTH) / 2;
    }

    protected int backgroundTop() {
        return 2;
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;//        //frame = (int) ((tickCount / 10) % 7);
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param guiGraphics the GuiGraphics object used for rendering.
     * @param mouseX      the x-coordinate of the mouse cursor.
     * @param mouseY      the y-coordinate of the mouse cursor.
     * @param partialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderLabels(guiGraphics, mouseX, mouseY);
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int left = this.backgroundLeft();
        int top = this.backgroundTop();
        int j1 = this.font.width(this.numberOfPages);
        guiGraphics.drawString(this.font, this.numberOfPages, left + 240 - j1, top + 44, 0, false);
        guiGraphics.drawString(font, Component.literal("RP Name"), left + 50, top + 94, 0, false);
        guiGraphics.drawString(font, Component.literal("Gender"), left + 50, top + 144, 0, false);
    }

    protected void updateButtonVisibility() {
        this.backButton.visible = this.currentPage > 0;
    }

    protected Component getPageNumberMessage() {
        return Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages()).withColor(-16777216);
        //.withoutShadow();
    }

    protected abstract int getNumPages();
}
