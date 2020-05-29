package com.nur.ipapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.regex.*;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class MyEventHandler {
	@SubscribeEvent
    public void onOtherChat(ClientChatReceivedEvent event)
    {
		String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
		String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
		Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
		Matcher m = IP_PATTERN.matcher(event.message.getUnformattedText());
		if(m.find()) {
			try {
				URL url = new URL("http://v2.api.iphub.info/ip/"+m.group(0));
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				con.addRequestProperty("X-Key", "OT\\EwNTpLVHdMV1dYWWt2OTFhbDZ3QVhBRENQT0p2RnhYT3hlVA==");
			    int responseCode = con.getResponseCode();
			    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			    String inputLine;
			    StringBuffer response = new StringBuffer();
			    while ((inputLine = in.readLine()) != null) {
			    	response.append(inputLine);
			    }
			    in.close();
			    if(response.toString().contains("\"block\":0")) {
			    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is a "+EnumChatFormatting.GREEN+"Residential or business IP (good)"));
			    } else if (response.toString().contains("\"block\":1")) {
			    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is a "+EnumChatFormatting.DARK_RED+"Non-residential IP (hosting provider, proxy, etc.)"));
			    } else if (response.toString().contains("\"block\":2")) {
			    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+m.group(0)+EnumChatFormatting.GOLD+" is a "+EnumChatFormatting.YELLOW+"Non-residential & residential IP (warning, may flag innocent people)"));
			    } else {
			    	Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+EnumChatFormatting.BOLD+"(!) Error while scanning "+m.group(0)));
			    }
			} catch (Exception ex) {
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(""+EnumChatFormatting.RED+"(!) Error while scanning "+m.group(0)));
			}
		};
    }
}
