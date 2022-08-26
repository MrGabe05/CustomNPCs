package noppes.npcs.ai;

import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import noppes.npcs.entity.EntityNPCInterface;

public class FlyingMoveHelper extends MovementController {
    private EntityNPCInterface entity;
    private int courseChangeCooldown;

    public FlyingMoveHelper(EntityNPCInterface entity){
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick(){
        if (this.operation == MovementController.Action.MOVE_TO){
            
            if (this.courseChangeCooldown-- <= 0){
                this.courseChangeCooldown = 4;
                double d0 = this.getWantedX() - this.entity.getX();
                double d1 = this.getWantedY() - this.entity.getY();
                double d2 = this.getWantedZ() - this.entity.getZ();


                Vector3d vector3d = new Vector3d(this.getWantedX() - this.entity.getX(), this.getWantedY() - this.entity.getY(), this.getWantedZ() - this.entity.getZ());
                double length = vector3d.length();
                vector3d = vector3d.normalize();

                if (length > 0.5 && this.isNotColliding(vector3d, MathHelper.ceil(length))){
                    double speed = entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue() / 2.5;
                	if(length < 3 && speed > 0.1f) {
                		speed = 0.1f;
                	}
                	Vector3d m = this.entity.getDeltaMovement().add(vector3d.scale(speed));
                	//m.add(d0 / length * speed, d1 / length * speed, d2 / length * speed);
                	this.entity.setDeltaMovement(m);
                    this.entity.yBodyRot = this.entity.yRot = -((float)Math.atan2(m.x, m.z)) * 180.0F / (float)Math.PI;
                }
                else{
                    this.operation = MovementController.Action.WAIT;
                }
            }
        }
    }

    private boolean isNotColliding(Vector3d vec, int length){
        AxisAlignedBB axisalignedbb = this.entity.getBoundingBox();

        for(int i = 1; i < length; ++i) {
            axisalignedbb = axisalignedbb.move(vec);
            if (!this.entity.level.noCollision(this.entity, axisalignedbb)) {
                return false;
            }
        }

        return true;
    }
}
