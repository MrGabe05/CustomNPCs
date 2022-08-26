package noppes.npcs.client.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiColorSelector extends GuiBasic implements ITextfieldListener{

	private final static ResourceLocation resource = new ResourceLocation("customnpcs:textures/gui/color.png");
	
	private int colorX, colorY;
	
	private GuiTextFieldNop textfield;
	
	public int color;

	public SubGuiColorSelector(int color){
		imageWidth = 176;
		imageHeight = 222;
		this.color = color;
		setBackground("smallbg.png");
	}

    @Override
    public void init() {
    	super.init();
    	colorX = guiLeft + 30;
    	colorY = guiTop + 50;
    	
		this.addTextField(textfield = new GuiTextFieldNop(0, this, guiLeft + 53, guiTop + 20, 70, 20, getColor()));
		textfield.setTextColor(color);

    	addButton(new GuiButtonNop(this, 66, guiLeft + 112, guiTop + 198, 60, 20, "gui.done"));
    }
    
    public String getColor(){
		String str = Integer.toHexString(color);
    	while(str.length() < 6)
    		str = "0" + str;
    	return str;
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
			textfield.setTextColor(color);
		}
		catch(NumberFormatException e){
			textfield.setValue(prev);
		}
		return true;
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {
		if(btn.id == 66){
        	close();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int par1, int limbSwingAmount, float par3){
    	super.render(matrixStack, par1, limbSwingAmount, par3);

        minecraft.getTextureManager().bind(resource);
        
        RenderSystem.color4f(1, 1, 1, 1);
        this.blit(matrixStack, colorX, colorY, 0, 0, 120, 120);
    }
    
	@Override
    public boolean mouseClicked(double i, double j, int k){
		super.mouseClicked(i, j, k);
		if( i < colorX  || i > colorX + 117 || j < colorY || j > colorY + 117)
			return false;
		InputStream stream = null;
		try (IResource iresource = this.minecraft.getResourceManager().getResource(resource)){
            BufferedImage bufferedimage = ImageIO.read(stream = iresource.getInputStream());
            color = bufferedimage.getRGB((int)(i - guiLeft - 30) * 4, (int)(j - guiTop - 50) * 4)  & 16777215;
        	textfield.setTextColor(color);
        	textfield.setValue(getColor());
			
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
		int color = 0;
		try{
			color = Integer.parseInt(textfield.getValue(),16);
		}
		catch(NumberFormatException e){
			color = 0;
		}
		this.color = color;
		textfield.setTextColor(color);
	}
}
