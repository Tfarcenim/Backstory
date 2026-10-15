package tfar.backstory.attachments;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.backstory.Backstory;
import tfar.backstory.PlayerBackstory;
import tfar.backstory.platform.Services;

public class DataAttachmentUtil {

    public static <T> void clearValue(LivingEntity entity,CommonDataAttachment<T> attachment) {
        Services.PLATFORM.setAttachedValue(entity,attachment, attachment.defaultValueSupplier.apply(entity));
    }

    public static boolean hasBackstory(Player player) {
        return !Services.PLATFORM.getAttachedValue(player,CommonDataAttachments.BACKSTORY).isEmpty();
    }

    public static PlayerBackstory getBackstory(Player player) {
        return Services.PLATFORM.getAttachedValue(player,CommonDataAttachments.BACKSTORY);
    }

    public static void setBackstory(Player player, PlayerBackstory backstory) {
        Services.PLATFORM.setAttachedValue(player,CommonDataAttachments.BACKSTORY, backstory);
    }
}
