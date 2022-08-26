package noppes.npcs.entity.data;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.BossInfo;
import net.minecraftforge.registries.ForgeRegistries;
import nikedemos.markovnames.generators.MarkovGenerator;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.INPCDisplay;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nullable;
import java.util.Random;

public class DataDisplay implements INPCDisplay{
	EntityNPCInterface npc;

	private String name = "Noppes";
	private String title = "";

	private int markovGeneratorId=8; //roman,japanese,slavic,welsh,sami,oldnorse,ancientgreek,aztec,classic,spanish (0 - 9 inclusively)
	private int markovGender = 0; //0:random, 1:male, 2:female
	
	public byte skinType = 0; //0:normal, 1:player, 2:url
	private String url = "";
	public GameProfile playerProfile;
	private String texture = "customnpcs:textures/entity/humanmale/steve.png";
	
	private String cloakTexture = "";
	private String glowTexture = "";

	private int visible = 0; //0:visible, 1:invisible, 2:semi-invisible
	public Availability availability=new Availability();
	
	private int modelSize = 5;

	private int showName = 0;
	private int skinColor = 0xFFFFFF;
	
	private boolean disableLivingAnimation = false;	
	private byte hitboxState = 0; //0:Normal, 1:None, 2:Solid
	
	private byte showBossBar = 0;
	private BossInfo.Color bossColor = BossInfo.Color.PINK;
	
	public DataDisplay(EntityNPCInterface npc){
		this.npc = npc;
		if(!npc.isClientSide()){
			markovGeneratorId = new Random().nextInt(10);
			name = getRandomName();
		}
		if(npc.getRandom().nextInt(10) == 0){
			DataPeople.Person p = DataPeople.get();
			name = p.name;
			title = p.title;
			if(!p.skin.isEmpty()){
				texture = p.skin;
			}
		}
	}
	
	public String getRandomName() {
		return MarkovGenerator.fetch(markovGeneratorId, markovGender);
	}
	
