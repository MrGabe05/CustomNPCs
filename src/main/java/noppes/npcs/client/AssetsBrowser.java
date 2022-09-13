package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.resources.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import noppes.npcs.CustomNpcs;
import noppes.npcs.mixin.SimpleReloadableResourceManagerMixin;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AssetsBrowser {
	public boolean isRoot;
	private int depth;
	private String folder;
	public HashSet<String> folders = new HashSet<String>();
	public HashSet<String> files = new HashSet<String>();
	private final String[] extensions;

	public AssetsBrowser(String folder, String[] extensions){
		this.extensions = extensions;
		this.setFolder(folder);
	}
	
	public void setFolder(String folder){
		if(!folder.endsWith("/"))
			folder += "/";
		isRoot = folder.length() <= 1;
		this.folder = "/assets" + folder;
		depth = StringUtils.countMatches(this.folder, "/");
		getFiles();
	}
	
	public AssetsBrowser(String[] extensions){
		this.extensions = extensions;
	}

	private void getFiles() {
		folders.clear();
		files.clear();

		SimpleReloadableResourceManagerMixin simplemanager = (SimpleReloadableResourceManagerMixin) Minecraft.getInstance().getResourceManager();

		Map<String, FallbackResourceManager> map =  simplemanager.getPacks();//namespaceResourceManagers
        for(String name: map.keySet()){
        	FallbackResourceManager manager = map.get(name);
        	List<IResourcePack> list = manager.fallbacks;
        	for(IResourcePack pack : list){
				processResourcePack(pack);
        	}
        }

		for (ModFileInfo mod : ModList.get().getModFiles()) {
			File file = mod.getFile().getFilePath().toFile();
			if(file.exists())
				progressFile(file);
		}

		ResourcePackList repos = Minecraft.getInstance().getResourcePackRepository();
		repos.reload();
		Collection<ResourcePackInfo> list = repos.getAvailablePacks();
		for(ResourcePackInfo entry : list) {
			processResourcePack(entry.open());
		}

		URL url = VanillaPack.class.getResource("/assets/.mcassetsroot");
		if(url != null) {
			File f = decodeFile(url.getFile());
			if(f.isDirectory()) {
				checkFolder(new File(f,"assets"), url.getFile().length());
			}
			else {
				progressFile(f);
			}
		}

//		File file = new File(repos.getDirResourcepacks(),repos.getResourcePackName());
//		if(file.exists()){
//			progressFile(file);
//		}
		checkFolder(new File(CustomNpcs.Dir,"assets"),CustomNpcs.Dir.getAbsolutePath().length());
		//TODO fix
	}

	private File decodeFile(String url) {
		if(url.startsWith("file:")) {
			url = url.substring(5);
		}
		url = url.replace('/', File.separatorChar);
		int i = url.indexOf('!');
		if(i > 0) {
			url = url.substring(0, i);
		}
		try {
			url = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		}
		return new File(url);
	}

	private void processResourcePack(IResourcePack pack){
		if(pack instanceof LegacyResourcePackWrapper) {
			pack = ObfuscationReflectionHelper.getPrivateValue(LegacyResourcePackWrapper.class, (LegacyResourcePackWrapper)pack, "field_211854_b");//locationMap
		}
		if(pack instanceof ResourcePack){
			ResourcePack p = (ResourcePack) pack;
			File file = p.file;
			if(file != null){
				if(file.isDirectory()) {
					checkFolder(new File(file, "assets"), file.getAbsolutePath().length());
				}
				else {
					progressFile(file);
				}
			}
		}
	}

	private void progressFile(File file){
		try {
	        if (!file.isDirectory() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")))
	        {
	            ZipFile zip = new ZipFile(file);
	            Enumeration<? extends ZipEntry> entries = zip.entries();
	            while(entries.hasMoreElements()){
	                ZipEntry zipentry = entries.nextElement();
	                String entryName = zipentry.getName();
	        		checkFile(entryName);
	                
	            }
	            zip.close();
	        }
	        else if(file.isDirectory()){
	        	int length = file.getAbsolutePath().length();
	        	checkFolder(file,length);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkFolder(File file, int length){
		File[] files = file.listFiles();
		if(files == null){
			return;
		}
    	for(File f : files){
    		String name = f.getAbsolutePath().substring(length);
    		name = name.replace("\\", "/");
            if(!name.startsWith("/"))
            	name = "/" + name;
    		if(f.isDirectory() && (folder.startsWith(name) || name.startsWith(folder))){
    			checkFile(name + "/");
    			checkFolder(f, length);
    		}
    		else
    			checkFile(name);
    	}
	}
	
	private void checkFile(String name){
        if(!name.startsWith("/"))
        	name = "/" + name;
        if(!name.startsWith(this.folder)){
        	return;
        }
        String[] split = name.split("/");
        int count = split.length;
        if(count == depth + 1){
        	if(validExtension(name)){
        		files.add(split[depth]);
        	}
        }
        else if(depth + 1 < count){
        	folders.add(split[depth]);
        }
	}

	private boolean validExtension(String entryName) {
		int index = entryName.lastIndexOf(".");
		if(index < 0)
			return false;
    	String extension = entryName.substring(index + 1);
    	for(String ex : extensions){
    		if(ex.equalsIgnoreCase(extension))
    			return true;
    	}
		return false;
	}

	public String getAsset(String asset) {
		String[] split = folder.split("/");
		if(split.length < 3)
			return null;
		String texture = split[2] + ":";
		texture += folder.substring(texture.length() + 8) + asset;
		return texture;
	}

	public static String getRoot(String asset) {
		String mod = "minecraft";
		int index = asset.indexOf(":");
		if(index > 0){
			mod = asset.substring(0,index);
			asset = asset.substring(index + 1);
		}
		if(asset.startsWith("/"))
			asset = asset.substring(1);
		String location = "/" + mod + "/" + asset;
		index = location.lastIndexOf("/");
		if(index > 0)
			location = location.substring(0,index);
		return location;
	}
}
