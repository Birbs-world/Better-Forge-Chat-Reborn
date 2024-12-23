package com.rvt.bfcrmod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigHandler {
	private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
	public static final ConfigBuilder config = new ConfigBuilder(builder);
	public static ModConfigSpec spec = builder.build();
	
	public static class ConfigBuilder {
		public final ModConfigSpec.ConfigValue<String> playerNameFormat;
		public final ModConfigSpec.ConfigValue<String> chatMessageFormat;
		public final ModConfigSpec.ConfigValue<String> timestampFormat;
		public final ModConfigSpec.ConfigValue<String> discordBotToken;

		public final ModConfigSpec.ConfigValue<Integer> maximumNicknameLength;
		public final ModConfigSpec.ConfigValue<Integer> minimumNicknameLength;
		
		public final ModConfigSpec.ConfigValue<Boolean> enableTimestamp;
		public final ModConfigSpec.ConfigValue<Boolean> enableFtbEssentials;
		public final ModConfigSpec.ConfigValue<Boolean> enableLuckPerms;
		public final ModConfigSpec.ConfigValue<Boolean> enableMarkdown;
		public final ModConfigSpec.ConfigValue<Boolean> enableColorsCommand;
		public final ModConfigSpec.ConfigValue<Boolean> enableTabListIntegration;
		public final ModConfigSpec.ConfigValue<Boolean> enableMetadataInTabList;
		public final ModConfigSpec.ConfigValue<Boolean> enableNicknamesInTabList;
		public final ModConfigSpec.ConfigValue<Boolean> enableWhoisCommand;
		public final ModConfigSpec.ConfigValue<Boolean> enableChatNicknameCommand;
		public final ModConfigSpec.ConfigValue<Boolean> autoEnableChatNicknameCommand;
		//public final ModConfigSpec.ConfigValue<Boolean> enableDiscordBotIntegration;
		
		public ConfigBuilder(ModConfigSpec.Builder builder) {
			builder.push("BetterForgeChatModConfig");
			playerNameFormat = builder
					.comment("  Controls the chat message format",
							 "    $prefix is replaced by the user's prefix or nothing if the user has no prefix",
							 "    $suffix is replaced by the user's suffix or nothing if the user has no suffix",
							 "    $name is replaced by the user's name, or nickname if they have one")
					.define("playerNameFormat", "$prefix$name$suffix");
			chatMessageFormat = builder
					.comment("  Controls the chat message format",
							 "    $time is replaced by the timestamp field or nothing if disabled", 
							 "    $name is replaced by the user's name, or nickname if they have one",
							 "    $msg is replaced by the username's message (if you use it more then once it WILL break this mod)")
					.define("chatMessageFormat", "$time | $name: $msg");
			timestampFormat = builder
					.comment("  Timestamp format following the java SimpleDateFormat",
							 "    Read more here: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html")
					.define("timestampFormat", "HH:mm");
			discordBotToken = builder.comment("  Discord bot token for discord integration").define("discordBotToken", "");
			enableTimestamp = builder.comment("  Enables or disables the filling in of timestamps").define("enableTimestamp", true);
			enableFtbEssentials = builder.comment("  Enables or disables FTB essentials nickname integration").define("useFtbEssentials", true);
			enableLuckPerms = builder.comment("  Enables or disables LuckPerms integration").define("useLuckPerms", true);
			enableMarkdown = builder.comment("  Enables or disables markdown styling").define("markdownEnabled", true);
			enableTabListIntegration = builder.comment("  Enables or disables custom tab list information").define("tabList", true);
			enableMetadataInTabList = builder.comment("  Enables or disables prefixes&suffixes in the tab list").define("tabListMetadata", true);
			enableNicknamesInTabList = builder.comment("  Enables or disables nicknames in the tab list").define("tabListNicknames", true);
			enableColorsCommand = builder.comment("  Enables or disables the /colors command").define("enableColorsCommand", true);
			enableWhoisCommand = builder.comment(
					"  Enables or disables the integrated whois command"
				  + "    (If autoIntegratedNicknames is true, this setting is ignored) ").define("enableWhoisCommand", true);
			enableChatNicknameCommand = builder.comment(
					  "  Enables or disables the integrated nickname command"
					+ "   (If autoIntegratedNicknames is true, this setting is ignored) ").define("enableIntegratedNicknames", false);
			//enableDiscordBotIntegration = builder.comment("  Enables or disables discord integration").define("enableDiscordIntegraation", false);
			autoEnableChatNicknameCommand = builder.comment("  When true, enables the integrated nickname-related commands if FTB essentials is not present").define("autoIntegratedNicknames", true);
			maximumNicknameLength = builder.comment("  Maximum allowed nickname length (for integrated nickname commands)").defineInRange("maximumNicknameLength", 50, 1, 500);
			minimumNicknameLength = builder.comment("  Minimum allowed nickname length (for integrated nickname commands)").defineInRange("minimumNicknameLength", 1, 1, 500);
			builder.pop();
		}
	}
}
