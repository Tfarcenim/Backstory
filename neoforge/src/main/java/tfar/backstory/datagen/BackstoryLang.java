package tfar.backstory.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.neoforge.common.data.LanguageProvider;
import tfar.backstory.Backstory;
import tfar.backstory.Gender;

public class BackstoryLang extends LanguageProvider {
    public BackstoryLang(PackOutput output) {
        super(output, Backstory.MOD_ID,"en_us");
    }

    @Override
    protected void addTranslations() {
        addGender(Gender.MALE,"Male");
        addGender(Gender.FEMALE,"Female");
        addGender(Gender.NON_BINARY,"Non-binary");
    }

    protected void addGender(Gender gender,String value) {
        addTextComponent(gender.getTranslation(),value);
    }

    protected void addTextComponent(MutableComponent component, String text) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(),text);
        } else {
            throw new UnsupportedOperationException(component +" is not translatable");
        }
    }
}
