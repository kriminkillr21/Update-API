package modUpdater;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderWorldGenerator;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * @author krimin_killr21
 */
public class Updater {
	
	public static Map<Integer, ModContainer> modListId = new HashMap<Integer, ModContainer>();
	public static Map<Integer, String> modListWeb = new HashMap<Integer, String>();
	public static Map<Integer, String> modListMesOut = new HashMap<Integer, String>();
	public static Map<Integer, String> modListMesUp = new HashMap<Integer, String>();
	public static Minecraft mc = ModLoader.getMinecraftInstance();
	
	/**
	 * modc should be an instance of the main mod file.<br><br>
	 * 
	 * web should be the url of the webpage you want it to scan for the version number. <br><br>
	 * 
	 * mesOut should be the message you want the game to display when it detects that it's out of date.<br><br>
	 * 
	 * mesUp should be the message you want the game to display when it's up-to-date.<br><br>
	 * 
	 * <strong>Note:</strong> $1 will be replaced with the current version number of your mod(Format: x.x). $2 will be replaced with the name of the mod. $3 will be replace with
	 * the current version of Minecraft (Format: Minecraft x.x.x). $4 will be replaced with the current version of Forge Mod Loader (Format: x.x.x.xxx).<br><br>
	 * 
	 * @author krimin_killr21
	 */
	public static void Subscribe (Class modc, String web, String mesOut, String mesUp) {
		
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		
		List<ModContainer> mods = Loader.instance().getActiveModList();
		for(int i = 0; i < mods.size(); i++){
			if(mods.get(i).getMod() != null){
				if(mods.get(i).getMod().getClass().equals(modc)){
					int id;
					if(modListId.isEmpty()){
						id = 0;
					} else {
						id = modListId.size();	
					}
					modListId.put(id, mods.get(i));
		    		modListWeb.put(id, web);
					modListMesOut.put(id, mesOut);	
					modListMesUp.put(id, mesUp);	
				}
			}
		}
	}
	
	public static void run() {
		
		for(int i = 0; i < modListId.size(); i++){
		
			ModContainer mod;
			String web = modListWeb.get(i);
			String mesOut = modListMesOut.get(i);
			String mesUp = modListMesUp.get(i);
			mod = modListId.get(i);
			
		    InputStream is = null;
		    boolean upToDate = false;
		    
			try { 
		          URL url = new URL(web);
		          is = url.openStream();
		          DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
		          String line;
		          while ((line = dis.readLine()) != null)
		            if (line.contains(mod.getDisplayVersion())) {
		            	upToDate = true;
		            }
		            else {
		            	
		            }
		      } catch (MalformedURLException mue) {
		        mue.printStackTrace();
		      } catch (IOException ioe) {
		        ioe.printStackTrace();
		      } finally {
		        try {
		          is.close();
		        }
		        catch (IOException ioe) {
		        }
		      }
			
			mesUp = mesUp.replace("$1", mod.getVersion());
			mesUp = mesUp.replace("$2", mod.getName());
			mesUp = mesUp.replace("$3", Loader.instance().getMCVersionString());
			mesUp = mesUp.replace("$4", Loader.instance().getFMLVersionString());
			mesOut = mesOut.replace("$1", mod.getVersion());
			mesOut = mesOut.replace("$2", mod.getName());
			mesOut = mesOut.replace("$3", Loader.instance().getMCVersionString());
			mesOut = mesOut.replace("$4", Loader.instance().getFMLVersionString());
			
			if(upToDate){
				mc.thePlayer.sendChatToPlayer(mesUp);
			} else {
				mc.thePlayer.sendChatToPlayer(mesOut);
			}
			
		}
		
	}

}
