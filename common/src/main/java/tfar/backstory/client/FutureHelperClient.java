package tfar.backstory.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tfar.backstory.MultiLineEditBoxDuck;

public class FutureHelperClient {

    public static MultiLineEditBoxBuilder builder() {
        return new MultiLineEditBoxBuilder();
    }
    //@Deprecated(forRemoval = true,since = "26.1")
    public static class MultiLineEditBoxBuilder {
        private int x;
        private int y;
        private Component placeholder = CommonComponents.EMPTY;
        private int textColor = -2039584;
        private boolean textShadow = true;
        private int cursorColor = -3092272;
        private boolean showBackground = true;
        private boolean showDecorations = true;

        public MultiLineEditBoxBuilder setX(int x) {
            this.x = x;
            return this;
        }

        public MultiLineEditBoxBuilder setY(int y) {
            this.y = y;
            return this;
        }

        public MultiLineEditBoxBuilder setPlaceholder(Component placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public MultiLineEditBoxBuilder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public MultiLineEditBoxBuilder setTextShadow(boolean textShadow) {
            this.textShadow = textShadow;
            return this;
        }

        public MultiLineEditBoxBuilder setCursorColor(int cursorColor) {
            this.cursorColor = cursorColor;
            return this;
        }

        public MultiLineEditBoxBuilder setShowBackground(boolean showBackground) {
            this.showBackground = showBackground;
            return this;
        }

        public MultiLineEditBoxBuilder setShowDecorations(boolean showDecorations) {
            this.showDecorations = showDecorations;
            return this;
        }

        public MultiLineEditBox build(Font font, int width, int height, Component narration) {
            MultiLineEditBox multiLineEditBox = new MultiLineEditBox(
                    font,
                    this.x,
                    this.y,
                    width,
                    height,
                    this.placeholder,
                    narration//,
                  //  this.textColor,
                  //  this.textShadow,
                  //  this.cursorColor,
                  //  this.showBackground,
                  //  this.showDecorations
            );

            MultiLineEditBoxDuck multiLineEditBoxDuck = (MultiLineEditBoxDuck) multiLineEditBox;

            multiLineEditBoxDuck.setTextColor(textColor);
            multiLineEditBoxDuck.setCursorColor(cursorColor);
            multiLineEditBoxDuck.setShadow(textShadow);
            multiLineEditBoxDuck.setShowBackground(showBackground);

            return multiLineEditBox;
        }
    }
}
