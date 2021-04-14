package com.nur.ipapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCheckHandler {
    static boolean toggled = true;
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    final static JsonParser parser = new JsonParser();

    public static boolean isJson(String json) {
        try {
            parser.parse(json).getAsJsonObject();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public void onOtherChat(ClientChatReceivedEvent event) {
        if (!toggled) return;
        String msg = event.message.getUnformattedText();
        if (msg.contains("IP Address") || msg.contains("Scanning") || (msg.contains("*") && msg.contains(":") && msg.contains("[") && msg.contains("]")) || msg.contains("Last used IP:")) {
            Pattern ipPattern = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})");
            Matcher m = ipPattern.matcher(msg);
            if (!m.find()) return;
            if ("".equals(Main.apiKey)) {
                if (!Main.apiKeyNotSetWarningSent) {
                    event.message.appendSibling(new ChatComponentText(EnumChatFormatting.DARK_RED + " (API Key not set)")).setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/setvpnapikey ")));
                    Main.apiKeyNotSetWarningSent = true;
                }
                return;
            }
            final String IP = m.group(0);
            final ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/alts " + IP));
            if (IPHandler.isCached(IP)) {
                APIResult cachedResult = IPHandler.getCached(IP);
                if (cachedResult != null) {
                    event.message.appendSibling(new ChatComponentText(" " + cachedResult.getDisplayNameWithParentheses()));
                    if (IPHandler.isInfoCached(IP))
                        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(IPHandler.getCachedInfo(IP))));
                    event.message.setChatStyle(style);
                    return;
                }
            }
            HttpURLConnection con = null;
            try {
                URL url = new URL("https://vpnapi.io/api/" + IP + "?key=" + Main.apiKey);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (!isJson(response.toString())) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while scanning " + IP).setChatStyle(style));
                    event.message.setChatStyle(style);
                    return;
                }
                JsonObject j = parser.parse(response.toString()).getAsJsonObject();
                String hoverText = EnumChatFormatting.RESET + "IP Information: " + EnumChatFormatting.RED + IP + "\n" + IPHandler.ipInfo(j);
                if (j.has("security")) {
                    JsonObject security = j.get("security").getAsJsonObject();
                    if (security.has("vpn") && security.has("proxy") && security.has("tor")) {
                        APIResult result = security.get("vpn").getAsBoolean() || security.get("proxy").getAsBoolean() || security.get("tor").getAsBoolean() ? APIResult.BAD : APIResult.GOOD;
                        event.message.appendSibling(new ChatComponentText(" " + result.getDisplayNameWithParentheses()));
                        event.message.setChatStyle(style);
                        IPHandler.cache(IP, result);
                    }
                } else {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while scanning " + IP + ": " + j.get("message").getAsString()).setChatStyle(style));
                    return;
                }
                style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText)));
                event.message.setChatStyle(style);
            } catch (Exception ex) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuilder errorMessage = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        errorMessage.append(inputLine);
                    }
                    in.close();
                    String j = AutoCheckHandler.parser.parse(errorMessage.toString()).getAsJsonObject().get("message").getAsString();
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while scanning " + IP + ": " + EnumChatFormatting.UNDERLINE + j).setChatStyle(style));
                } catch (Exception exception) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while scanning " + IP).setChatStyle(style));
                    exception.printStackTrace();
                }
                ex.printStackTrace();
            }
        }
    }
}
