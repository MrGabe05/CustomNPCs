package noppes.npcs.client.fx;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;


public class EntityRainbowFX extends Particle
{
    private float quadSize;
    public static float[][] colorTable = {
            {
                    1.0F, 0, 0
            },
            {
                    1.0F, 0.5f, 0
            },
            {
                    1.0F, 1.0f, 0
            },
            {
                    0F, 1.0f, 0
            },
            {
                    0F, 0, 1.0f
            },
            {
                    0,4375F, 0, 1.0f
            },
            {
                    0.5625F, 0, 1.0f
            }
    };
    public EntityRainbowFX(ClientWorld world, double d, double d1, double d2, double f, double f1, double f2){
        this(world, d, d1, d2, 1.0F, f, f1, f2);
        quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
    }

    public EntityRainbowFX(ClientWorld world, double d, double d1, double d2, float f, double f1, double f2, double f3){
        super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);
        xd *= 0.10000000149011612D;
        yd *= 0.10000000149011612D;
        zd *= 0.10000000149011612D;
        if(f1 == 0.0F){
            f1 = 1.0F;
        }
        int i = world.random.nextInt(colorTable.length);
        rCol = colorTable[i][0];
        gCol = colorTable[i][1];
        bCol = colorTable[i][2];
        quadSize *= 0.75F;
        quadSize *= f;
        reddustParticleScale = quadSize;
        lifetime = (int)(16D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
        lifetime *= f;
        //noClip = false;
    }

    @Override
    public void render(IVertexBuilder renderer, ActiveRenderInfo info, float partialTicks){
        float f6 = (((float)age + partialTicks) / (float)lifetime) * 32F;
        if(f6 < 0.0F){
            f6 = 0.0F;
        }
        else if(f6 > 1.0F){
            f6 = 1.0F;
        }
        quadSize = reddustParticleScale * f6;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick(){
        xo = x;
        yo = y;
        zo = z;
        if(age++ >= lifetime)
        {
            remove();
        }
        //TODO fix setParticleTextureIndex(7 - (age * 8) / lifetime);with selectSpriteWithAge
        this.
                move(xd, yd, zd);
        if(y == yo)
        {
            xd *= 1.1000000000000001D;
            zd *= 1.1000000000000001D;
        }
        xd *= 0.95999997854232788D;
        yd *= 0.95999997854232788D;
        zd *= 0.95999997854232788D;
        if(onGround)
        {
            xd *= 0.69999998807907104D;
            zd *= 0.69999998807907104D;
        }
    }

    float reddustParticleScale;
}
