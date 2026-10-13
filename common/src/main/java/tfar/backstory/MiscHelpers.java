package tfar.backstory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class MiscHelpers {

    public static <E extends Enum<E>> StreamCodec<RegistryFriendlyByteBuf,E> altEnumStreamCodec(Class<E> eClass) {
        return StreamCodec.of(FriendlyByteBuf::writeEnum, buffer -> buffer.readEnum(eClass));
    }

    public static <E extends Enum<E>> StreamCodec<FriendlyByteBuf,E> enumStreamCodec(Class<E> eClass) {
        return StreamCodec.of(FriendlyByteBuf::writeEnum, buffer -> buffer.readEnum(eClass));
    }

}
