package tfar.backstory.platform.services;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import tfar.backstory.attachments.CommonDataAttachment;
import tfar.backstory.network.client.S2CModPacket;
import tfar.backstory.network.server.C2SModPacket;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> void registerDataAttachment(CommonDataAttachment<T> attachment);

    @Nullable
    <T> T getAttachedValue(Object object, CommonDataAttachment<T> attachment);

    @Nullable
    default <T> T getOrDefaultAttachedValue(Object object, CommonDataAttachment<T> attachment,T defaultValue) {
        T value = getAttachedValue(object, attachment);
        return value != null ? value : defaultValue;
    }

    default <T> T getOrCreateAttachedValue(Object object, CommonDataAttachment<T> attachment) {
        T value = getAttachedValue(object, attachment);
        if (value != null) {
            return value;
        }
        value = attachment.getDefaultValueSupplier().apply(object);
        setAttachedValue(object, attachment, value);
        return value;
    }

    <T> void setAttachedValue(Object object, CommonDataAttachment<T> attachment, @Nullable T value);

    <MSG extends S2CModPacket> void registerClientPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec);
    <MSG extends C2SModPacket> void registerServerPacket(CustomPacketPayload.Type<MSG> type, StreamCodec<RegistryFriendlyByteBuf,MSG> streamCodec);

    void sendToClient(S2CModPacket msg, ServerPlayer player);
    void sendToServer(C2SModPacket msg);

    void setShadow(EditBox editBox,boolean shadow);
}