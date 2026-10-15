package tfar.backstory.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import tfar.backstory.Backstory;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.network.server.C2SSetPlayerBackstoryPacket;
import tfar.backstory.platform.Services;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class BackstoryEditScreen extends Screen {

    protected static final int IMAGE_WIDTH = 285;
    protected static final int IMAGE_HEIGHT = 240;

    protected static final int TEXTURE_WIDTH = 1995;
    protected static final int TEXTURE_HEIGHT = 240;

    protected static final int[] OPENING_FRAME_SEQUENCE = new int[]{0, 1, 2, 3};
    private final UUID playerUUID;

    protected int frame = 3;//there are 7 frames, 0 - 6
    private boolean isModified;

    long tickCount;
    private int currentPage;
    private final List<String> pages = Lists.newArrayList();

    private Component numberOfPages = CommonComponents.EMPTY;
    private MultiLineEditBox page;

    private EditBox nameEdit;
    private EditBox genderEdit;
    private PageButton forwardButton;
    private PageButton backButton;
    private Button doneButton;
    public static final ResourceLocation BOOK_LOCATION = Backstory.id("textures/gui/image.png");


    protected BackstoryEditScreen(Component title, UUID player,PlayerBackstory existingBackstory) {
        super(title);
        this.playerUUID = player;

        if (!existingBackstory.isEmpty()) {

        }

        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }

    private int getNumPages() {
        return this.pages.size();
    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        //frame = (int) ((tickCount / 10) % 7);
    }

    protected void init() {


        this.page = FutureHelperClient.builder()
                .setShowDecorations(false)
                .setTextColor(0xff000000)
                .setCursorColor(0xff000000)
                .setShowBackground(false)
                .setTextShadow(false)
                .setX((this.width + 20) / 2 - 8)
                .setY(60)
                .build(this.font, 108, 140, CommonComponents.EMPTY);
        this.page.setCharacterLimit(PlayerBackstory.CHAR_LIMIT);
        //this.pages.setLineLimit(126 / 9);
        this.page.setValueListener(value -> this.pages.set(this.currentPage, value));
        this.addRenderableWidget(this.page);
        this.updatePageContent();

        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            boolean b = this.saveChanges();

            if (b) {
                minecraft.setScreen(null);
            }
        }).bounds(this.width / 2 + 2, 220, 98, 20).build());

        int leftPos = (this.width - IMAGE_WIDTH) / 2;
        int y = 200;
        this.forwardButton = this.addRenderableWidget(new PageButton(leftPos + 225, y, true, button -> this.pageForward(), true));
        this.backButton = this.addRenderableWidget(new PageButton(leftPos + 36, y, false, button -> this.pageBack(), true));

        nameEdit = initEditbox(120, Minecraft.getInstance().player.getName().getString());
        genderEdit = initEditbox(170, "Male");

        this.updateButtonVisibility();
    }

    private EditBox initEditbox(int yPos, String starter) {
        int left = backgroundLeft();
        int top = backgroundTop();
        EditBox editBox = new EditBox(this.font, left + 45, top + yPos, 98, 12, Component.translatable("name"));
        editBox.setCanLoseFocus(true);
        editBox.setTextColor(-1);
        editBox.setTextColorUneditable(-1);
        editBox.setBordered(false);
        editBox.setMaxLength(25);
        //editBox.setResponder(this::onNameChanged);
        editBox.setValue(starter);
        editBox.setTextColor(0xff000000);

        Services.PLATFORM.setShadow(editBox, false);

        this.addRenderableWidget(editBox);
        return editBox;
    }

    protected void onNameChanged(String string) {
    }

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.page);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.getPageNumberMessage());
    }

    private Component getPageNumberMessage() {
        return Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages()).withColor(-16777216);
        //.withoutShadow();
    }

    private void pageBack() {
        if (this.currentPage > 0) {
            this.currentPage--;
            this.updatePageContent();
        }

        this.updateButtonVisibility();
    }

    private void pageForward() {
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

    private void updatePageContent() {
        this.page.setValue(this.pages.get(this.currentPage));//, true);
        this.numberOfPages = this.getPageNumberMessage();
    }

    private boolean saveChanges() {
        this.eraseEmptyTrailingPages();
        PlayerBackstory playerBackstory = new PlayerBackstory(nameEdit.getValue(),genderEdit.getValue(),
                this.pages.stream().map(Filterable::passThrough).toList());

        if (playerBackstory.isValid()) {
            Services.PLATFORM.sendToServer(new C2SSetPlayerBackstoryPacket(playerBackstory, playerUUID));
            return true;
        }

        //this.updateLocalCopy();
        //int i = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
        //this.minecraft.getConnection().send(new ServerboundEditBookPacket(i, this.pages, publish ? Optional.of(this.title.trim()) : Optional.empty()));
        return false;
    }

    private void appendPageToBook() {
        if (this.getNumPages() < PlayerBackstory.PAGE_LIMIT) {
            this.pages.add("");
            this.isModified = true;
        }
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

    private int backgroundLeft() {
        return (this.width - IMAGE_WIDTH) / 2;
    }

    private int backgroundTop() {
        return 2;
    }


    private void updateButtonVisibility() {
        this.backButton.visible = this.currentPage > 0;
    }

    private void eraseEmptyTrailingPages() {
        ListIterator<String> listiterator = this.pages.listIterator(this.pages.size());

        while (listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
            listiterator.remove();
        }
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
        int left = this.backgroundLeft();
        int top = this.backgroundTop();

        int j1 = this.font.width(this.numberOfPages);
        guiGraphics.drawString(this.font, this.numberOfPages, left + 240 - j1, top + 44, 0, false);

        guiGraphics.drawString(font, Component.literal("RP Name"), left + 50, top + 94, 0, false);
        guiGraphics.drawString(font, Component.literal("Gender"), left + 50, top + 144, 0, false);

    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        int u = frame * IMAGE_WIDTH;
        guiGraphics.blit(BOOK_LOCATION, (this.width - IMAGE_WIDTH + 16) / 2, 2, u, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

}
