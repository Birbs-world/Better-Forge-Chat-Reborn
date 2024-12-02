package com.jeremiahbl.bfcmod;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TextFormatter {
	public static final String RESET_ALL_FORMAT = "&r";
	
	public static final String BOLD_FORMAT          = "&l";
	public static final String UNDERLINE_FORMAT     = "&n";
	public static final String ITALIC_FORMAT        = "&o";
	public static final String OBFUSCATED_FORMAT    = "&k";
	public static final String STRIKETHROUGH_FORMAT = "&m";
	
	public static final String COLOR_BLACK =        "&0";
	public static final String COLOR_DARK_BLUE =    "&1";
	public static final String COLOR_DARK_GREEN =   "&2";
	public static final String COLOR_DARK_AQUA =    "&3";
	public static final String COLOR_DARK_RED =     "&4";
	public static final String COLOR_DARK_PURPLE =  "&5";
	public static final String COLOR_GOLD =         "&6";
	public static final String COLOR_GRAY =         "&7";
	public static final String COLOR_DARK_GRAY =    "&8";
	public static final String COLOR_BLUE =         "&9";
	public static final String COLOR_GREEN =        "&a";
	public static final String COLOR_AQUA =         "&b";
	public static final String COLOR_RED =          "&c";
	public static final String COLOR_LIGHT_PURPLE = "&d";
	public static final String COLOR_YELLOW =       "&e";
	public static final String COLOR_WHITE =        "&f";
	
	public static MutableComponent stringToFormattedText(String msg) {
		return stringToFormattedText(msg, true, true);
	}
	public static MutableComponent stringToFormattedText(String msg, boolean enableColors, boolean enableStyles) {
		if(msg == null) return null;
		MutableComponent newMsg = Component.empty();
		boolean nextIsStyle = false;
		String curStr = "";
		ChatFormatting curColor = ChatFormatting.WHITE;
		byte curStyle = 0;
		for(int i = 0; i < ((CharSequence) msg).length(); i++) {
			char c = ((CharSequence) msg).charAt(i);
			if(c == '&') {
				if(nextIsStyle) {
					nextIsStyle = false;
					curStr += "&";
				} else nextIsStyle = true;
			} else if(nextIsStyle) {
				MutableComponent tmp = BitwiseStyling.makeEncapsulatingTextComponent(curStr, enableStyles ? curStyle : 0);
				tmp.withStyle(curColor);
				newMsg.append(tmp);
				if(enableColors)
					curColor = getColor(c, curColor);
				curStr = "";

				if(c == 'r') {
					curColor = ChatFormatting.WHITE;
					curStyle = 0;
				} else curStyle |= BitwiseStyling.getStyleBit(c);
				nextIsStyle = false;
			} else curStr += c;
		}
		if(!curStr.isEmpty()) {
			MutableComponent tmp = BitwiseStyling.makeEncapsulatingTextComponent(curStr, enableStyles ? curStyle : 0);
			tmp.withStyle(curColor);
			newMsg.append(tmp);
		}
		return newMsg;
	}
	public static String removeTextFormatting(String msg) {
		if(msg == null) return null;
		String newMsg = "", curStr = "";
		boolean nextIsStyle = false;
		for(int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if(c == '&') {
				if(nextIsStyle) {
					nextIsStyle = false;
					curStr += "&";
				} else nextIsStyle = true;
			} else if(nextIsStyle) {
				if(isColorOrStyle(c)) {
					newMsg += curStr;
					curStr = "";
				} else curStr += ("&" + c);
				nextIsStyle = false;
			} else curStr += c;
		}
		if(!curStr.isEmpty())
			newMsg += curStr;
		return newMsg;
	}

	public static boolean messageContainsColorsOrStyles(String msg, boolean checkColors) {
		boolean checkNext = false;
		for(int i = 0; i < msg.length(); i++) {
			char c = msg.charAt(i);
			if(c == '&') {
                checkNext = !checkNext;
			} else if(checkNext) {
				if(checkColors) { if(isColor(c)) return true; }
				else { if(isStyle(c)) return true; }
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

	private static ChatFormatting getColor(char c, ChatFormatting cur) {
            return switch (c) {
                case '0' -> ChatFormatting.BLACK;
                case '1' -> ChatFormatting.DARK_BLUE;
                case '2' -> ChatFormatting.DARK_GREEN;
                case '3' -> ChatFormatting.DARK_AQUA;
                case '4' -> ChatFormatting.DARK_RED;
                case '5' -> ChatFormatting.DARK_PURPLE;
                case '6' -> ChatFormatting.GOLD;
                case '7' -> ChatFormatting.GRAY;
                case '8' -> ChatFormatting.DARK_GRAY;
                case '9' -> ChatFormatting.BLUE;
                case 'a' -> ChatFormatting.GREEN;
                case 'b' -> ChatFormatting.AQUA;
                case 'c' -> ChatFormatting.RED;
                case 'd' -> ChatFormatting.LIGHT_PURPLE;
                case 'e' -> ChatFormatting.YELLOW;
                case 'f' -> ChatFormatting.WHITE;
                case 'r' -> ChatFormatting.WHITE;
                default -> cur;
            }; // Reset
    }
	private static boolean isColorOrStyle(char c) {
		return isColor(c) || isStyle(c);
	}
	private static boolean isColor(char c) {
		if(c >= '0' && c <= '9') return true;
        return c >= 'a' && c <= 'f';
    }
    private static boolean isStyle(char c) {
    	if(c >= 'k' && c <= 'o') return true;
        return c == 'r';
    }
    
}
