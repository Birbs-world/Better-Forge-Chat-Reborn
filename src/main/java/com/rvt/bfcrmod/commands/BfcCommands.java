package com.rvt.bfcrmod.commands;

import java.util.Arrays;

import com.rvt.bfcrmod.BetterForgeChat;
import com.rvt.bfcrmod.TextFormatter;
import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.PermissionsHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;

public class BfcCommands {
	private static final Iterable<String> bfcrmodSubCommands = Arrays.asList(new String[] {
			"info", "colors", "test"
	});
	
	protected static boolean checkPermission(CommandSourceStack c, PermissionNode<Boolean> node) {
		try {
			return PermissionsHandler.playerHasPermission(c.getPlayerOrException().getUUID(), node);
		} catch(CommandSyntaxException e) {
			// Not a player (console or rcon)
			return true;
		}
	}
	protected static boolean checkContextPermission(CommandContext<CommandSourceStack> c, PermissionNode<Boolean> node) {
		return checkPermission(c.getSource(), node);
	}
	protected static int failNoPermission(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendFailure(TextFormatter.stringToFormattedText(TextFormatter.COLOR_RED + "You don't have permission to run this command" + TextFormatter.RESET_ALL_FORMAT));
		return 0;
	}
	
	public static void register(CommandDispatcher<CommandSourceStack> disp) {
		disp.register(Commands.literal("bfcr").requires((c) -> {
				return checkPermission(c, PermissionsHandler.bfcrmodCommand);
			}).then(Commands.argument("mode", StringArgumentType.greedyString())
					.suggests((context, builder) -> SharedSuggestionProvider.suggest(bfcrmodSubCommands, builder))
					.executes(BfcCommands::modCommand)));
		if(ConfigHandler.config.enableColorsCommand.get()) {
			disp.register(Commands.literal("colors").requires((c) -> {
					return checkPermission(c, PermissionsHandler.coloredChatNode);
				}).executes(BfcCommands::colorCommand));
		}
		NickCommands.register(disp);
	}
	
	public static int modCommand(CommandContext<CommandSourceStack> ctx) {
		String arg = StringArgumentType.getString(ctx, "mode");
            switch (arg) {
                case "colors" -> {
                    if(checkContextPermission(ctx, PermissionsHandler.bfcrmodCommandColorsSubCommand))
                        return colorCommand(ctx);
                    else return failNoPermission(ctx);
                }
                case "info" -> {
                    if(checkContextPermission(ctx, PermissionsHandler.bfcrmodCommandInfoSubCommand)) {
                        boolean hasMetaProv = BetterForgeChat.instance.metadataProvider != null;
                        boolean hasNickProv = BetterForgeChat.instance.nicknameProvider != null;
                        String metaProvName = hasMetaProv ? BetterForgeChat.instance.metadataProvider.getProviderName() : "";
                        String nickProvName = hasNickProv ? BetterForgeChat.instance.nicknameProvider.getProviderName() : "";
                        if(hasMetaProv) metaProvName = " (via " + metaProvName + ")";
                        if(hasNickProv) nickProvName = " (via " + nickProvName + ")";
						String finalMetaProvName = metaProvName;
						String finalNickProvName = nickProvName;
						ctx.getSource().sendSuccess(() ->TextFormatter.stringToFormattedText(
                                BetterForgeChat.CHAT_ID_STR + "\n&eMod ID: &d" + BetterForgeChat.MODID + "    &r&eMod version: &d" + BetterForgeChat.VERSION + " (forge)&r\n\n"
                                        + (hasMetaProv ? "&a&lWITH" : "&c&lWITHOUT") + "&r&e metadata integration" + finalMetaProvName + "&r\n"
                                        + (hasNickProv ? "&a&lWITH" : "&c&lWITHOUT") + "&r&e nickname integration" + finalNickProvName + "&r\n"), false);
                        return 1;
                    } else return failNoPermission(ctx);
                }
                case "test" -> {
                    ctx.getSource().sendSuccess(() ->TextFormatter.stringToFormattedText(
                            BetterForgeChat.CHAT_ID_STR
                                    + "&eColors & Styling internal debug test&r\n"
                                            + "Normal &lBold&r &nUnderline&r &oItalic&r &mStrikthrough&r &kObfuscated&r &rReset\n"
                                            + "Normal &lBold &nUnderline &oItalic &mStrikthrough &kObfuscated &rReset"), false);
                    return 1;
                }
                default -> {
                    return 0;
                }
            }
	}
	public static int colorCommand(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() ->
				TextFormatter.stringToFormattedText(
				BetterForgeChat.CHAT_ID_STR + TextFormatter.colorString()), false);
		return 1;
	}
}
