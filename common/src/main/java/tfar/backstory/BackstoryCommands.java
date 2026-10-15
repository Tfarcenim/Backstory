package tfar.backstory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import tfar.backstory.attachments.DataAttachmentUtil;
import tfar.backstory.network.client.S2CBackstoryScreenPacket;
import tfar.backstory.platform.Services;

public class BackstoryCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Backstory.MOD_ID)
                .then(Commands.literal("edit")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes(BackstoryCommands::openEditSelf)
                        )
                );
    }

    static int openEditSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Services.PLATFORM.sendToClient(new S2CBackstoryScreenPacket(
                DataAttachmentUtil.getBackstory(player),player.getUUID(), S2CBackstoryScreenPacket.Manage.EDIT),player);
        return 1;
    }
}
