package noppes.npcs.client.fx;


import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import noppes.npcs.ModelPartData;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityEnderFX extends SpriteTexturedParticle {
    public static IAnimatedSprite portalSprite;
    private final float portalParticleScale;
    private final int particleNumber;
    private final EntityNPCInterface npc;
    private final ResourceLocation location;
    private boolean move = true;
    private float startX = 0, startY = 0, startZ = 0;

    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    public EntityEnderFX(EntityNPCInterface npc, double partialTicks, double rotationY,
                         double rotationXY, double par8, double par10, double par12, ModelPartData data) {
        super((ClientWorld) npc.level, partialTicks, rotationY, rotationXY, par8, par10, par12);
        this.pickSprite(portalSprite);
        this.npc = npc;
        particleNumber = npc.getRandom().nextInt(2);
        quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
        portalParticleScale = quadSize = random.nextFloat() * 0.2F + 0.5F;

        rCol = (data.color >> 16 & 255) / 255f;
        gCol = (data.color >> 8  & 255) / 255f;
        bCol = (data.color & 255) / 255f;

        this.portalPosX = this.npc.getX();
        this.portalPosY = this.npc.getY();
        this.portalPosZ = this.npc.getZ();

        setPos(npc.getX(), npc.getY(), npc.getZ());
        if(npc.getRandom().nextInt(3) == 1){
            move = false;
            this.x = (float) npc.getX();
            this.y = (float) npc.getY();
            this.z = (float) npc.getZ();
        }

        if(data.playerTexture)
            location = npc.textureLocation;
        else
            location = data.getResource();

        this.lifetime = (int)(Math.random() * 10.0D) + 40;
    }

    @Override
    public float getQuadSize(float p_217561_1_) {
        float lvt_2_1_ = ((float)this.age + p_217561_1_) / (float)this.lifetime;
        lvt_2_1_ = 1.0F - lvt_2_1_;
        lvt_2_1_ *= lvt_2_1_;
        lvt_2_1_ = 1.0F - lvt_2_1_;
        return this.quadSize * lvt_2_1_;
    }

    @Override
    public int getLightColor(float p_189214_1_) {
        int lvt_2_1_ = super.getLightColor(p_189214_1_);
        float lvt_3_1_ = (float)this.age / (float)this.lifetime;
        lvt_3_1_ *= lvt_3_1_;
        lvt_3_1_ *= lvt_3_1_;
        int lvt_4_1_ = lvt_2_1_ & 255;
        int lvt_5_1_ = lvt_2_1_ >> 16 & 255;
        lvt_5_1_ += (int)(lvt_3_1_ * 15.0F * 16.0F);
        if (lvt_5_1_ > 240) {
            lvt_5_1_ = 240;
        }

        return lvt_4_1_ | lvt_5_1_ << 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float lvt_1_1_ = (float)this.age / (float)this.lifetime;
            float lvt_2_1_ = lvt_1_1_;
            lvt_1_1_ = -lvt_1_1_ + lvt_1_1_ * lvt_1_1_ * 2.0F;
            lvt_1_1_ = 1.0F - lvt_1_1_;
            this.x = this.portalPosX + this.xd * (double)lvt_1_1_;
            this.y = this.portalPosY + this.yd * (double)lvt_1_1_ + (double)(1.0F - lvt_2_1_);
            this.z = this.portalPosZ + this.zd * (double)lvt_1_1_;
        }
    }

    @Override
    public void render(IVertexBuilder renderer, ActiveRenderInfo info, float partialTicks){
        BufferBuilder buffer = (BufferBuilder) renderer;
        if(move){
            startX = (float)(npc.xo + (npc.getX() - npc.xo) * (double)partialTicks);
            startY = (float)(npc.yo + (npc.getY() - npc.yo) * (double)partialTicks);
            startZ = (float)(npc.zo + (npc.getZ() - npc.zo) * (double)partialTicks);
        }
        Tessellator tessellator = Tessellator.getInstance();
        tessellator.end();
//        float scale = ((float)age + partialTicks) / (float)lifetime;
//        scale = 1.0F - scale;
//        scale *= scale;
//        scale = 1.0F - scale;
//        quadSize = portalParticleScale * scale;

        Minecraft.getInstance().textureManager.bind(location);
        buffer.begin(7, DefaultVertexFormats.PARTICLE);
        Vector3d vector3d = info.getPosition();
        float f = (float)(MathHelper.lerp(partialTicks, this.xo, this.x) - vector3d.x());
        float f1 = (float)(MathHelper.lerp(partialTicks, this.yo, this.y) - vector3d.y());
        float f2 = (float)(MathHelper.lerp(partialTicks, this.zo, this.z) - vector3d.z());
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = info.rotation();
        } else {
            quaternion = new Quaternion(info.rotation());
            float f3 = MathHelper.lerp(partialTicks, this.oRoll, this.roll);
            quaternion.mul(Vector3f.ZP.rotation(f3));
        }


        Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
        vector3f1.transform(quaternion);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f4 = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.transform(quaternion);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = 0.875f;
        float f8 = f7 + 0.125f;
        float f5 = 0.75f - (particleNumber * 0.25f);
        float f6 = f5 + 0.25f;
        int j = this.getLightColor(partialTicks);
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();

        tessellator.end();
        Minecraft.getInstance().textureManager.bind(AtlasTexture.LOCATION_PARTICLES);
        buffer.begin(7, DefaultVertexFormats.PARTICLE);
    }

    @Override
    public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
        this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
        this.setLocationFromBoundingbox();
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
