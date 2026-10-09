package com.rvt.bfcrmod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rvt.bfcrmod.config.ConfigHandler;
import com.rvt.bfcrmod.config.IReloadable;
import com.rvt.bfcrmod.events.ChatEventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class Parser implements IReloadable {
    private static final int MAX_SYMBOL_LENGTH = 2;
    
    private static String boldSymbol1 = "**";
    private static String boldSymbol2 = "";
    private static String italicSymbol1 = "*";
    private static String italicSymbol2 = "_";
    private static String obfuscatedSymbol1 = "~";
    private static String obfuscatedSymbol2 = "";
    private static String strikethroughSymbol1 = "~~";
    private static String strikethroughSymbol2 = "";
    private static String underlineSymbol1 = "__";
    private static String underlineSymbol2 = "";
    
    @Override
    public void reloadConfigOptions() {
        boldSymbol1 = readSymbol(ConfigHandler.config.boldSymbol1.get(), boldSymbol1);
        boldSymbol2 = readSymbol(ConfigHandler.config.boldSymbol2.get(), boldSymbol2);
        italicSymbol1 = readSymbol(ConfigHandler.config.italicSymbol1.get(), italicSymbol1);
        italicSymbol2 = readSymbol(ConfigHandler.config.italicSymbol2.get(), italicSymbol2);
        obfuscatedSymbol1 = readSymbol(ConfigHandler.config.obfuscatedSymbol1.get(), obfuscatedSymbol1);
        obfuscatedSymbol2 = readSymbol(ConfigHandler.config.obfuscatedSymbol2.get(), obfuscatedSymbol2);
        strikethroughSymbol1 = readSymbol(ConfigHandler.config.strikethroughSymbol1.get(), strikethroughSymbol1);
        strikethroughSymbol2 = readSymbol(ConfigHandler.config.strikethroughSymbol2.get(), strikethroughSymbol2);
        underlineSymbol1 = readSymbol(ConfigHandler.config.underlineSymbol1.get(), underlineSymbol1);
        underlineSymbol2 = readSymbol(ConfigHandler.config.underlineSymbol2.get(), underlineSymbol2);
    }
    
    public static MutableComponent parse(String input) {
        return parse(input, true, true, true);
    }

    public static MutableComponent parse(String input, boolean enableColors, boolean enableStyles) {
        return parse(input, enableColors, enableStyles, false);
    }

    public static MutableComponent parse(String input, boolean enableColors, boolean enableStyles, boolean enableMarkdown) {
        MutableComponent output = Component.empty();
        if(input == null || input.isEmpty()) return output;
        
        List<Marker> markers = configuredMarkers();
        ParserState state = new ParserState();
        Map<String, Integer> activeMarkers = new LinkedHashMap<>();
        StringBuilder text = new StringBuilder();
        
        for(int i = 0; i < input.length();) {
            char current = input.charAt(i);
            
            if(current == '\\') {
                if(i + 1 < input.length()) {
                    text.append(input.charAt(i + 1));
                    i += 2;
                } else {
                    text.append(current);
                    i++;
                }
                continue;
            }
            
            if(current == '&' && i + 1 < input.length()) {
                if(input.charAt(i + 1) == '#' && i + 8 <= input.length()) {
                    int rgb = parseHexColor(input, i + 2);
                    if(rgb >= 0) {
                        appendSegment(output, text, state.currentStyle());
                        if(enableColors) state.applyHexColor(rgb);
                        i += 8;
                        continue;
                    }
                }
                ChatFormatting formatting = ChatFormatting.getByCode(input.charAt(i + 1));
                if(formatting != null) {
                    appendSegment(output, text, state.currentStyle());
                    if(formatting == ChatFormatting.RESET || (formatting.isColor() ? enableColors : enableStyles))
                        state.applyLegacyFormat(formatting);
                    i += 2;
                    continue;
                }
            }
            
            Marker marker = enableMarkdown ? markerAt(input, i, markers) : null;
            if(marker != null) {
                if(isActive(activeMarkers, marker)) {
                    appendSegment(output, text, state.currentStyle());
                    close(activeMarkers, state, marker);
                    i += marker.token().length();
                    continue;
                }
                
                if(hasClosingMarker(input, i + marker.token().length(), marker, markers)) {
                    appendSegment(output, text, state.currentStyle());
                    open(activeMarkers, state, marker);
                    i += marker.token().length();
                    continue;
                }
                
                text.append(marker.token());
                i += marker.token().length();
                continue;
            }
            
            text.append(current);
            i++;
        }
        
        appendSegment(output, text, state.currentStyle());
        return output;
    }

    public static String stripFormatting(String input) {
        if(input == null) return null;
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if(c == '&' && i + 1 < input.length() && input.charAt(i + 1) == '#' && parseHexColor(input, i + 2) >= 0) i += 7;
            else if(c == '&' && i + 1 < input.length() && ChatFormatting.getByCode(input.charAt(i + 1)) != null) i++;
            else result.append(c);
        }
        return result.toString();
    }

    public static boolean containsFormatting(String input, boolean colors) {
        if(input == null) return false;
        for(int i = 0; i + 1 < input.length(); i++) {
            if(input.charAt(i) != '&') continue;
            if(input.charAt(i + 1) == '#' && parseHexColor(input, i + 2) >= 0) {
                if(colors) return true;
                i += 7;
                continue;
            }
            ChatFormatting format = ChatFormatting.getByCode(input.charAt(i + 1));
            if(format != null && (colors ? format.isColor() : !format.isColor())) return true;
        }
        return false;
    }

    public static String colorCodes() {
        return """
                
                       &fLight:   &c\\&c &e\\&e &9\\&9 &a\\&a &b\\&b &d\\&d &f\\&f &7\\&7
                       &fDark:   &4\\&4 &6\\&6 &1\\&1 &2\\&2 &3\\&3 &5\\&5 &0\\&0 &8\\&8
                       &fStyles: &l\\&l&r &n\\&n&r &o\\&o&r &m\\&m&r &k\\&k&r
                       \
                """;
    }
    
    private static String readSymbol(String configured, String fallback) {
        return configured == null || configured.length() > MAX_SYMBOL_LENGTH ? fallback : configured;
    }

    private static int parseHexColor(String input, int start) {
        if(start + 6 > input.length()) return -1;
        int rgb = 0;
        for(int i = start; i < start + 6; i++) {
            int digit = Character.digit(input.charAt(i), 16);
            if(digit < 0) return -1;
            rgb = (rgb << 4) | digit;
        }
        return rgb;
    }
    
    private static List<Marker> configuredMarkers() {
        Map<String, EnumSet<InlineFormat>> markerFormats = new LinkedHashMap<>();
        addMarker(markerFormats, boldSymbol1, InlineFormat.BOLD);
        addMarker(markerFormats, boldSymbol2, InlineFormat.BOLD);
        addMarker(markerFormats, italicSymbol1, InlineFormat.ITALIC);
        addMarker(markerFormats, italicSymbol2, InlineFormat.ITALIC);
        addMarker(markerFormats, obfuscatedSymbol1, InlineFormat.OBFUSCATED);
        addMarker(markerFormats, obfuscatedSymbol2, InlineFormat.OBFUSCATED);
        addMarker(markerFormats, strikethroughSymbol1, InlineFormat.STRIKETHROUGH);
        addMarker(markerFormats, strikethroughSymbol2, InlineFormat.STRIKETHROUGH);
        addMarker(markerFormats, underlineSymbol1, InlineFormat.UNDERLINED);
        addMarker(markerFormats, underlineSymbol2, InlineFormat.UNDERLINED);
        
        List<Marker> markers = new ArrayList<>();
        for(Map.Entry<String, EnumSet<InlineFormat>> entry : markerFormats.entrySet()) {
            markers.add(new Marker(entry.getKey(), List.copyOf(entry.getValue())));
        }
        
        markers.sort(Comparator.comparingInt((Marker marker) -> marker.token().length()).reversed());
        return markers;
    }
    
    private static void addMarker(Map<String, EnumSet<InlineFormat>> markers, String token, InlineFormat format) {
        if(token == null || token.isEmpty()) return;
        markers.computeIfAbsent(token, ignored -> EnumSet.noneOf(InlineFormat.class)).add(format);
    }
    
    private static void appendSegment(MutableComponent output, StringBuilder text, Style style) {
        if(text.isEmpty()) return;
        output.append(Component.literal(text.toString()).setStyle(style));
        text.setLength(0);
    }
    
    private static boolean isActive(Map<String, Integer> activeMarkers, Marker marker) {
        return activeMarkers.getOrDefault(marker.token(), 0) > 0;
    }
    
    private static void open(Map<String, Integer> activeMarkers, ParserState state, Marker marker) {
        activeMarkers.merge(marker.token(), 1, Integer::sum);
        state.open(marker);
    }
    
    private static void close(Map<String, Integer> activeMarkers, ParserState state, Marker marker) {
        int depth = activeMarkers.getOrDefault(marker.token(), 0);
        if(depth <= 1) {
            activeMarkers.remove(marker.token());
        } else {
            activeMarkers.put(marker.token(), depth - 1);
        }
        state.close(marker);
    }
    
    private static Marker markerAt(String input, int index, List<Marker> markers) {
        for(Marker marker : markers) {
            if(startsWith(input, index, marker.token())) return marker;
        }
        return null;
    }
    
    private static boolean hasClosingMarker(String input, int start, Marker target, List<Marker> markers) {
        for(int i = start; i < input.length();) {
            char current = input.charAt(i);
            if(current == '\\') {
                i += i + 1 < input.length() ? 2 : 1;
                continue;
            }
            
            Marker marker = markerAt(input, i, markers);
            if(marker != null) {
                if(marker.token().equals(target.token())) return true;
                i += marker.token().length();
                continue;
            }
            
            i++;
        }
        return false;
    }
    
    private static boolean startsWith(String input, int index, String token) {
        return index + token.length() <= input.length() && input.startsWith(token, index);
    }
    
    private enum InlineFormat {
        BOLD,
        ITALIC,
        UNDERLINED,
        OBFUSCATED,
        STRIKETHROUGH
    }
    
    private record Marker(String token, List<InlineFormat> formats) {}
    
    private static class ParserState {
        private final ChatFormatting defaultColor = configuredDefaultColor();
        private Style legacyStyle = Style.EMPTY.withColor(defaultColor);
        private int boldDepth;
        private int italicDepth;
        private int underlinedDepth;
        private int obfuscatedDepth;
        private int strikethroughDepth;
        
        private void applyLegacyFormat(ChatFormatting formatting) {
            legacyStyle = formatting == ChatFormatting.RESET
                ? Style.EMPTY.withColor(defaultColor)
                : legacyStyle.applyLegacyFormat(formatting);
        }

        private void applyHexColor(int rgb) {
            legacyStyle = legacyStyle.withColor(rgb);
        }

        private static ChatFormatting configuredDefaultColor() {
            ChatFormatting color = ChatEventHandler.getChatMessageColor();
            return color == null ? ChatFormatting.WHITE : color;
        }
        
        private void open(Marker marker) {
            for(InlineFormat format : marker.formats()) {
                open(format);
            }
        }
        
        private void close(Marker marker) {
            for(InlineFormat format : marker.formats()) {
                close(format);
            }
        }
        
        private void open(InlineFormat format) {
            switch(format) {
                case BOLD -> boldDepth++;
                case ITALIC -> italicDepth++;
                case UNDERLINED -> underlinedDepth++;
                case OBFUSCATED -> obfuscatedDepth++;
                case STRIKETHROUGH -> strikethroughDepth++;
            }
        }
        
        private void close(InlineFormat format) {
            switch(format) {
                case BOLD -> boldDepth = Math.max(0, boldDepth - 1);
                case ITALIC -> italicDepth = Math.max(0, italicDepth - 1);
                case UNDERLINED -> underlinedDepth = Math.max(0, underlinedDepth - 1);
                case OBFUSCATED -> obfuscatedDepth = Math.max(0, obfuscatedDepth - 1);
                case STRIKETHROUGH -> strikethroughDepth = Math.max(0, strikethroughDepth - 1);
            }
        }
        
        private Style currentStyle() {
            Style style = legacyStyle;
            if(boldDepth > 0) style = style.withBold(true);
            if(italicDepth > 0) style = style.withItalic(true);
            if(underlinedDepth > 0) style = style.withUnderlined(true);
            if(obfuscatedDepth > 0) style = style.withObfuscated(true);
            if(strikethroughDepth > 0) style = style.withStrikethrough(true);
            return style;
        }
    }
}
