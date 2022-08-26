package noppes.npcs.client.gui.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GuiModelColor extends GuiBasic implements ITextfieldListener{

	private Screen parent;
	private final static ResourceLocation colorPicker = new ResourceLocation("moreplayermodels:textures/gui/color.png");
	private final static ResourceLocation colorgui = new ResourceLocation("moreplayermodels:textures/gui/color_gui.png");
	
	private int colorX, colorY;
	
	private GuiTextFieldNop textfield;
	
	public int color;

	private ColorCallback callback;
	public GuiModelColor(Screen parent, int color, ColorCallback callback){
		this.parent = parent;
		this.callback = callback;
		imageHeight = 230;
		closeOnEsc = false;
		background = colorgui;
		this.color = color;
	}

    @Override
    public void init() {
    	super.init();
    	colorX = guiLeft + 4;
    	colorY = guiTop + 50;
		this.addTextField(textfield = new GuiTextFieldNop(0, this, guiLeft + 35, guiTop + 25, 60, 20, getColor()));
		addButton(new GuiButtonNop(this, 66, guiLeft + 107, guiTop + 8, 20, 20, "X"));
		textfield.setTextColor(color);
    }

    @Override
	public void buttonEvent(GuiButtonNop guibutton) {
    	if(guibutton.id == 66){
    		close();
    	}
    }
    
    @Override
    public boolean charTyped(char c, int i){
    	String prev = textfield.getValue();
    	super.charTyped(c, i);
    	String newText = textfield.getValue();
    	if(newText.equals(prev))
    		return false;
		try{
			color = Integer.parseInt(textfield.getValue(),16);
			callback.color(color);
			textfield.setTextColor(color);
		}
		catch(NumberFormatException e){
			textfield.setValue(prev);
		}
		return true;
    }

    @Override
    public void render(MatrixStack matrixStack, int par1, int limbSwingAmount, float par3){
    	super.render(matrixStack, par1, limbSwingAmount, par3);

    	RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bind(colorPicker);
        this.blit(matrixStack, colorX, colorY, 0, 0, 120, 120);
    }
    
	@Override
    public boolean mouseClicked(double i, double j, int k){
		super.mouseClicked(i, j, k);
		if( i < colorX  || i > colorX + 120 || j < colorY || j > colorY + 120)
			return false;
		InputStream stream = null;
		try (IResource resource = this.minecraft.getResourceManager().getResource(colorPicker)){
            BufferedImage bufferedimage = ImageIO.read(stream = resource.getInputStream());
            int color = bufferedimage.getRGB((int)(i - guiLeft - 4) * 4, (int)(j - guiTop - 50) * 4)  & 16777215;
            if(color != 0){
            	this.color = color;
            	callback.color(color);
            	textfield.setTextColor(color);
            	textfield.setValue(getColor());
            }
			
		} catch (IOException e) {
		} 
		finally{
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					
				}
			}
		}
		return true;
    }

	@Override
	public void unFocused(GuiTextFieldNop textfield) {
		try{
			color = Integer.parseInt(textfield.getValue(),16);
		}
		catch(NumberFormatException e){
			color = 0;
		}
		callback.color(color);
		textfield.setTextColor(color);
	}
	
	public String getColor() {
		String str = Integer.toHexString(color);

    	while(str.length() < 6)
    		str = "0" + str;
    	
    	return str;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}
	public static interface ColorCallback{
	    public void color(int color);
	}
}
