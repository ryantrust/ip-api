package com.nur.ipapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.*;

public class MyEventHandler {
	@SubscribeEvent
    public void onOtherChat(ClientChatReceivedEvent event)
    {
		String msg = event.message.getUnformattedText();
		if(msg.contains("IP Address")||msg.contains("Scanning")||(msg.contains("*")&&msg.contains(":")&&msg.contains("[")&&msg.contains("]"))) {
			String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
			String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
			Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
			Matcher m = IP_PATTERN.matcher(msg);
			if(m.find()) {
				ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://iphub.info/?ip="+m.group(0)));
				try {
					URL url = new URL("http://v2.api.iphub.info/ip/"+m.group(0));
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					con.addRequestProperty("X-Key", "**IPHUB API KEY HERE**");
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					if(response.toString().contains("\"block\":0")) {
						//Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is "+EnumChatFormatting.GREEN+"good").setChatStyle(style));
						event.message.appendSibling(new ChatComponentText(""+EnumChatFormatting.GREEN+" (good)"));
						event.message.setChatStyle(style);
					} else if (response.toString().contains("\"block\":1")) {
						//Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is "+EnumChatFormatting.RED+"bad").setChatStyle(style));
						event.message.appendSibling(new ChatComponentText(""+EnumChatFormatting.RED+" (bad)"));
						event.message.setChatStyle(style);
					} else if (response.toString().contains("\"block\":2")) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is "+EnumChatFormatting.YELLOW+"Non-residential & residential IP (warning, may flag innocent people)").setChatStyle(style));
						event.message.setChatStyle(style);
					} else {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"Error while scanning "+m.group(0)).setChatStyle(style));
						event.message.setChatStyle(style);
					}
				} catch (Exception ex) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"Error while scanning "+m.group(0)).setChatStyle(style));
					event.message.setChatStyle(style);
				}
			};
		}
    }
}
