package tfar.backstory.attachments;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface SyncHandler<T> {
    /**
     * Decides whether data should be sent to some player that can see the holder.
     *
     * <p>By default, all players that can see the holder are sent the data.
     * A typical use case for this method is to only send player-specific data to that player.
     *
     * <p>The returned value should be consistent for a given holder and player.
     *
     * @param holder the holder for the attachment, can be cast if the subtype is known
     * @param to     the player that might receive the data
     * @return {@code true} to send data to the player, {@code false} otherwise
     */
    default boolean sendToPlayer(Object holder, ServerPlayer to) {
        return true;
    }

    /**
     * Writes attachment data to a buffer.
     *
     * <p>If {@code initialSync} is {@code true},
     * the data should be written in full because the client does not have any previous data.
     *
     * <p>If {@code initialSync} is {@code false},
     * the client already received a previous version of the data.
     * In this case, this method is only called once for the attachment,
     * and the resulting data is broadcast to all relevant players.
     *
     * <p>If nothing is written to the buffer, nothing is sent to the client at all,
     * and {@link #read} will not be called on the client side.
     */
    void write(RegistryFriendlyByteBuf buf, T attachment, boolean initialSync);

    /**
     * Reads attachment data on the client side.
     *
     * @param holder        the attachment holder, can be cast if the subtype is known
     * @param previousValue the previous value of the attachment, or {@code null} if there was no previous value
     * @return the new value of the attachment, or {@code null} if the attachment should be removed
     */
    @Nullable
    T read(Object holder, RegistryFriendlyByteBuf buf, @Nullable T previousValue);
}
