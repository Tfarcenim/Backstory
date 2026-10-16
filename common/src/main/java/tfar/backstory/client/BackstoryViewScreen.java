package tfar.backstory.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.network.chat.*;
import tfar.backstory.AbstractScrollWidgetDuck;
import tfar.backstory.MultiLineTextWidgetDuck;
import tfar.backstory.PlayerBackstory;

import java.util.UUID;

public class BackstoryViewScreen extends BackstoryScreen {

    private FittingMultiLineTextWidget page;


    protected BackstoryViewScreen(Component title, UUID playerUUID, PlayerBackstory backstory, int i) {
        super(title,playerUUID, backstory, i);
        backstory.pages().forEach(backstoryPage ->
                pages.add(backstoryPage.get(Minecraft.getInstance().isTextFilteringEnabled())));
    }

    @Override
    protected void init() {
        super.init();
        page = new FittingMultiLineTextWidget(left+BACKSTORY_LEFT+3, top+BACKSTORY_TOP,BACKSTORY_WIDTH,BACKSTORY_HEIGHT,
                Component.literal(backstory.getPages(Minecraft.getInstance().isTextFilteringEnabled()).get(currentPage)),font);

        page.setColor(0);

        ((MultiLineTextWidgetDuck)page.multilineWidget).setShadow(false);

        ((AbstractScrollWidgetDuck)page).setShowBackground(false);

        addRenderableWidget(page);
        this.createMenuControls();
        updatePageContent();
    }

    protected void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(this.width / 2 - 100,
                220, 200, 20).build());
    }

    @Override
    protected void updateButtonVisibility() {
        super.updateButtonVisibility();
        this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
    }

    @Override
    public boolean handleComponentClicked(Style style) {
        ClickEvent clickevent = style.getClickEvent();
        if (clickevent == null) {
            return false;
        } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String s = clickevent.getValue();

            try {
                int i = Integer.parseInt(s) - 1;
                return this.forcePage(i);
            } catch (Exception exception) {
                return false;
            }
        } else {
            boolean flag = super.handleComponentClicked(style);
            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                this.onClose();
            }

            return flag;
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        int padding = 16;
        guiGraphics.drawString(font, Component.literal(backstory.chosenName()), left + LEFT_TEXT_X, top + NAME_Y+padding, 0, false);
        guiGraphics.drawString(font, backstory.gender().getTranslation(), left + LEFT_TEXT_X, top + GENDER_Y+padding, 0, false);
    }

    @Override
    protected void updatePageContent() {
        super.updatePageContent();
        MutableComponent text = Component.literal(this.pages.get(this.currentPage));
        this.page.setMessage(text);//, true);
        page.multilineWidget.setMessage(text);
    }
}
