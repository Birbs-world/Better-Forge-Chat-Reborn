package com.rvt.bfcrmod.config;

import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigHandler {
	private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
	public static final ConfigBuilder config = new ConfigBuilder(builder);
	public static ModConfigSpec spec = builder.build();
	
	public static class ConfigBuilder {
		public final ModConfigSpec.ConfigValue<String> playerNameFormat;
		public final ModConfigSpec.ConfigValue<String> chatMessageFormat;
		public final ModConfigSpec.EnumValue<ChatFormatting> chatMessageColor;
		public final ModConfigSpec.ConfigValue<String> timestampFormat;
		public final ModConfigSpec.IntValue maximumNicknameLength;
		public final ModConfigSpec.IntValue minimumNicknameLength;
		public final ModConfigSpec.BooleanValue enableTimestamp;
		public final ModConfigSpec.BooleanValue enableFtbEssentials;
		public final ModConfigSpec.BooleanValue enableLuckPerms;
		public final ModConfigSpec.BooleanValue enableMarkdown;
		public final ModConfigSpec.BooleanValue enableTabListIntegration;
		public final ModConfigSpec.BooleanValue enableMetadataInTabList;
		public final ModConfigSpec.BooleanValue enableNicknamesInTabList;
		public final ModConfigSpec.BooleanValue enableWhoisCommand;
		public final ModConfigSpec.BooleanValue enableChatNicknameCommand;
		public final ModConfigSpec.BooleanValue autoEnableChatNicknameCommand;
		public final ModConfigSpec.ConfigValue<String> boldSymbol1;
		public final ModConfigSpec.ConfigValue<String> boldSymbol2;
		public final ModConfigSpec.ConfigValue<String> italicSymbol1;
		public final ModConfigSpec.ConfigValue<String> italicSymbol2;
		public final ModConfigSpec.ConfigValue<String> obfuscatedSymbol1;
		public final ModConfigSpec.ConfigValue<String> obfuscatedSymbol2;
		public final ModConfigSpec.ConfigValue<String> underlineSymbol1;
		public final ModConfigSpec.ConfigValue<String> underlineSymbol2;
		public final ModConfigSpec.ConfigValue<String> strikethroughSymbol1;
		public final ModConfigSpec.ConfigValue<String> strikethroughSymbol2;
		
		public ConfigBuilder(ModConfigSpec.Builder builder) {
//			builder.push("BetterNeoForgeChatModConfig");
			playerNameFormat = builder
					.comment("""
							 Controls the chat message format
							 $prefix is replaced by the user's prefix or nothing if the user has no prefix
							 $suffix is replaced by the user's suffix or nothing if the user has no suffix
							 $name is replaced by the user's name, or nickname if they have one""")
					.define("playerNameFormat", "$prefix$name$suffix");
			chatMessageFormat = builder
					.comment("""
							 Controls the chat message format
							 $time is replaced by the timestamp field or nothing if disabled
							 $name is replaced by the user's name, or nickname if they have one
							 colors can be uses in the formatting string. for a global message color see next section
							 $msg is replaced by the username's message (if you use it more then once it WILL break this mod)""")
					.define("chatMessageFormat", "$name: $msg");
			chatMessageColor = builder.comment("Sets the global color of the chat messages").defineEnum("chatMessageColor", ChatFormatting.WHITE);
			timestampFormat = builder
					.comment("""
							 Timestamp format following the java SimpleDateFormat
							 Read more here: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html""")
					.define("timestampFormat", "HH:mm");
			enableTimestamp = builder.comment("Enables or disables the filling in of timestamps").define("enableTimestamp", false);
			enableFtbEssentials = builder.comment("Enables or disables FTB essentials nickname integration").define("useFtbEssentials", true);
			enableLuckPerms = builder.comment("Enables or disables LuckPerms integration").define("useLuckPerms", true);
			enableMarkdown = builder.comment("Enables or disables markdown styling").define("markdownEnabled", true);
			enableTabListIntegration = builder.comment("Enables or disables custom tab list information").define("tabList", true);
			enableMetadataInTabList = builder.comment("Enables or disables prefixes&suffixes in the tab list").define("tabListMetadata", true);
			enableNicknamesInTabList = builder.comment("Enables or disables nicknames in the tab list").define("tabListNicknames", true);
			enableWhoisCommand = builder.comment("""
				  Enables or disables the integrated whois command
				  (If autoIntegratedNicknames is true, this setting is ignored)""").define("enableWhoisCommand", true);
			enableChatNicknameCommand = builder.comment("""
					Enables or disables the integrated nickname command
					(If autoIntegratedNicknames is true, this setting is ignored)""").define("enableIntegratedNicknames", false);
			autoEnableChatNicknameCommand = builder.comment("When true, enables the integrated nickname-related commands if FTB essentials is not present").define("autoIntegratedNicknames", true);
			maximumNicknameLength = builder.comment("Maximum allowed nickname length (for integrated nickname commands)").defineInRange("maximumNicknameLength", 50, 1, 500);
			minimumNicknameLength = builder.comment("Minimum allowed nickname length (for integrated nickname commands)").defineInRange("minimumNicknameLength", 1, 1, 500);
			builder.push("Delimiters");
			
			boldSymbol1 = builder
					.comment("Controls what symbol is used for bold formatting")
					.define("boldSymbol1", "**");
			boldSymbol2 = builder
					.comment("Controls what symbol is used for bold formatting")
					.define("boldSymbol2", "");
			italicSymbol1 = builder
					.comment("Controls what symbol is used for italic formatting")
					.define("italicSymbol1", "*");
			italicSymbol2 = builder
					.comment("Controls what symbol is used for italic formatting")
					.define("italicSymbol2", "_");
			obfuscatedSymbol1 = builder
					.comment("Controls what symbol is used for obfuscated formatting")
					.define("obfuscatedSymbol1", "~");
			obfuscatedSymbol2 = builder
					.comment("Controls what symbol is used for obfuscated formatting")
					.define("obfuscatedSymbol2", "");
			underlineSymbol1 = builder
					.comment("Controls what symbol is used for underline formatting")
					.define("underlineSymbol1", "__");
			underlineSymbol2 = builder
					.comment("Controls what symbol is used for underline formatting")
					.define("underlineSymbol2", "");
			strikethroughSymbol1 = builder
					.comment("Controls what symbol is used for strikethrough formatting")
					.define("strikethroughSymbol1", "~~");
			strikethroughSymbol2 = builder
					.comment("Controls what symbol is used for strikethrough formatting")
					.define("strikethroughSymbol2", "");
//			builder.pop();
			builder.pop();
		}
	}
}
