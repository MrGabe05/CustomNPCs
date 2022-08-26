package noppes.npcs.client.controllers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;


public class MusicController {
	//public TreeSet<String> sounds = new TreeSet<String>();
	//public TreeSet<String> music = new TreeSet<String>();
	
	public static MusicController Instance;
    public ISound playing;
    public ResourceLocation playingResource;
    public Entity playingEntity;

	//private SoundPoolEntry musicPool;
	
	public MusicController(){
		Instance = this;
	}


	public void stopMusic(){
		SoundHandler handler = Minecraft.getInstance().getSoundManager();
		if(playing != null)
			handler.stop(playing);
		handler.stop(null, SoundCategory.MUSIC);
		handler.stop(null, SoundCategory.AMBIENT);
		handler.stop(null, SoundCategory.RECORDS);
		playingResource = null;
		playingEntity = null;
		playing = null;
	}
	
	public void playStreaming(String music, Entity entity, boolean isLooping){
		if(isPlaying(music)){			
			return;
		}
		stopMusic();
		playingEntity = entity;
		playingResource = new ResourceLocation(music);
		SoundHandler handler = Minecraft.getInstance().getSoundManager();
        playing = new SimpleSound(playingResource, SoundCategory.RECORDS, 4.0F, 1.0F, isLooping, 0, ISound.AttenuationType.LINEAR, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), false);
        handler.play(playing);
	}

	public void playMusic(String music, Entity entity, boolean isLooping) {
		if(isPlaying(music))
			return;
		stopMusic();
		playingResource = new ResourceLocation(music);

		playingEntity = entity;

		SoundHandler handler = Minecraft.getInstance().getSoundManager();
        playing = new SimpleSound(playingResource, SoundCategory.MUSIC, 1.0F, 1.0F, isLooping, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, false);
        handler.play(playing);
	}
	

	public boolean isPlaying(String music) {
		ResourceLocation resource = new ResourceLocation(music);
		if(playingResource == null || !playingResource.equals(resource)){
			return false;
		}
    	return Minecraft.getInstance().getSoundManager().isActive(playing);
	}
	
	public void playSound(SoundCategory cat, String music, BlockPos pos, float volume, float pitch) {
		SimpleSound rec = new SimpleSound(new ResourceLocation(music), cat, volume, pitch, false, 0, ISound.AttenuationType.LINEAR, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, false);
		Minecraft.getInstance().getSoundManager().play(rec);
	}
}
