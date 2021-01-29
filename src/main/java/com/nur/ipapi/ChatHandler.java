package com.nur.ipapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.*;

public class ChatHandler {
	static boolean toggled = true;
	Gson gson = new Gson();
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
    public void onOtherChat(ClientChatReceivedEvent event)
    {
    	if(!toggled)return;
		String msg = event.message.getUnformattedText();
		if(msg.contains("IP Address")||msg.contains("Scanning")||(msg.contains("*")&&msg.contains(":")&&msg.contains("[")&&msg.contains("]"))||msg.contains("Last used IP:")) {
			String zeroTo255 = "(25[0-5]|2[0-4][0-9]|[01]?[0-9]{1,2})";
			String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
			Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
			Matcher m = IP_PATTERN.matcher(msg);
			if(m.find()) {
				//ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://iphub.info/?ip="+m.group(0)));
				ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/alts "+m.group(0)));
				try {
					URL url = new URL("http://v2.api.iphub.info/ip/"+m.group(0));
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					con.addRequestProperty("X-Key", Main.apiKey);
					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuilder response = new StringBuilder();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					if(!isJson(response.toString())) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"Error while scanning "+m.group(0)).setChatStyle(style));
						event.message.setChatStyle(style);
						return;
					}
					JsonObject j = parser.parse(response.toString()).getAsJsonObject();
					switch (j.get("block").getAsInt()) {
						case 0:
							event.message.appendSibling(new ChatComponentText(""+EnumChatFormatting.GREEN+" (good)"));
							event.message.setChatStyle(style);
							break;
						case 1:
							event.message.appendSibling(new ChatComponentText(""+EnumChatFormatting.RED+" (bad)"));
							event.message.setChatStyle(style);
							break;
						case 2:
							Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is "+EnumChatFormatting.YELLOW+"Non-residential & residential IP (warning, may flag innocent people)").setChatStyle(style));
							event.message.setChatStyle(style);
							break;
						default:
							Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"Error while scanning "+m.group(0)).setChatStyle(style));
							event.message.setChatStyle(style);
					}
				} catch (Exception ex) {
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) "+EnumChatFormatting.RED+"Error while scanning "+m.group(0)).setChatStyle(style));
					event.message.setChatStyle(style);
					ex.printStackTrace();
				}
			}
		}
    }
}
