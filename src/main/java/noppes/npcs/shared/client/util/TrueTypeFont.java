package noppes.npcs.shared.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.client.renderer.CTextureUtil;
import noppes.npcs.shared.common.util.LRUHashMap;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class TrueTypeFont {
	enum GlyphType{ NORMAL, COLOR, RANDOM, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET, OTHER }
	
	private final static int MaxWidth = 512;
    private static final List<Font> allFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
	private final List<Font> usedFonts = new ArrayList<>();
    
	private final LinkedHashMap<String, GlyphCache> textcache = new LRUHashMap<>(100);
	private final Map<Character, Glyph> glyphcache = new HashMap<>();
	private final List<TextureCache> textures = new ArrayList<>();
    
	private final Font font;
	private int lineHeight;
	
	private final Graphics2D globalG = (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
		
	public float scale;
	
	private int specialChar = 167;
	
	public TrueTypeFont(Font font, float scale){
		this.font = font;
		this.scale = scale;
		globalG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		lineHeight = globalG.getFontMetrics(font).getHeight();
	}
	
	public TrueTypeFont(ResourceLocation resource, int fontSize, float scale) throws IOException, FontFormatException{
		try(IResource r = Minecraft.getInstance().getResourceManager().getResource(resource)){
			InputStream stream = r.getInputStream();
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
			ge.registerFont(font);
			this.font = font.deriveFont(Font.PLAIN, fontSize);
			this.scale = scale;
			globalG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			lineHeight = globalG.getFontMetrics(font).getHeight();
		}
	}
	
	public void setSpecial(char c){
		specialChar = c;
	}
	
	public void draw(String text, float x, float y, int color){
		GlyphCache cache = getOrCreateCache(text);
		
        float r = (color >> 16 & 255) / 255F;
        float g = (color >> 8 & 255) / 255F;
        float b = (color & 255) / 255F;
		RenderSystem.color4f(r, g, b, 1);

		RenderSystem.enableBlend();
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 0);
		RenderSystem.scalef(scale, scale, 1);
		float i = 0;
		for(Glyph gl : cache.glyphs){
			if(gl.type != GlyphType.NORMAL){
				if(gl.type == GlyphType.RESET){
					RenderSystem.color4f(r, g, b, 1);
				}
				else if(gl.type == GlyphType.COLOR){
					RenderSystem.color4f((gl.color >> 16 & 255) / 255F, (gl.color >> 8 & 255) / 255F, (gl.color & 255) / 255F, 1);
				}
			}
			else{
				RenderSystem.bindTexture(gl.texture);
		        fillGradient(i, 0, gl.x * textureScale(), gl.y * textureScale(), gl.width * textureScale(), gl.height * textureScale());
		        i += gl.width * textureScale();
			}
		}
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		RenderSystem.color4f(1, 1, 1, 1);
	}
	
	private GlyphCache getOrCreateCache(String text){
		GlyphCache cache = textcache.get(text);
		if(cache != null)
			return cache;
		cache = new GlyphCache();
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			if(c == specialChar && i + 1 < text.length()){
				char next = text.toLowerCase(Locale.ENGLISH).charAt(i + 1);
                int index = "0123456789abcdefklmnor".indexOf(next);
                if(index >= 0){
                	Glyph g = new Glyph();

                    if (index < 16){
                    	g.type = GlyphType.COLOR; 
                    	g.color = TextFormatting.getByCode(next).getColor();
                    }
                    else if(index == 16){
                    	g.type = GlyphType.RANDOM;
                    }
                    else if(index == 17){
                    	g.type = GlyphType.BOLD;
                    }
                    else if(index == 18){
                    	g.type = GlyphType.STRIKETHROUGH;
                    }
                    else if(index == 19){
                    	g.type = GlyphType.UNDERLINE;
                    }
                    else if(index == 20){
                    	g.type = GlyphType.ITALIC;
                    }
                    else{
                    	g.type = GlyphType.RESET;
                    }
        			cache.glyphs.add(g);
                	i++;
                	continue;
                }
			}
			Glyph g = getOrCreateGlyph(c);
			cache.glyphs.add(g);
			cache.width += g.width;
			cache.height = Math.max(cache.height, g.height);
		}
		textcache.put(text, cache);
		return cache;
	}
	
	private Glyph getOrCreateGlyph(char c){
		Glyph g = glyphcache.get(c);
		if(g != null)
			return g;
		
		TextureCache cache = getCurrentTexture();
		Font font = getFontForChar(c);
		FontMetrics metrics = globalG.getFontMetrics(font);
		g = new Glyph();
		g.width = Math.max(metrics.charWidth(c), 1);
		g.height = Math.max(metrics.getHeight(), 1);
		
		if(cache.x + g.width >= MaxWidth){
			cache.x = 0;
			cache.y += lineHeight + 1;
			if(cache.y >= MaxWidth){
				cache.full = true;
				cache = getCurrentTexture();
			}
		}
		
		g.x = cache.x;
		g.y = cache.y;		
		cache.x += g.width + 3;
		lineHeight = Math.max(lineHeight, g.height);
		
		cache.g.setFont(font);
		cache.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		cache.g.drawString(c + "", g.x, g.y + metrics.getAscent());
		g.texture = cache.textureId;

		CTextureUtil.uploadTextureImage(cache.textureId, cache.bufferedImage);
        glyphcache.put(c,  g);
        
		return g;
	}
	
	private TextureCache getCurrentTexture(){
		TextureCache cache = null;
		for(TextureCache t : textures){
			if(!t.full){
				cache = t;
				break;
			}
		}
		if(cache == null){
			textures.add(cache = new TextureCache());
		}
		return cache;
	}
	
	public void drawCentered(String text, float x, float y, int color){
		draw(text, x - (width(text) / 2f), y, color);
	}
	
	private Font getFontForChar(char c){
		if(font.canDisplay(c))
			return font;

		for(Font f : usedFonts){
			if(f.canDisplay(c)){
				return f;
			}
		}
		
		Font fa = new Font("Arial Unicode MS", Font.PLAIN, this.font.getSize());
		if(fa.canDisplay(c))
			return fa;
		
		for(Font f : allFonts){
			if(f.canDisplay(c)){
				usedFonts.add(f = f.deriveFont(Font.PLAIN, this.font.getSize()));
				return f;
			}
		}
		return font;
	}

    public void fillGradient(float x, float y, float textureX, float textureY, float width, float height){
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        int zLevel = 0;
		BufferBuilder tessellator = Tessellator.getInstance().getBuilder();
		tessellator.begin(7, DefaultVertexFormats.POSITION_TEX);
		//tessellator.noColor();
		tessellator.vertex(x, y + height, zLevel).uv(textureX * f, (textureY + height) * f1).endVertex();
		tessellator.vertex(x + width, y + height, zLevel).uv((textureX + width) * f, (textureY + height) * f1).endVertex();
		tessellator.vertex(x + width, y, zLevel).uv((textureX + width) * f, textureY * f1).endVertex();
		tessellator.vertex(x, y, zLevel).uv(textureX * f, textureY * f1).endVertex();
		Tessellator.getInstance().end();
    }
    
    public int width(String text){
		GlyphCache cache = getOrCreateCache(text);
		return (int) (cache.width * scale * textureScale());
    }
    
    public int height(String text){
    	if(text == null || text.trim().isEmpty())
    		return (int) (lineHeight * scale * textureScale());
		GlyphCache cache = getOrCreateCache(text);
		return Math.max(1,(int) (cache.height * scale * textureScale()));
    }
    
    private float textureScale(){
    	return 0.5f;
    }
	
	public void dispose(){
		for(TextureCache cache : textures){
			RenderSystem.deleteTexture(cache.textureId);
		}
		textcache.clear();
	}
	
	static class TextureCache{
		int x, y;	
		int textureId = GL11.glGenTextures();
		BufferedImage bufferedImage = new BufferedImage(MaxWidth, MaxWidth, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		boolean full;
	}
	
	static class Glyph{
		GlyphType type = GlyphType.NORMAL;
		int color = -1;
		int x, y, height, width, texture;
	}
	
	static class GlyphCache{
		public int width, height;
		List<Glyph> glyphs = new ArrayList<>();
	}

	public String getFontName() {
		return font.getFontName();
	}
}
