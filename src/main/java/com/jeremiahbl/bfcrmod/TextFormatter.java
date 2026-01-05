package com.jeremiahbl.bfcrmod;


import com.jeremiahbl.bfcrmod.events.ChatEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Objects;

public final class TextFormatter {
	public static final String RESET_ALL_FORMAT = "&r";

	public static final String BOLD_FORMAT = "&l";
	public static final String UNDERLINE_FORMAT = "&n";
	public static final String ITALIC_FORMAT = "&o";
	public static final String OBFUSCATED_FORMAT = "&k";
	public static final String STRIKETHROUGH_FORMAT = "&m";

	public static final String COLOR_BLACK = "&0";
	public static final String COLOR_DARK_BLUE = "&1";
	public static final String COLOR_DARK_GREEN = "&2";
	public static final String COLOR_DARK_AQUA = "&3";
	public static final String COLOR_DARK_RED = "&4";
	public static final String COLOR_DARK_PURPLE = "&5";
	public static final String COLOR_GOLD = "&6";
	public static final String COLOR_GRAY = "&7";
	public static final String COLOR_DARK_GRAY = "&8";
	public static final String COLOR_BLUE = "&9";
	public static final String COLOR_GREEN = "&a";
	public static final String COLOR_AQUA = "&b";
	public static final String COLOR_RED = "&c";
	public static final String COLOR_LIGHT_PURPLE = "&d";
	public static final String COLOR_YELLOW = "&e";
	public static final String COLOR_WHITE = "&f";

	public static MutableComponent stringToFormattedText(String msg) {
		return stringToFormattedText(msg, true, true);
	}

	public static MutableComponent stringToFormattedText(String msg, boolean enableColors, boolean enableStyles) {
		TextColor curColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
		String DefaultColor = ChatEventHandler.getChatMessageColor();
		BetterForgeChat.LOGGER.debug("default Color: {}", DefaultColor);
		if (!DefaultColor.isEmpty())
			curColor = TextColor.fromLegacyFormat(Objects.requireNonNullElse(ChatFormatting.getByName(DefaultColor), ChatFormatting.RESET));

		if (curColor == null) {
			curColor = TextColor.fromLegacyFormat(ChatFormatting.WHITE);
			BetterForgeChat.LOGGER.error("Chat color in Config is invalid, please check BFCR config file");
			BetterForgeChat.LOGGER.error("Resetting Global chat message color to White");
		}

		BetterForgeChat.LOGGER.debug("final chat color: {}", curColor != null ? curColor.toString() : "null");
		if (msg == null) return null;
		MutableComponent newMsg = Component.empty();

		String[] splittedMessage = split(msg);

		for (String messagePart : splittedMessage) {
			if (messagePart.isEmpty()) {
				continue;
			}
			if (messagePart.charAt(0) == '&' && messagePart.charAt(1) != '#') {
				Style style = getStyle(messagePart.charAt(1), newMsg.getSiblings().isEmpty() ? Style.EMPTY : newMsg.getSiblings().get(newMsg.getSiblings().size()-1).getStyle());
				newMsg.append(permCheckAndMessageToAppend(enableColors, enableStyles, messagePart, style, false));
			} else if (messagePart.length() > 7 && messagePart.charAt(0) == '&' && messagePart.charAt(1) == '#') {
				String hex = messagePart.substring(1, 8);
				newMsg.append(permCheckAndMessageToAppend(enableColors, enableStyles, messagePart, Style.EMPTY.withColor(TextColor.parseColor(hex)), true));
			} else {
				newMsg.append(messagePart);
			}
		}


		return newMsg;
	}

	/**
	 *
	 * @param enableColors permissions for colors
	 * @param enableStyles permissions for styles
	 * @param messagePart  part of the message (see: return of split())
	 * @param style        Style to use for Component
	 * @param hex          if the messagePart will be hex
	 * @return Component to append to newMsg
	 */
	private static Component permCheckAndMessageToAppend(boolean enableColors, boolean enableStyles, String messagePart, Style style, boolean hex) {
		if (enableColors && enableStyles) {
			if (hex) {
				return Component.literal(messagePart.replaceFirst("&#[0-9A-Fa-f]{6}", "")).withStyle(style);
			} else {
				return Component.literal(messagePart.replaceFirst("&[0-9a-fk-or]", "")).withStyle(style);
			}
		} else if (enableColors && !enableStyles) {
			if (hex) {
				return Component.literal(messagePart.replaceFirst("&#[0-9A-Fa-f]{6}", "")).withStyle(style);
			} else {
				return Component.literal(messagePart.replaceFirst("&[0-9a-fr]", "")).withStyle(removeStyle(style));
			}
		} else if (!enableColors && enableStyles && !hex) {
			return Component.literal(messagePart.replaceFirst("&[k-or]", "")).withStyle(removeColor(style));
		} else {
			return Component.literal(messagePart);
		}
	}

