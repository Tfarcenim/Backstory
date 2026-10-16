package tfar.backstory;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import tfar.backstory.attachments.DataAttachmentUtil;
import tfar.backstory.network.client.S2CBackstoryScreenPacket;
import tfar.backstory.platform.Services;

import java.util.Collection;

public class BackstoryCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Backstory.MOD_ID)
                .then(Commands.literal("edit")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(source -> source.hasPermission(Commands.LEVEL_MODERATORS))
                                .executes(BackstoryCommands::openEditOther)
                        )
                        .executes(BackstoryCommands::openEditSelf)
                )
                .then(Commands.literal("change")
                        .requires(source -> source.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("allow", BoolArgumentType.bool())
                                        .executes(BackstoryCommands::setCanEditSelf)
                                )
                        )
                )
                .then(Commands.literal("view")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(BackstoryCommands::openViewOther)
                        )
                        .executes(BackstoryCommands::openViewSelf)
                )
                .then(Commands.literal("request")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(BackstoryCommands::requestViewPermission)
                        )
                )
                .then(Commands.literal("can_view")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("allow", BoolArgumentType.bool())
                                        .executes(BackstoryCommands::setCanView)
                                )
                        )
                )
                .then(Commands.literal("wipe")
                        .requires(source -> source.hasPermission(Commands.LEVEL_MODERATORS))
                        .then(Commands.argument("players", EntityArgument.players())
                                        .executes(BackstoryCommands::wipeBackstory)
                        )
                )
        );
    }

    static int openEditSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();

        boolean isMod = source.hasPermission(Commands.LEVEL_MODERATORS);

        boolean hasPermission = isMod || DataAttachmentUtil.canChangeBackstory(player) || !DataAttachmentUtil.hasCreatedBackstory(player);

        if (hasPermission) {
            Services.PLATFORM.sendToClient(new S2CBackstoryScreenPacket(
                    DataAttachmentUtil.getBackstory(player), player.getUUID(),DataAttachmentUtil.getOrCreateBookColor(player), S2CBackstoryScreenPacket.Manage.EDIT), player);
            return 1;
        } else {
            source.sendFailure(Component.literal("You do not have permission to edit your backstory!"));
        }
        return 0;
    }

    static int openEditOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer editingPlayer = source.getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");

        Services.PLATFORM.sendToClient(new S2CBackstoryScreenPacket(
                DataAttachmentUtil.getBackstory(targetPlayer), targetPlayer.getUUID(),DataAttachmentUtil.getOrCreateBookColor(targetPlayer), S2CBackstoryScreenPacket.Manage.EDIT), editingPlayer);
        return 1;
    }

    static int requestViewPermission(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer viewingPlayer = source.getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");

        targetPlayer.sendSystemMessage(Component.empty()
                .append(viewingPlayer.getName()).append(" wants to view your backstory ")
                .append(Component.literal("[Accept?]")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                                .withClickEvent(
                                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backstory can_view "+viewingPlayer.getGameProfile().getName()+" true"
                                        )))));

        return 1;
    }

    static int setCanView(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer targetPlayer = source.getPlayerOrException();
        Collection<ServerPlayer> viewingPlayers = EntityArgument.getPlayers(ctx, "players");
        boolean allow =  BoolArgumentType.getBool(ctx, "allow");

        for (ServerPlayer viewingPlayer : viewingPlayers) {
            if (allow) {
                DataAttachmentUtil.addViewPermission(targetPlayer, viewingPlayer.getUUID());
            } else {
             DataAttachmentUtil.removeViewPermission(targetPlayer, viewingPlayer.getUUID());
            }
        }

        return viewingPlayers.size();
    }

    static int openViewOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer viewingPlayer = source.getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "player");

        boolean isMod = source.hasPermission(Commands.LEVEL_MODERATORS);
        boolean hasViewPermission = DataAttachmentUtil.hasViewPermission(targetPlayer, viewingPlayer);


        if (isMod || hasViewPermission) {
            Services.PLATFORM.sendToClient(new S2CBackstoryScreenPacket(
                    DataAttachmentUtil.getBackstory(targetPlayer), targetPlayer.getUUID(),DataAttachmentUtil.getOrCreateBookColor(targetPlayer), S2CBackstoryScreenPacket.Manage.VIEW), viewingPlayer);
            return 1;
        }

        source.sendFailure(Component.literal("You must request permission to view other's backstory!"));

        return 0;
    }

    static int openViewSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Services.PLATFORM.sendToClient(new S2CBackstoryScreenPacket(
                DataAttachmentUtil.getBackstory(player), player.getUUID(),DataAttachmentUtil.getOrCreateBookColor(player), S2CBackstoryScreenPacket.Manage.VIEW), player);
        return 1;
    }

    static int setCanEditSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "players");
        boolean allow = BoolArgumentType.getBool(ctx, "allow");
        for (ServerPlayer player : players) {
            DataAttachmentUtil.setCanEditBackStory(player, allow);
        }
        return players.size();
    }

    static int wipeBackstory(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "players");
        for (ServerPlayer player : players) {
            DataAttachmentUtil.wipeBackStory(player);
        }
        return players.size();
    }
}