	public CompoundNBT save(CompoundNBT nbttagcompound) {
		nbttagcompound.putString("Name", name);
		nbttagcompound.putInt("MarkovGeneratorId", markovGeneratorId);
		nbttagcompound.putInt("MarkovGender", markovGender);
		nbttagcompound.putString("Title", title);
		nbttagcompound.putString("SkinUrl", url);
		nbttagcompound.putString("Texture", texture);
		nbttagcompound.putString("CloakTexture", cloakTexture);
		nbttagcompound.putString("GlowTexture", glowTexture);
		nbttagcompound.putByte("UsingSkinUrl", skinType);
		
        if (this.playerProfile != null)
        {
            CompoundNBT nbttagcompound1 = new CompoundNBT();
            NBTUtil.writeGameProfile(nbttagcompound1, this.playerProfile);
            nbttagcompound.put("SkinUsername", nbttagcompound1);
        }
		
		nbttagcompound.putInt("Size", modelSize);

		nbttagcompound.putInt("ShowName", showName);
		nbttagcompound.putInt("SkinColor", skinColor);
		nbttagcompound.putInt("NpcVisible", visible);
		nbttagcompound.put("VisibleAvailability", this.availability.save(new CompoundNBT()));

		nbttagcompound.putBoolean("NoLivingAnimation", disableLivingAnimation);
		nbttagcompound.putByte("IsStatue", hitboxState);
		nbttagcompound.putByte("BossBar", showBossBar);
		nbttagcompound.putInt("BossColor", bossColor.ordinal());

		return nbttagcompound;
	}
	public void readToNBT(CompoundNBT nbttagcompound) {
		setName(nbttagcompound.getString("Name"));
		setMarkovGeneratorId(nbttagcompound.getInt("MarkovGeneratorId"));
		setMarkovGender(nbttagcompound.getInt("MarkovGender"));
		title = nbttagcompound.getString("Title");
		

        int prevSkinType = skinType;
        String prevTexture = texture;
        String prevUrl = url;
        String prevPlayer = getSkinPlayer();

		url = nbttagcompound.getString("SkinUrl");
		skinType = nbttagcompound.getByte("UsingSkinUrl");
		texture = nbttagcompound.getString("Texture");
		cloakTexture = nbttagcompound.getString("CloakTexture");
		glowTexture = nbttagcompound.getString("GlowTexture");
		
    	playerProfile = null;
		if(skinType == 1){
	        if (nbttagcompound.contains("SkinUsername", 10)){
	            this.playerProfile = NBTUtil.readGameProfile(nbttagcompound.getCompound("SkinUsername"));
	        }
	        else if (nbttagcompound.contains("SkinUsername", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkinUsername"))){
	            this.playerProfile = new GameProfile(null, nbttagcompound.getString("SkinUsername"));
	        }
	        this.loadProfile();
		}
		
		modelSize = ValueUtil.CorrectInt(nbttagcompound.getInt("Size"), 1, 30);

		showName = nbttagcompound.getInt("ShowName");
		
		if(nbttagcompound.contains("SkinColor"))
			skinColor = nbttagcompound.getInt("SkinColor");
		
		visible = nbttagcompound.getInt("NpcVisible");
		availability.load(nbttagcompound.getCompound("VisibleAvailability"));
		VisibilityController.instance.trackNpc(npc);

		disableLivingAnimation = nbttagcompound.getBoolean("NoLivingAnimation");
		hitboxState = nbttagcompound.getByte("IsStatue");
		setBossbar(nbttagcompound.getByte("BossBar"));
		setBossColor(nbttagcompound.getInt("BossColor"));
		
		if(prevSkinType != skinType || !texture.equals(prevTexture) || !url.equals(prevUrl) || !getSkinPlayer().equals(prevPlayer))
			npc.textureLocation = null;
		npc.textureGlowLocation = null;
		npc.textureCloakLocation = null;
		npc.refreshDimensions();
	}

	public void loadProfile(){
        if (this.playerProfile != null && !StringUtils.isNullOrEmpty(this.playerProfile.getName())){
			this.playerProfile = getGameprofile(npc.getServer(), this.playerProfile);
        }
    }

	private static GameProfile getGameprofile(MinecraftServer server, @Nullable GameProfile profile) {
		if(server == null){
			return SkullTileEntity.updateGameprofile(profile);
		}
		try{
			if(profile == null || StringUtils.isNullOrEmpty(profile.getName()) || profile.isComplete() && profile.getProperties().containsKey("textures")){
				return profile;
			}
			GameProfile gameprofile = server.getProfileCache().get(profile.getName());
			if (gameprofile == null) {
				return profile;
			} else {
				Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
				if (property == null) {
					gameprofile = server.getSessionService().fillProfileProperties(gameprofile, true);
				}

				return gameprofile;
			}
		}
		catch(Exception e){
			return profile;
		}
	}
	
	public boolean showName() {
		if(npc.isKilled())
			return false;
		return showName == 0 || (showName == 2 && npc.isAttacking());
	}

	@Override
	public String getName(){
		return name;
	}
	
	@Override
	public void setName(String name){
		if(this.name.equals(name))
			return;
		this.name = name;
		npc.bossInfo.setName(npc.getDisplayName());
		npc.updateClient = true;
	}
	
	@Override
	public int getShowName(){
		return showName;
	}
	
	@Override
	public void setShowName(int type){
		if(type == showName)
			return;
		this.showName = ValueUtil.CorrectInt(type, 0, 2);
		npc.updateClient = true;
	}
	
	public int getMarkovGender(){
		return markovGender;
	}
	
	public void setMarkovGender(int gender) {
		if(markovGender == gender)
			return;
		this.markovGender = ValueUtil.CorrectInt(gender, 0, 2);
	}
	
	public int getMarkovGeneratorId(){
		return markovGeneratorId;
	}
	
	public void setMarkovGeneratorId(int id) {
		if(markovGeneratorId == id)
			return;
		this.markovGeneratorId = ValueUtil.CorrectInt(id, 0, 9);
	}
	
	
	@Override
	public String getTitle(){
		return title;
	}
	
	@Override
	public void setTitle(String title){
		if(this.title.equals(title))
			return;
		this.title = title;
		npc.updateClient = true;
	}

	@Override
	public String getSkinUrl(){
		return url;
	}

	@Override
	public void setSkinUrl(String url){
		if(this.url.equals(url))
			return;
		this.url = url;
		if(url.isEmpty())
			skinType = 0;
		else
			skinType = 2;
		npc.updateClient = true;
	}


	@Override
	public String getSkinPlayer(){
		return playerProfile == null?"":playerProfile.getName();
	}

	@Override
	public void setSkinPlayer(String name){
		if(name == null || name.isEmpty()){
			playerProfile = null;
			skinType = 0;
		}
		else{
			playerProfile = new GameProfile(null, name);
			skinType = 1;
		}
		npc.updateClient = true;
	}


	@Override
	public String getSkinTexture(){
		return NoppesStringUtils.cleanResource(texture);
	}

	@Override
	public void setSkinTexture(String texture){
		if(texture == null || this.texture.equals(texture))
			return;
		this.texture = NoppesStringUtils.cleanResource(texture);
		npc.textureLocation = null;
		skinType = 0;
		npc.updateClient = true;
	}

	@Override
	public String getOverlayTexture(){
		return NoppesStringUtils.cleanResource(glowTexture);
	}

	@Override
	public void setOverlayTexture(String texture){
		if(this.glowTexture.equals(texture))
			return;
		this.glowTexture = NoppesStringUtils.cleanResource(texture);
		npc.textureGlowLocation = null;
		npc.updateClient = true;
	}

	@Override
	public String getCapeTexture(){
		return NoppesStringUtils.cleanResource(cloakTexture);
	}

	@Override
	public void setCapeTexture(String texture){
		if(this.cloakTexture.equals(texture))
			return;
		this.cloakTexture = NoppesStringUtils.cleanResource(texture);
		npc.textureCloakLocation = null;
		npc.updateClient = true;
	}

	@Override
	public boolean getHasLivingAnimation(){
		return !disableLivingAnimation;
	}

	@Override
	public void setHasLivingAnimation(boolean enabled){
		disableLivingAnimation = !enabled;
		npc.updateClient = true;
	}

	@Override
	public int getBossbar(){
		return showBossBar;
	}

	@Override
	public void setBossbar(int type){
		if(type == showBossBar)
			return;
		showBossBar = (byte) ValueUtil.CorrectInt(type, 0, 2);
		npc.bossInfo.setVisible(showBossBar == 1);
		npc.updateClient = true;
	}

	@Override
	public int getBossColor(){
		return bossColor.ordinal();
	}

	@Override
	public void setBossColor(int color) {
		if(color < 0 || color >= BossInfo.Color.values().length)
			throw new CustomNPCsException("Invalid Boss Color: " + color);
		bossColor = BossInfo.Color.values()[color];
		npc.bossInfo.setColor(bossColor);
	}

	@Override
	public int getVisible(){
		return visible;
	}
	
	@Override
	public void setVisible(int type){
		if(type == visible)
			return;
		visible = ValueUtil.CorrectInt(type, 0, 2);
		npc.updateClient = true;
	}

	@Override
	public int getSize(){
		return modelSize;
	}

	@Override
	public void setSize(int size){
		if(modelSize == size)
			return;
		modelSize = ValueUtil.CorrectInt(size, 1, 30);
		npc.updateClient = true;
	}

	@Override
	public void setModelScale(int part, float x, float y, float z){
		ModelData modeldata = ((EntityCustomNpc)npc).modelData;
		ModelPartConfig model = null;
		if(part == 0)
			model = modeldata.getPartConfig(EnumParts.HEAD);
		else if(part == 1)
			model = modeldata.getPartConfig(EnumParts.BODY);
		else if(part == 2)
			model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
		else if(part == 3)
			model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
		else if(part == 4)
			model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
		else if(part == 5)
			model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
		
		if(model == null)
			throw new CustomNPCsException("Unknown part: " + part);
		
		model.setScale(x, y, z);
		npc.updateClient = true;
	}
	
	@Override
	public float[] getModelScale(int part){
		ModelData modeldata = ((EntityCustomNpc)npc).modelData;
		ModelPartConfig model = null;
		if(part == 0)
			model = modeldata.getPartConfig(EnumParts.HEAD);
		else if(part == 1)
			model = modeldata.getPartConfig(EnumParts.BODY);
		else if(part == 2)
			model = modeldata.getPartConfig(EnumParts.ARM_LEFT);
		else if(part == 3)
			model = modeldata.getPartConfig(EnumParts.ARM_RIGHT);
		else if(part == 4)
			model = modeldata.getPartConfig(EnumParts.LEG_LEFT);
		else if(part == 5)
			model = modeldata.getPartConfig(EnumParts.LEG_RIGHT);
		
		if(model == null)
			throw new CustomNPCsException("Unknown part: " + part);
		
		return new float[]{model.scaleX, model.scaleY, model.scaleZ};
	}
	
	@Override
	public int getTint(){
		return skinColor;
	}

	@Override
	public void setTint(int color){
		if(color == skinColor)
			return;
		this.skinColor = color;
		npc.updateClient = true;
	}

	@Override
	public void setModel(String id) {
		ModelData modeldata = ((EntityCustomNpc)npc).modelData;
		if(id == null) {
			if(modeldata.getEntityName() == null)
				return;
			modeldata.setEntity((String)null);
			npc.updateClient = true;
		}
		else {
			ResourceLocation resource = new ResourceLocation(id);
			EntityType type = ForgeRegistries.ENTITIES.getValue(resource);
			if(type == null)
				throw new CustomNPCsException("Unknown entity id: " + id);
			
			modeldata.setEntity(id);
			npc.updateClient = true;
		}
	}

	@Override
	public String getModel() {
		ModelData modeldata = ((EntityCustomNpc)npc).modelData;
		if(modeldata.getEntityName() == null)
			return null;
		return modeldata.getEntityName().toString();
	}

	@Override
	public byte getHitboxState() {
		return hitboxState;
	}

	@Override
	public void setHitboxState(byte state) {
		if(hitboxState == state)
			return;
		this.hitboxState = state;
		npc.updateClient = true;
	}

	@Override
	public boolean isVisibleTo(IPlayer player) {
		return isVisibleTo(player);
	}

	public boolean isVisibleTo(ServerPlayerEntity player) {
		if(visible == 1) {
			return !availability.isAvailable(player);
		}
		return availability.isAvailable(player);
	}
}