	/**
	 * splits msg by hex colors, legacy colors, and styles.
	 * each element of the array will begin with a color code (e.g. &5), hex code (e.g. #AABBCC), or style code (e.g. &l)
	 *
	 * @param msg the message that should be split
	 * @return String array split by hex colors, legacy colors, and styles
	 */
	private static String[] split(String msg) {
		return msg.split("(?=(&#[0-9A-Fa-f]{6}|&[0-9a-fk-or]))");
	}

	private static Style removeStyle(Style style) {
		return style.withBold(false).withStrikethrough(false).withObfuscated(false).withItalic(false).withUnderlined(false);
	}

	private static Style removeColor(Style style) {
		Style tmp = Style.EMPTY.withBold(style.isBold()).withUnderlined(style.isUnderlined()).withItalic(style.isItalic()).withObfuscated(style.isObfuscated()).withStrikethrough(style.isStrikethrough());
		return style.withColor(ChatFormatting.WHITE).withBold(tmp.isBold()).withUnderlined(tmp.isUnderlined()).withItalic(tmp.isItalic()).withObfuscated(tmp.isObfuscated()).withStrikethrough(tmp.isStrikethrough());
	}


	public static String removeTextFormatting(String msg) {
		if (msg == null) return null;
		StringBuilder newMsg = new StringBuilder();
		StringBuilder curStr = new StringBuilder();
		boolean nextIsStyle = false;
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (c == '&') {
				if (nextIsStyle) {
					nextIsStyle = false;
					curStr.append("&");
				} else nextIsStyle = true;
			} else if (nextIsStyle) {
				if (isColorOrStyle(c)) {
					newMsg.append(curStr);
					curStr = new StringBuilder();
				} else curStr.append("&").append(c);
				nextIsStyle = false;
			} else curStr.append(c);
		}
		if (!curStr.isEmpty())
			newMsg.append(curStr);
		return newMsg.toString();
	}

	public static boolean messageContainsColorsOrStyles(String msg, boolean checkColors) {
		boolean checkNext = false;
		for (int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if (c == '&') {
				checkNext = !checkNext;
			} else if (checkNext) {
				if (checkColors) {
					if (isColor(c)) return true;
				} else {
					if (isStyle(c)) return true;
				}
			}
		}
		return false;
	}

	public static String colorString() {
		return """
                &fLight:  &c&&c &e&&e &9&&9 &a&&a &b&&b &d&&d &f&&f &7&&7
                &fDark:   &4&&4 &6&&6 &1&&1 &2&&2 &3&&3 &5&&5 &0&&0 &8&&8
                &fStyles: &l&&l&r &n&&n&r &o&&o&r &m&&m&r &k&&k&r
                """;
	}

	private static Style getStyle(char c, Style currentStyle) {
		return switch (c) {
			case '0' -> Style.EMPTY.withColor(ChatFormatting.BLACK);
			case '1' -> Style.EMPTY.withColor(ChatFormatting.DARK_BLUE);
			case '2' -> Style.EMPTY.withColor(ChatFormatting.DARK_GREEN);
			case '3' -> Style.EMPTY.withColor(ChatFormatting.DARK_AQUA);
			case '4' -> Style.EMPTY.withColor(ChatFormatting.DARK_RED);
			case '5' -> Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE);
			case '6' -> Style.EMPTY.withColor(ChatFormatting.GOLD);
			case '7' -> Style.EMPTY.withColor(ChatFormatting.GRAY);
			case '8' -> Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
			case '9' -> Style.EMPTY.withColor(ChatFormatting.BLUE);
			case 'a' -> Style.EMPTY.withColor(ChatFormatting.GREEN);
			case 'b' -> Style.EMPTY.withColor(ChatFormatting.AQUA);
			case 'c' -> Style.EMPTY.withColor(ChatFormatting.RED);
			case 'd' -> Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
			case 'e' -> Style.EMPTY.withColor(ChatFormatting.YELLOW);
			case 'f', 'r' -> Style.EMPTY.withColor(ChatFormatting.WHITE);

			case 'l' -> currentStyle.withBold(true);
			case 'n' -> currentStyle.withUnderlined(true);
			case 'o' -> currentStyle.withItalic(true);
			case 'k' -> currentStyle.withObfuscated(true);
			case 'm' -> currentStyle.withStrikethrough(true);
			default -> currentStyle;
		}; // Reset
	}


	private static boolean isColorOrStyle(char c) {
		return isColor(c) || isStyle(c);
	}

	private static boolean isColor(char c) {
		if (c >= '0' && c <= '9') return true;
		return c >= 'a' && c <= 'f';
	}

	private static boolean isStyle(char c) {
		if (c >= 'k' && c <= 'o') return true;
		return c == 'r';
	}

}