package noppes.npcs.api.wrapper.gui;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.gui.*;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.controllers.CustomGuiController;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.ArrayList;
import java.util.List;

public class CustomGuiWrapper implements ICustomGui {

    int id;
    int width,height;
    int playerInvX,playerInvY;
    boolean pauseGame,showPlayerInv;
    String backgroundTexture = "";

    ScriptContainer scriptHandler;
    List<ICustomGuiComponent> components = new ArrayList<>();
    List<IItemSlot> slots = new ArrayList<>();

    public CustomGuiWrapper(){}

    public CustomGuiWrapper(int id, int width, int height, boolean pauseGame) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.pauseGame = pauseGame;
        this.scriptHandler = ScriptContainer.Current;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<ICustomGuiComponent> getComponents() {
        return components;
    }

    @Override
    public List<IItemSlot> getSlots() {
        return slots;
    }

    public ScriptContainer getScriptHandler() {
        return this.scriptHandler;
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void setDoesPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }

    public boolean getDoesPauseGame() {
        return pauseGame;
    }

    @Override
    public void setBackgroundTexture(String resourceLocation) {
        this.backgroundTexture = resourceLocation;
    }

    public String getBackgroundTexture() {
        return backgroundTexture;
    }

    @Override
    public IButton addButton(int id, String label, int x, int y) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y);
        this.components.add(component);
        return (IButton)this.components.get(this.components.size()-1);
    }

    @Override
    public IButton addButton(int id, String label, int x, int y, int width, int height) {
        CustomGuiButtonWrapper component  = new CustomGuiButtonWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return (IButton)this.components.get(this.components.size()-1);
    }

    @Override
    public IButton addTexturedButton(int id, String label, int x, int y, int width, int height, String texture) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture);
        this.components.add(component);
        return (IButton)this.components.get(this.components.size()-1);
    }

    @Override
    public IButton addTexturedButton(int id, String label, int x, int y, int width, int height, String texture, int textureX, int textureY) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture, textureX, textureY);
        this.components.add(component);
        return (IButton)this.components.get(this.components.size()-1);
    }

    @Override
    public ILabel addLabel(int id, String label, int x, int y, int width, int height) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return (ILabel)this.components.get(this.components.size()-1);
    }

    @Override
    public ILabel addLabel(int id, String label, int x, int y, int width, int height, int color) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height, color);
        this.components.add(component);
        return (ILabel) this.components.get(this.components.size()-1);
    }

    @Override
    public ITextField addTextField(int id, int x, int y, int width, int height) {
        CustomGuiTextFieldWrapper component = new CustomGuiTextFieldWrapper(id, x, y, width, height);
        this.components.add(component);
        return (ITextField) this.components.get(this.components.size()-1);
    }

    @Override
    public ITextArea addTextArea(int id, int x, int y, int width, int height) {
        CustomGuiTextAreaWrapper component = new CustomGuiTextAreaWrapper(id, x, y, width, height);
        this.components.add(component);
        return (ITextArea) this.components.get(this.components.size()-1);
    }

    @Override
    public ITexturedRect addTexturedRect(int id, String texture, int x, int y, int width, int height) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height);
        this.components.add(component);
        return (ITexturedRect)this.components.get(this.components.size()-1);
    }

    @Override
    public ITexturedRect addTexturedRect(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height, textureX,textureY);
        this.components.add(component);
        return (ITexturedRect) this.components.get(this.components.size()-1);
    }

    @Override
    public IItemSlot addItemSlot(int x, int y) {
        return this.addItemSlot(x, y, ItemScriptedWrapper.AIR);
    }

    @Override
    public IItemSlot addItemSlot(int x, int y, IItemStack stack) {
        CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x,y, stack);
        this.slots.add(slot);
        return this.slots.get(this.slots.size()-1);
    }

    @Override
    public IScroll addScroll(int id, int x, int y, int width, int height, String[] list) {
        CustomGuiScrollWrapper component = new CustomGuiScrollWrapper(id, x, y, width, height, list);
        this.components.add(component);
        return (IScroll) this.components.get(this.components.size()-1);
    }

    @Override
    public void showPlayerInventory(int x, int y) {
        this.showPlayerInv = true;
        this.playerInvX = x;
        this.playerInvY = y;
    }

    @Override
    public ICustomGuiComponent getComponent(int componentID) {
        for(ICustomGuiComponent component : this.components) {
            if(component.getID() == componentID)
                return component;
        }
        return null;
    }

    @Override
    public void removeComponent(int componentID) {
        components.removeIf(c -> c.getID() == componentID);
    }

    @Deprecated
    public void updateComponent(ICustomGuiComponent component) {
        for(int i = 0; i < this.components.size(); i++) {
            ICustomGuiComponent c = this.components.get(i);
            if(c.getID() == component.getID()) {
                this.components.set(i, component);
                return;
            }
        }
    }

    @Override
    public void update(IPlayer player) {
        if(((ServerPlayerEntity)player.getMCEntity()).containerMenu instanceof ContainerCustomGui) {
            Packets.send((ServerPlayerEntity)player.getMCEntity(), new PacketGuiData(toNBT()));
        }
    }

    public boolean getShowPlayerInv() {
        return showPlayerInv;
    }

    public int getPlayerInvX() {
        return playerInvX;
    }

    public int getPlayerInvY() {
        return playerInvY;
    }

    public ICustomGui fromNBT(CompoundNBT tag) {
        this.id = tag.getInt("id");
        this.width = tag.getIntArray("size")[0];
        this.height = tag.getIntArray("size")[1];
        this.pauseGame = tag.getBoolean("pause");
        this.backgroundTexture = tag.getString("bgTexture");


        List<ICustomGuiComponent> components = new ArrayList<>();
        ListNBT list = tag.getList("components", Constants.NBT.TAG_COMPOUND);
        for(INBT b : list) {
            CustomGuiComponentWrapper component = CustomGuiComponentWrapper.createFromNBT((CompoundNBT)b);
            components.add(component);
        }
        this.components = components;

        List<IItemSlot> slots = new ArrayList<>();
        list = tag.getList("slots", Constants.NBT.TAG_COMPOUND);
        for(INBT b : list) {
            CustomGuiItemSlotWrapper component = (CustomGuiItemSlotWrapper)CustomGuiComponentWrapper.createFromNBT((CompoundNBT)b);
            slots.add(component);
        }
        this.slots = slots;

        this.showPlayerInv = tag.getBoolean("showPlayerInv");
        if(showPlayerInv) {
            this.playerInvX = tag.getIntArray("pInvPos")[0];
            this.playerInvY = tag.getIntArray("pInvPos")[1];
        }

        return this;
    }

    public CompoundNBT toNBT() {
        CompoundNBT tag = new CompoundNBT();

        tag.putInt("id", id);
        tag.putIntArray("size", new int[]{this.width,this.height});
        tag.putBoolean("pause", pauseGame);
        tag.putString("bgTexture", backgroundTexture);

        ListNBT list = new ListNBT();
        for(ICustomGuiComponent c : this.components) {
            list.add(((CustomGuiComponentWrapper)c).toNBT(new CompoundNBT()));
        }
        tag.put("components", list);

        list = new ListNBT();
        for(ICustomGuiComponent c : this.slots) {
            list.add(((CustomGuiComponentWrapper)c).toNBT(new CompoundNBT()));
        }
        tag.put("slots", list);

        tag.putBoolean("showPlayerInv", this.showPlayerInv);
        if(this.showPlayerInv) {
            tag.putIntArray("pInvPos", new int[] {this.playerInvX, this.playerInvY});
        }

        return tag;
    }

}
