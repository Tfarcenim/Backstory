package tfar.backstory.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.network.server.C2SSetPlayerBackstoryPacket;
import tfar.backstory.platform.Services;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class BackstoryEditScreen extends BackstoryScreen {

    private final List<String> pages = Lists.newArrayList();
    private final PlayerBackstory existingBackstory;
    private MultiLineEditBox page;
    private EditBox nameEdit;
    private EditBox genderEdit;


    protected BackstoryEditScreen(Component title, UUID playerUUID,PlayerBackstory existingBackstory) {
        super(title,playerUUID);
        this.existingBackstory = existingBackstory;

        if (!existingBackstory.isEmpty()) {
            for (Filterable<String> filterable : existingBackstory.pages()) {
                pages.add(filterable.get(Minecraft.getInstance().isTextFilteringEnabled()));
            }
        }

        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
    }

    protected void init() {
        super.init();
        int left = backgroundLeft();
        int top = backgroundTop();
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

        nameEdit = initEditbox(120, Minecraft.getInstance().player.getName().getString());
        genderEdit = initEditbox(170, "Male");

        if (!existingBackstory.isEmpty()) {
            nameEdit.setValue(existingBackstory.chosenName());
            genderEdit.setValue(existingBackstory.gender());
        }

        createMenuControls();

        this.updateButtonVisibility();
    }

    protected void createMenuControls() {
        int left = backgroundLeft();
        int y = 220;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .bounds(left+40, y, 98, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            boolean b = this.saveChanges();
            if (b) {
                onClose();
            }
        }).bounds(left + 140, y, 98, 20).build());
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

    @Override
    protected void setInitialFocus() {
        this.setInitialFocus(this.page);
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), this.getPageNumberMessage());
    }

    protected int getNumPages() {
        return pages.size();
    }

    protected void pageBack() {
        super.pageBack();
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


    private void eraseEmptyTrailingPages() {
        ListIterator<String> listiterator = this.pages.listIterator(this.pages.size());

        while (listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
            listiterator.remove();
        }
    }
}
