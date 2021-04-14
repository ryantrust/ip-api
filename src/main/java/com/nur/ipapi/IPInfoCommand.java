package com.nur.ipapi;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPInfoCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "ipinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "ipinfo <ip>";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<String>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Please provide an IP Address!"));
            return;
        }
        Pattern ipPattern = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})");
        final Matcher m = ipPattern.matcher(args[0]);
        if (!m.find()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + args[0] + " is not a valid IP Address!"));
            return;
        }
        final String IP = m.group(0);
        final ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/alts " + IP));
        new Thread(new Runnable() {
            public void run() {
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

                    if (AutoCheckHandler.isNotJson(response.toString())) {
                        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(response.toString())));
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.RED + "Error while scanning " + IP).setChatStyle(style));
                        return;
                    }
                    JsonObject j = AutoCheckHandler.parser.parse(response.toString()).getAsJsonObject();

                    style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(AutoCheckHandler.gson.toJson(j))));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("" + EnumChatFormatting.WHITE + EnumChatFormatting.BOLD + "(!) " + EnumChatFormatting.WHITE + "IP Information: " + EnumChatFormatting.RED + IP).setChatStyle(style));

                    BufferedReader bufReader = new BufferedReader(new StringReader(IPHandler.ipInfo(j)));
                    String ln;
                    while ((ln = bufReader.readLine()) != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ln).setChatStyle(style));
                    }

                } catch (IOException e) {
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
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<String>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
