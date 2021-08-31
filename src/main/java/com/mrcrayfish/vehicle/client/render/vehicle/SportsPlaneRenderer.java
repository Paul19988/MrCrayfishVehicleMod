package com.mrcrayfish.vehicle.client.render.vehicle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.AbstractPlaneRenderer;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import com.mrcrayfish.vehicle.entity.vehicle.SportsPlaneEntity;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class SportsPlaneRenderer extends AbstractPlaneRenderer<SportsPlaneEntity>
{
    public SportsPlaneRenderer(VehicleProperties defaultProperties)
    {
        super(defaultProperties);
    }

    @Override
    protected void render(@Nullable SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float partialTicks, int light)
    {
        this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE.getModel(), matrixStack, renderTypeBuffer, light);

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.translate(8 * 0.0625, 0, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(-5F));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -3 * 0.0625, 8 * 0.0625);
            matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(180F));
            matrixStack.translate(8 * 0.0625, 0.0625, 0);
            matrixStack.translate(6 * 0.0625, 0, 0);
            matrixStack.mulPose(Axis.POSITIVE_X.rotationDegrees(5F));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WING.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -0.5, 0);
            matrixStack.scale(0.85F, 0.85F, 0.85F);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, 0F, -3 * 0.0625F, 24 * 0.0625F, 0F, partialTicks, light, true);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, 7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, 100F, partialTicks, light, false);
            this.renderPlaneLeg(vehicle, matrixStack, renderTypeBuffer, -7.5F * 0.0625F, -3 * 0.0625F, 2 * 0.0625F, -100F, partialTicks, light, false);
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        {
            matrixStack.translate(0, -1.5 * 0.0625, 22.2 * 0.0625);
            if(vehicle != null)
            {
                float propellerRotation = vehicle.getBladeRotation(partialTicks); //TODO convert to property
                matrixStack.mulPose(Axis.POSITIVE_Z.rotationDegrees(propellerRotation));
            }
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_PROPELLER.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }

    private void renderPlaneLeg(@Nullable SportsPlaneEntity vehicle, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float offsetX, float offsetY, float offsetZ, float legRotation, float partialTicks, int light, boolean rotate)
    {
        matrixStack.pushPose();
        {
            matrixStack.translate(offsetX, offsetY, offsetZ);

            matrixStack.pushPose();
            if(rotate)
            {
                float wheelAngle = this.wheelAngleProperty.get(vehicle, partialTicks);
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(wheelAngle));
            }
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_WHEEL_COVER.getModel(), matrixStack, renderTypeBuffer, light);
            matrixStack.popPose();

            matrixStack.mulPose(Axis.POSITIVE_Y.rotationDegrees(legRotation));
            this.renderDamagedPart(vehicle, SpecialModels.SPORTS_PLANE_LEG.getModel(), matrixStack, renderTypeBuffer, light);
        }
        matrixStack.popPose();
    }

    @Override
    public void applyPlayerModel(SportsPlaneEntity entity, PlayerEntity player, PlayerModel model, float partialTicks)
    {
        model.rightLeg.xRot = (float) Math.toRadians(-85F);
        model.rightLeg.yRot = (float) Math.toRadians(10F);
        model.leftLeg.xRot = (float) Math.toRadians(-85F);
        model.leftLeg.yRot = (float) Math.toRadians(-10F);
    }

    @Override
    public void applyPlayerRender(SportsPlaneEntity entity, PlayerEntity player, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        int index = entity.getSeatTracker().getSeatIndex(player.getUUID());
        if(index != -1)
        {
            VehicleProperties properties = entity.getProperties();
            Seat seat = properties.getSeats().get(index);
            Vector3d seatVec = seat.getPosition().add(0, properties.getAxleOffset() + properties.getWheelOffset(), 0).scale(properties.getBodyTransform().getScale()).multiply(-1, 1, 1).scale(0.0625);
            double playerScale = 32.0 / 30.0;
            double offsetX = -seatVec.x * playerScale;
            double offsetY = (seatVec.y + player.getMyRidingOffset()) * playerScale + (24 * 0.0625);
            double offsetZ = seatVec.z * playerScale;
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-seat.getYawOffset()));
            matrixStack.translate(offsetX, offsetY, offsetZ);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(entity.getBodyRotationPitch(partialTicks)));
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-entity.getBodyRotationRoll(partialTicks)));
            matrixStack.translate(-offsetX, -offsetY, -offsetZ);
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(seat.getYawOffset()));
        }
    }

    @Nullable
    @Override
    public EntityRayTracer.IRayTraceTransforms getRayTraceTransforms()
    {
        return (tracer, transforms, parts) ->
        {
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE, parts, transforms);
            EntityRayTracer.createFuelPartTransforms(ModEntities.SPORTS_PLANE.get(), SpecialModels.FUEL_DOOR_CLOSED, parts, transforms);
            EntityRayTracer.createKeyPortTransforms(ModEntities.SPORTS_PLANE.get(), parts, transforms);
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0, -0.1875F, 0.5F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Z, 180F),
                    EntityRayTracer.MatrixTransformation.createTranslation(0.875F, 0.0625F, 0.0F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, 5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WING, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.875F, -0.1875F, 0.5F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_X, -5F));
            transforms.add(EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
            transforms.add(EntityRayTracer.MatrixTransformation.createScale(0.85F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, -100F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_WHEEL_COVER, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F));
            EntityRayTracer.createTransformListForPart(SpecialModels.SPORTS_PLANE_LEG, parts, transforms,
                    EntityRayTracer.MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F),
                    EntityRayTracer.MatrixTransformation.createRotation(Axis.POSITIVE_Y, 100F));
        };
    }
}
