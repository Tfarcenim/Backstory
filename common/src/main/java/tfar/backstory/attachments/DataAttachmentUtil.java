package tfar.backstory.attachments;

import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.platform.Services;

public class DataAttachmentUtil {

    public static <T> void clearValue(LivingEntity entity,CommonDataAttachment<T> attachment) {
        Services.PLATFORM.setAttachedValue(entity,attachment, attachment.defaultValueSupplier.apply(entity));
    }

    public static PlayerBackstory getBackstory(Player player) {
        return Services.PLATFORM.getOrCreateAttachedValue(player,CommonDataAttachments.BACKSTORY);
    }

    public static void setBackstory(Player player, PlayerBackstory backstory) {
        Services.PLATFORM.setAttachedValue(player,CommonDataAttachments.BACKSTORY, backstory);
        if (backstory.useNickname(player)) {
            Services.PLATFORM.refreshName(player);
        }
    }

    public static boolean hasCreatedBackstory(Player player) {
        return Services.PLATFORM.getAttachedValue(player,CommonDataAttachments.CREATED_BACKSTORY) != null;
    }

    public static boolean canChangeBackstory(Player player) {
        return Services.PLATFORM.getAttachedValue(player,CommonDataAttachments.CAN_CHANGE_BACKSTORY) != null;
    }

    public static void setCanEditBackStory(Player player, boolean canEditBackStory) {
        Unit unit = canEditBackStory ? Unit.INSTANCE : null;
        Services.PLATFORM.setAttachedValue(player,CommonDataAttachments.CAN_CHANGE_BACKSTORY, unit);
    }

    public static void markCreatedBackstory(Player player) {
        Services.PLATFORM.setAttachedValue(player,CommonDataAttachments.CREATED_BACKSTORY, Unit.INSTANCE);
    }
}
