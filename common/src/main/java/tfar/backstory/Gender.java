package tfar.backstory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public enum Gender implements StringRepresentable {
    MALE("male"),FEMALE("female"),NON_BINARY("non_binary");

    public static final Gender[] VALUES = values();

    private final String name;
    private final MutableComponent translation;

    Gender(String name) {
        this.name = name;
        translation = Component.translatable(Backstory.MOD_ID+"."+name);
    }

    public MutableComponent getTranslation() {
        return translation;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
