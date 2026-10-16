package tfar.backstory.attachments;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.backstory.Gender;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.platform.Services;

import java.util.*;

public class DataAttachmentUtil {

    public static <T> void clearValue(LivingEntity entity, CommonDataAttachment<T> attachment) {
        Services.PLATFORM.setAttachedValue(entity, attachment, attachment.defaultValueSupplier.apply(entity));
    }

    public static PlayerBackstory getBackstory(Player player) {
        return Services.PLATFORM.getOrCreateAttachedValue(player, CommonDataAttachments.BACKSTORY);
    }

    public static void setBackstory(Player player, PlayerBackstory backstory) {
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.BACKSTORY, backstory);
        if (backstory.useNickname(player)) {
            Services.PLATFORM.refreshName(player);
        }
    }

    public static boolean hasCreatedBackstory(Player player) {
        return Services.PLATFORM.getAttachedValue(player, CommonDataAttachments.CREATED_BACKSTORY) != null;
    }

    public static boolean canChangeBackstory(Player player) {
        return Services.PLATFORM.getAttachedValue(player, CommonDataAttachments.CAN_CHANGE_BACKSTORY) != null;
    }

    public static void setCanEditBackStory(Player player, boolean canEditBackStory) {
        Unit unit = canEditBackStory ? Unit.INSTANCE : null;
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.CAN_CHANGE_BACKSTORY, unit);
    }

    public static void markCreatedBackstory(Player player) {
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.CREATED_BACKSTORY, Unit.INSTANCE);
    }

    public static void addViewPermission(ServerPlayer player, UUID... newViewers) {
        List<UUID> viewers = new ArrayList<>(Services.PLATFORM.getOrCreateAttachedValue(player, CommonDataAttachments.CAN_VIEW));
        for (UUID viewer : newViewers) {
            if (!viewers.contains(viewer)) {
                viewers.add(viewer);
            }
        }
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.CAN_VIEW, viewers);
    }

    public static void removeViewPermission(ServerPlayer player, UUID... newViewers) {
        List<UUID> viewers = new ArrayList<>(Services.PLATFORM.getOrCreateAttachedValue(player, CommonDataAttachments.CAN_VIEW));
        Arrays.stream(newViewers).forEach(viewers::remove);
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.CAN_VIEW, viewers);
    }

    public static boolean hasViewPermission(ServerPlayer player, ServerPlayer viewer) {
        List<UUID> viewers = Services.PLATFORM.getOrCreateAttachedValue(player, CommonDataAttachments.CAN_VIEW);
        return viewers.contains(viewer.getUUID());
    }

    public static int getOrCreateBookColor(Player player) {
        return Services.PLATFORM.getOrCreateAttachedValue(player, CommonDataAttachments.BOOK_COLOR);
    }

    public static void wipeBackStory(Player player) {
        PlayerBackstory existing = getBackstory(player);
        PlayerBackstory backstory = new PlayerBackstory(player.getGameProfile().getName(), existing.gender(),
                List.of(new Filterable<>("", Optional.of(""))));
        Services.PLATFORM.setAttachedValue(player, CommonDataAttachments.BACKSTORY,backstory);
    }
}
