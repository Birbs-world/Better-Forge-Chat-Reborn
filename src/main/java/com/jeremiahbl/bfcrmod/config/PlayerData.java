package com.jeremiahbl.bfcrmod.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import com.jeremiahbl.bfcrmod.BetterForgeChat;

public class PlayerData {
	public static final Map<UUID, PlayerData> map = new HashMap<>();
	public static final String playerDataFileName = "bfcrmod.playerdata";
	
	public final UUID uuid;
	public String nickname = null;
	
	public PlayerData(UUID uuid) {
		this.uuid = uuid;
	}
	
	public static void setNickname(UUID uuid, String nickName) {
		PlayerData dat = map.get(uuid);
		if(dat == null)
			dat = new PlayerData(uuid);
		dat.nickname = nickName;
		map.put(uuid, dat);
		BetterForgeChat.LOGGER.debug("adding {} to map",dat);
	}
	public static String getNickname(UUID id) {
		PlayerData dat = map.get(id);
		return dat == null ? null : dat.nickname;
	}
	public static UUID whoIs(String nickName) {
		return null;
	}
	
	@Override public String toString() {
		String out = "";
		out += "[PlayerDataEntry]\n";
		out += "\tUUID: " + encodeStr(uuid.toString()) + '\n';
		out += "\tNickname: " + encodeStr(nickname) + '\n';
		return out;
	}
	public static ArrayList<PlayerData> fromString(String str) {
		if(str == null) return null;
		String[] strs = (str.trim().split("\n"));
		ArrayList<PlayerData> out = new ArrayList<PlayerData>();
		BetterForgeChat.LOGGER.debug("map string array: \n{}", (Object[]) strs);
		BetterForgeChat.LOGGER.debug("string array length: {}",strs.length);

		if(strs.length > 2 && strs[0].contentEquals("[PlayerDataEntry]")) {
			for(int data = 1; data < strs.length; data ++) {
				BetterForgeChat.LOGGER.debug("data number {}",data);
				BetterForgeChat.LOGGER.debug("data pairs: {}", strs[data]);
				UUID uuid = null;
				String nick = null;

				String[] pairs = strs[data].trim().split(":");
				BetterForgeChat.LOGGER.debug("processing UUID pair: {}", (Object) pairs);
				pairs[0] = pairs[0].trim();
				try {
					if (pairs[0].contentEquals("UUID")){
						uuid = UUID.fromString(decodeStr(pairs[1]));
						pairs = strs[data+1].trim().split(":");
						pairs[1] = pairs[1].trim();
						BetterForgeChat.LOGGER.debug("processing Nickname pair: {}", (Object) pairs);
						if (pairs[0].contentEquals("Nickname"))
							nick = decodeStr(pairs[1]);
						}
					} catch (NullPointerException npe) {
						BetterForgeChat.LOGGER.error("Failed to parse PlayerData: \"" + strs[data] + "\"");
					}

				if(uuid != null) {
					PlayerData player = new PlayerData(uuid);
					player.nickname = nick;
					BetterForgeChat.LOGGER.debug("adding player to map: {}",player);
					out.add(player);
					data++;
				}
				BetterForgeChat.LOGGER.debug("new data number {}",data);
			}


		} else return null;
        return out;
    }


	public static void loadFromDir(File playerDirectory){
		File dataFile = new File(playerDirectory, playerDataFileName);
		try {
			FileInputStream fis = new FileInputStream(dataFile);
			String Input = new String(fis.readAllBytes());
			BetterForgeChat.LOGGER.debug("Current open file content: \n {}",Input);
			ArrayList<PlayerData> playerlist = PlayerData.fromString(Input);
			BetterForgeChat.LOGGER.debug("fromstring output: {}", playerlist);
			for(int player =0;player < playerlist.size();player++) {
				PlayerData pdata = playerlist.get(0);
				if (pdata != null) {
					map.put(pdata.uuid, pdata);
					BetterForgeChat.LOGGER.debug("loaded playerData \n {} from bfcr.playerData", pdata);
				} else {
					BetterForgeChat.LOGGER.debug("failed to load playerData \n {} from bfcr.playerData", pdata);
				}
			}
			/*
			Scanner scn = new Scanner(Input).useDelimiter("[PlayerDataEntry]\n");
			while (scn.hasNext()){
				PlayerData pdat = PlayerData.fromString(scn.next());
				if(pdat != null){
                    map.put(pdat.uuid, pdat);
					BetterForgeChat.LOGGER.debug("loaded playerData \n {} from bfcr.playerData",pdat);
                }else{
					BetterForgeChat.LOGGER.debug("failed to load playerData \n {} from bfcr.playerData",pdat);
				}
			}
			scn.close();
			fis.close();
			*/
		} catch(IOException ioe) {
            BetterForgeChat.LOGGER.error("Failed to load {}", dataFile.getAbsolutePath());
		}
	}
	public static void saveToDir(File playerDirectory) {
		BetterForgeChat.LOGGER.debug("current playerdata map: {}",map);
		File dataFile = new File(playerDirectory, playerDataFileName);
		try {
			FileOutputStream fos = new FileOutputStream(dataFile);
			for(PlayerData dat : map.values())
				if(dat != null) {
                    fos.write(dat.toString().getBytes());
					BetterForgeChat.LOGGER.debug("saved player data \n {} to bfcr.player Data",dat);
                }
			fos.close();
		} catch(IOException ioe) {
            BetterForgeChat.LOGGER.error("Failed to save {}", dataFile.getAbsolutePath());
		}
	}
	public static String encodeStr(String str) {
		if(str == null) return "null";
		String newStr = "";
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if((c >= ' ' && c <= '~') && c != '\"' && c != '\\') newStr += c;
			else {
				String hex = Integer.toHexString(c);
				if(hex.length() < 4) hex = "0".repeat(4 - hex.length()) + hex;
				newStr += "\\u" + hex;
			}
		}
		return "\"" + newStr + "\"";
	}
	public static String decodeStr(String str) {
		if(str == null) return null;
		str = str.trim();
		if(str.startsWith("\"") && str.endsWith("\"") && str.length() > 1) {
			String out = "";
			for(int i = 1; i < str.length() - 1; i++) {
				char c = str.charAt(i);
				if(c == '\\' && i < str.length() - 6 && str.charAt(i + 1) == 'u') {
					String hex = str.substring(i + 2, i + 6);
					out += (char) Integer.parseUnsignedInt(hex, 16);
					i += 5;
				} else out += c;
			}
			return out;
		} else return null;
	}
}
