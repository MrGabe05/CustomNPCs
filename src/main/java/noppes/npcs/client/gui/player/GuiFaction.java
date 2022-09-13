package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.matrix.MatrixStack;
import micdoodle8.mods.galacticraft.api.client.tabs.InventoryTabFactions;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

import java.util.ArrayList;

public class GuiFaction extends GuiNPCInterface {

	private final int imageWidth;
	private final int imageHeight;
	private int guiLeft;
	private int guiTop;

	private ArrayList<Faction> playerFactions = new ArrayList<Faction>();
	private PlayerFactionData data;

	private int page = 0;
	private int pages = 1;

	private GuiButtonNextPage buttonNextPage;
	private GuiButtonNextPage buttonPreviousPage;
	private final ResourceLocation indicator;

	public GuiFaction() {
		super();
		imageWidth = 200;
		imageHeight = 195;
		this.drawDefaultBackground = false;
		title = "";
		//Packets.sendServer(new SPacketPlayerFactionsGet());
		indicator = getResource("standardbg.png");
	}

	@Override
	public void init() {
		super.init();
		data = PlayerData.get(player).factionData;
		playerFactions = new ArrayList<Faction>();
		for (int id : data.factionData.keySet()) {
			Faction faction = FactionController.instance.getFaction(id);
			if (faction == null || faction.hideFaction)
				continue;
			playerFactions.add(faction);
		}

		pages = (playerFactions.size() - 1) / 5;
		pages++;

		page = 1;

		guiLeft = (width - imageWidth) / 2;
		guiTop = (height - imageHeight) / 2 + 12;

		TabRegistry.updateTabValues(guiLeft, guiTop + 8, InventoryTabFactions.class);
		TabRegistry.addTabsToList((button) -> {
			addButton(button);
		});

		this.addButton(buttonNextPage = new GuiButtonNextPage(this, 1, guiLeft + imageWidth - 43, guiTop + 180, true, (button) -> {
			page++;
			updateButtons();
		}));
		this.addButton(buttonPreviousPage = new GuiButtonNextPage(this, 2, guiLeft + 20, guiTop + 180, false, (button) -> {
			page--;
			updateButtons();
		}));
		updateButtons();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(indicator);
		blit(matrixStack, guiLeft, guiTop + 8, 0, 0, imageWidth, imageHeight);
		blit(matrixStack, guiLeft + 4, guiTop + 8, 56, 0, 200, imageHeight);

		if (playerFactions.isEmpty()) {
			ITextComponent noFaction = new TranslationTextComponent("faction.nostanding");
			font.draw(matrixStack, noFaction, guiLeft + (this.imageWidth - font.width(noFaction)) / 2, guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
		} else
			renderScreen(matrixStack);

		super.render(matrixStack, mouseX, mouseY, partialTicks);

	}

	private void renderScreen(MatrixStack matrixStack) {
		int size = 5;
		if (playerFactions.size() % 5 != 0 && page == pages)
			size = playerFactions.size() % 5;

		for (int id = 0; id < size; id++) {
			hLine(matrixStack, guiLeft + 2, guiLeft + imageWidth, guiTop + 14 + id * 30, 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);

			Faction faction = playerFactions.get((page - 1) * 5 + id);
			ITextComponent name = new TranslationTextComponent(faction.name);
			int current = data.factionData.get(faction.id);
			String points = " : " + current;

			ITextComponent standing = new TranslationTextComponent("faction.friendly");
			int color = 0x00FF00;
			if (current < faction.neutralPoints) {
				standing = new TranslationTextComponent("faction.unfriendly");
				color = 0xFF0000;
				points += "/" + faction.neutralPoints;
			} else if (current < faction.friendlyPoints) {
				standing = new TranslationTextComponent("faction.neutral");
				color = 0xF2FF00;
				points += "/" + faction.friendlyPoints;
			} else {
				points += "/-";
			}

			font.draw(matrixStack, name, guiLeft + (this.imageWidth - font.width(name)) / 2, guiTop + 19 + id * 30, faction.color);

			font.draw(matrixStack, standing, width / 2 - font.width(standing) - 1, guiTop + 33 + id * 30, color);
			font.draw(matrixStack, points, width / 2, guiTop + 33 + id * 30, CustomNpcResourceListener.DefaultTextColor);
		}
		hLine(matrixStack, guiLeft + 2, guiLeft + imageWidth, guiTop + 14 + size * 30, 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);

		if (pages > 1) {
			String s = page + "/" + pages;
			font.draw(matrixStack, s, guiLeft + (this.imageWidth - font.width(s)) / 2, guiTop + 203, CustomNpcResourceListener.DefaultTextColor);
		}
	}

	@Override
	public void buttonEvent(GuiButtonNop guibutton) {
		if (!(guibutton instanceof GuiButtonNextPage))
			return;
		int id = guibutton.id;
		if (id == 1) {
			page++;
		}
		if (id == 2) {
			page--;
		}
		updateButtons();
	}

	private void updateButtons() {
		buttonNextPage.visible = page < pages;
		buttonPreviousPage.visible = page > 1;
	}

	@Override
	public void save() {
	}
}