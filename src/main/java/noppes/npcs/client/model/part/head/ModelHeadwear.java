package noppes.npcs.client.model.part.head;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.model.Model2DRenderer;
import noppes.npcs.client.model.ModelScaleRenderer;
import noppes.npcs.constants.EnumParts;

import java.util.HashMap;

public class ModelHeadwear extends ModelScaleRenderer{

	private final static HashMap<ResourceLocation, ModelHeadwear> models = new HashMap<>();
	
	public ModelHeadwear(Model base, ResourceLocation location) {
		super(base, EnumParts.HEAD);	
		Model2DRenderer right = new Model2DRenderer(base, 32, 8, 8, 8, location);
		right.setPos(-4.641F, .8f, 4.64f);
		right.setScale(0.58f);
		right.setThickness(0.65f);
        setRotation(right, 0, (float)(Math.PI/2f), 0);
		this.addChild(right);
		
		Model2DRenderer left = new Model2DRenderer(base, 48, 8, 8, 8, location);
		left.setPos(4.639F, .8f, -4.64f);
		left.setScale(0.58f);
		left.setThickness(0.65f);
        setRotation(left, 0, (float)(Math.PI/-2f), 0);
		this.addChild(left);
		
		Model2DRenderer front = new Model2DRenderer(base, 40, 8, 8, 8, location);
		front.setPos(-4.64F, .801f, -4.641f);
		front.setScale(0.58f);
		front.setThickness(0.65f);
        setRotation(front, 0, 0, 0);
		this.addChild(front);
		
		Model2DRenderer back = new Model2DRenderer(base, 56, 8, 8, 8, location);
		back.setPos(4.64F, .801f, 4.639f);
		back.setScale(0.58f);
		back.setThickness(0.65f);
        setRotation(back, 0, (float)(Math.PI), 0);
		this.addChild(back);
		
		Model2DRenderer top = new Model2DRenderer(base, 40, 0, 8, 8, location);
		top.setPos(-4.64F, -8.5f, -4.64f);
		top.setScale(0.5799f);
		top.setThickness(0.65f);
        setRotation(top, (float)(Math.PI / -2), 0, 0);
		this.addChild(top);
		
		Model2DRenderer bottom = new Model2DRenderer(base, 48, 0, 8, 8, location);
		bottom.setPos(-4.64F, 0f, -4.64f);
		bottom.setScale(0.5799f);
		bottom.setThickness(0.65f);
        setRotation(bottom, (float)(Math.PI / -2), 0, 0);
		this.addChild(bottom);
	}

	public static void clear() {
		models.clear();
	}

	public void setRotation(ModelRenderer model, float x, float y, float z) {
		model.xRot = x;
		model.yRot = y;
		model.zRot = z;
	}

	public synchronized static ModelHeadwear getModel(ResourceLocation location, Model base){
		ModelHeadwear headwear = models.get(location);
		if(headwear == null){
			headwear = new ModelHeadwear(base, location);
			models.put(location, headwear);
		}
		return headwear;
	}
	
}
