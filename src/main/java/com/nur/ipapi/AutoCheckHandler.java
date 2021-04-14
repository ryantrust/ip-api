package com.nur.ipapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public static boolean isNotJson(String json) {
        try {
            parser.parse(json).getAsJsonObject();
        } catch (Exception ex) {
            return true;
        }
        return false;
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
            APIResult result;
            String ipInfo = "";
            if (IPHandler.isCached(IP) && IPHandler.isInfoCached(IP)) {
                result = IPHandler.getCached(IP);
                ipInfo = IPHandler.getCachedInfo(IP);
            } else {
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
                    if (isNotJson(response.toString())) {
                        result = APIResult.ERROR;
                    } else {
                        JsonObject j = parser.parse(response.toString()).getAsJsonObject();
                        ipInfo = IPHandler.ipInfo(j);
                        if (j.has("security")) {
                            JsonObject security = j.get("security").getAsJsonObject();
                            if (security.has("vpn") && security.has("proxy") && security.has("tor")) {
                                result = security.get("vpn").getAsBoolean() || security.get("proxy").getAsBoolean() || security.get("tor").getAsBoolean() ? APIResult.BAD : APIResult.GOOD;
                                IPHandler.cache(IP, result);
                                IPHandler.cacheInfo(IP, ipInfo);
                            } else {
                                result = APIResult.ERROR;
                            }
                        } else {
                            result = APIResult.ERROR;
                            ipInfo = "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Error: " + EnumChatFormatting.RED + j.get("message").getAsString();
                        }
                    }
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
                        ipInfo = "" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + " * " + EnumChatFormatting.GRAY + "Error: " + EnumChatFormatting.RED + j;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    ex.printStackTrace();
                    result = APIResult.ERROR;
                }
            }
            if (result == null) result = APIResult.ERROR;
            event.message.appendSibling(new ChatComponentText(" " + result.getDisplayNameWithParentheses()));
            if (!"".equals(ipInfo))
                style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.RESET + "IP Information: " + EnumChatFormatting.RED + IP + "\n" + ipInfo)));
            event.message.setChatStyle(style);
        }
    }
}
