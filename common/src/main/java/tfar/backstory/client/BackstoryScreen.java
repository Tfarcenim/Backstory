package tfar.backstory.client;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import tfar.backstory.Backstory;
import tfar.backstory.PlayerBackstory;

import java.util.List;

public abstract class BackstoryScreen extends Screen {

    protected static final int IMAGE_WIDTH = 285;
    protected static final int IMAGE_HEIGHT = 240;
    protected static final int TEXTURE_WIDTH = 1995;
    protected static final int TEXTURE_HEIGHT = 240;

    protected static final int BACKSTORY_LEFT = 144;
    protected static final int BACKSTORY_TOP = 54;

    protected static final int BACKSTORY_WIDTH = 108;
    protected static final int BACKSTORY_HEIGHT = 140;

    protected int left;
    protected int top;

    protected static final int NAME_Y = 124;
    protected static final int GENDER_Y = NAME_Y + 36;

    protected  static final int LEFT_TEXT_X = 50;

    protected final ResourceLocation BOOK_LOCATION;

    protected static final int[] OPENING_FRAME_SEQUENCE = new int[]{0, 1, 2, 3};
    protected final ResolvableProfile playerUUID;
    protected final PlayerBackstory backstory;
    protected final List<String> pages = Lists.newArrayList();
    protected int frame = 3;//there are 7 frames, 0 - 6
    protected int currentPage;
    protected Component numberOfPages = CommonComponents.EMPTY;
    protected PageButton forwardButton;
    protected PageButton backButton;
    long tickCount;
    protected final boolean playTurnSound = true;

    protected BackstoryScreen(Component title, ResolvableProfile playerUUID, PlayerBackstory backstory, int bookColor) {
        super(title);
        BOOK_LOCATION = Backstory.id("textures/gui/image"+bookColor+".png");
        this.playerUUID = playerUUID;
        this.backstory = backstory;
    }

    @Override
    protected void init() {
        left = (this.width - IMAGE_WIDTH) / 2;
        top = 2;
        super.init();
        this.createPageControlButtons();
    }


    /**
     * Moves the book to the specified page and returns true if it exists, {@code false} otherwise.
     */
    public boolean setPage(int pageNum) {
        int i = Mth.clamp(pageNum, 0, this.pages.size() - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
            return true;
        } else {
            return false;
        }
    }

    /**
     * I'm not sure why this exists. The function it calls is public and does all the work.
     */
    protected boolean forcePage(int pageNum) {
        return this.setPage(pageNum);
    }


    protected void createPageControlButtons() {
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
        this.updatePageContent();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            this.currentPage++;
        } else {
            this.appendPageToBook();
            if (this.currentPage < this.getNumPages() - 1) {
                this.currentPage++;
            }
        }

        this.updatePageContent();
        this.updateButtonVisibility();
    }

    protected void appendPageToBook() {
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        int u = frame * IMAGE_WIDTH;
        guiGraphics.blit(BOOK_LOCATION, (this.width - IMAGE_WIDTH + 16) / 2, 2, u, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
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

    /**
     * Called when a keyboard key is pressed within the GUI element.
     * <p>
     *
     * @param keyCode   the key code of the pressed key.
     * @param scanCode  the scan code of the pressed key.
     * @param modifiers the keyboard modifiers.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case 266:
                this.backButton.onPress();
                return true;
            case 267:
                this.forwardButton.onPress();
                return true;
            default:
                return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int j1 = this.font.width(this.numberOfPages);
        guiGraphics.drawString(this.font, this.numberOfPages, left + 240 - j1, top + 44, 0, false);
        guiGraphics.drawString(font, Component.literal("Name").withStyle(ChatFormatting.BOLD), left + LEFT_TEXT_X, top + NAME_Y, 0, false);
        guiGraphics.drawString(font, Component.literal("Gender").withStyle(ChatFormatting.BOLD), left + LEFT_TEXT_X, top + GENDER_Y, 0, false);



        Player player = playerUUID.id().isPresent() ? minecraft.level.getPlayerByUUID(playerUUID.id().get()) : null;
        boolean flag1 = player != null && LivingEntityRenderer.isEntityUpsideDown(player);
        boolean flag2 = player != null && player.isModelPartShown(PlayerModelPart.HAT);
        PlayerFaceRenderer.draw(guiGraphics, getPlayerSkin(playerUUID.gameProfile()), left + 59, top + 48, 64, flag2, flag1);
    }

    public static ResourceLocation getPlayerSkin(GameProfile gameProfile) {
        Minecraft minecraft = Minecraft.getInstance();
        SkinManager skinManager = minecraft.getSkinManager();
        PlayerSkin map = skinManager.getInsecureSkin(gameProfile);
        return map.texture();
    }

    protected void updateButtonVisibility() {
        this.backButton.visible = this.currentPage > 0;
    }

    protected Component getPageNumberMessage() {
        return Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages()).withColor(-16777216);
        //.withoutShadow();
    }

    protected int getNumPages() {
        return this.pages.size();
    }

    protected void updatePageContent() {
        this.numberOfPages = this.getPageNumberMessage();
    }
}
