package noppes.npcs.shared.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.packs.DelegatingResourcePack;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import noppes.npcs.mixin.DelegatingResourcePackMixin;
import noppes.npcs.mixin.LegacyResourcePackWrapperMixin;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AssetsFinder {

    private static final List<ResourceLocation> list = new ArrayList<>();
    private static String root;
    private static String type;

    public static List<ResourceLocation> find(String root, String type){
        AssetsFinder.root = root;
        AssetsFinder.type = type;
        list.clear();

        SimpleReloadableResourceManager simplemanager = (SimpleReloadableResourceManager) Minecraft.getInstance().getResourceManager();
        simplemanager.listPacks().forEach(AssetsFinder::processResourcePack);


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

        return list;
    }
    private static File decodeFile(String url) {
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
        } catch (UnsupportedEncodingException ignored) {
        }
        return new File(url);
    }

    private static void processResourcePack(IResourcePack pack){
        if(pack instanceof LegacyResourcePackWrapper) {
            pack = ((LegacyResourcePackWrapperMixin)pack).getSource();
        }

        if(pack instanceof DelegatingResourcePack){
            DelegatingResourcePackMixin m = (DelegatingResourcePackMixin) pack;
            m.getAssetPacks().values().forEach(t -> t.forEach(AssetsFinder::processResourcePack));
        }
        else if(pack instanceof ResourcePack){
            try {
                File file = pack instanceof ModFileResourcePack ? ((ModFileResourcePack)pack).getModFile().getFilePath().toFile() : ((ResourcePack)pack).file;
                if(file != null){
                    if(file.isDirectory()) {
                        checkFolder(new File(file, root), file.getAbsolutePath().length());
                    }
                    else {
                        progressFile(file);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void progressFile(File file){
        try {
            if(file.isDirectory()) {
                int length = file.getAbsolutePath().length();
                checkFolder(file, length);
            }
            else
            {
                ZipFile zip = new ZipFile(file);
                Enumeration<? extends ZipEntry> entries = zip.entries();
                while(entries.hasMoreElements()){
                    ZipEntry zipentry = entries.nextElement();
                    String entryName = zipentry.getName();
                    addFile(entryName);

                }
                zip.close();
            }
        }catch(NoSuchFileException ignored){

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkFolder(File file, int length){
        File[] files = file.listFiles();
        if(files == null){
            return;
        }
        for(File f : files){
            String name = f.getAbsolutePath().substring(length);
            name = name.replace("\\", "/");
            if(!name.startsWith("/"))
                name = "/" + name;
            if(f.isDirectory()){
                addFile(name + "/");
                checkFolder(f, length);
            }
            else
                addFile(name);
        }
    }
    private static void addFile(String name) {
        if(name.startsWith("/")) {
            name = name.substring(1);
        }
        if(!name.startsWith(root) || !name.toLowerCase().endsWith(type) || !isValidResource(name)) {
            return;
        }
        name = name.substring(7);
        int i = name.indexOf('/');
        String domain = name.substring(0, i);

        ResourceLocation loc = new ResourceLocation(domain, name.substring(i + 1));
        if(!list.contains(loc)){
            list.add(loc);
        }
    }
    public static boolean isValidResource(String name) {
        for(char c : name.toCharArray()){
            if(!ResourceLocation.isAllowedInResourceLocation(c)){
                return false;
            }
        }
        return true;
    }
}
