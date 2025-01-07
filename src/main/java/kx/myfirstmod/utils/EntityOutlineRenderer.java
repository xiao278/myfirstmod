package kx.myfirstmod.utils;

import kx.myfirstmod.MyFirstMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GhastEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class EntityOutlineRenderer{
//
//    public static void render(LivingEntity livingEntity, LivingEntityRenderer<?,?> renderer, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
//        matrixStack.push();
//        EntityModel<?> model = renderer.getModel();
//        model.handSwingProgress = getHandSwingProgress(livingEntity, g);
//        model.riding = livingEntity.hasVehicle();
//        model.child = livingEntity.isBaby();
//        float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
//        float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
//        float k = j - h;
//        if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
//            LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
//            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
//            k = j - h;
//            float l = MathHelper.wrapDegrees(k);
//            if (l < -85.0F) {
//                l = -85.0F;
//            }
//
//            if (l >= 85.0F) {
//                l = 85.0F;
//            }
//
//            h = j - l;
//            if (l * l > 2500.0F) {
//                h += l * 0.2F;
//            }
//
//            k = j - h;
//        }
//
//        float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
//        if (renderer.shouldFlipUpsideDown(livingEntity)) {
//            m *= -1.0F;
//            k *= -1.0F;
//        }
//
//        if (livingEntity.isInPose(EntityPose.SLEEPING)) {
//            Direction direction = livingEntity.getSleepingDirection();
//            if (direction != null) {
//                float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
//                matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
//            }
//        }
//
//        float l = getAnimationProgress(livingEntity, g);
//        setupTransforms(livingEntity, matrixStack, l, h, g);
//        matrixStack.scale(-1.0F, -1.0F, 1.0F);
//        this.scale(livingEntity, matrixStack, g);
//        matrixStack.translate(0.0F, -1.501F, 0.0F);
//        float n = 0.0F;
//        float o = 0.0F;
//        if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
//            n = livingEntity.limbAnimator.getSpeed(g);
//            o = livingEntity.limbAnimator.getPos(g);
//            if (livingEntity.isBaby()) {
//                o *= 3.0F;
//            }
//
//            if (n > 1.0F) {
//                n = 1.0F;
//            }
//        }
//
//        model.animateModel(livingEntity, o, n, g);
//        model.setAngles(livingEntity, o, n, l, k, m);
//        MinecraftClient minecraftClient = MinecraftClient.getInstance();
//        boolean bl = this.isVisible(livingEntity);
//        boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
//        boolean bl3 = minecraftClient.hasOutline(livingEntity);
//        RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
//        if (renderLayer != null) {
//            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
//            int p = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
//            model.render(matrixStack, vertexConsumer, i, p, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
//        }
//
//        if (!livingEntity.isSpectator()) {
//            for(FeatureRenderer<T, M> featureRenderer : this.features) {
//                featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
//            }
//        }
//
//        matrixStack.pop();
////        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
//    }
//
//    protected RenderLayer getRenderLayer(LivingEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
//        Identifier identifier = this.getTexture(entity);
//        return BlockGlowRenderer.TEST_LAYER;
////        if (translucent) {
////            return RenderLayer.getItemEntityTranslucentCull(identifier);
////        } else if (showBody) {
////            return this.model.getLayer(identifier);
////        } else {
////            return showOutline ? RenderLayer.getOutline(identifier) : null;
////        }
//    }
//
//    public static float getHandSwingProgress(LivingEntity entity, float tickDelta) {
//        return entity.getHandSwingProgress(tickDelta);
//    }
//
//    public static Identifier getTexture(LivingEntity entity) {
//        return null;
//    }
//
//    public static float getAnimationProgress(LivingEntity entity, float tickDelta) {
//        return (float)entity.age + tickDelta;
//    }
//
//    public static boolean isShaking(LivingEntity entity) {
//        return entity.isFrozen();
//    }
//
//
//    public static void setupTransforms(LivingEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
//        if (isShaking(entity)) {
//            bodyYaw += (float)(Math.cos((double)entity.age * (double)3.25F) * Math.PI * (double)0.4F);
//        }
//
//        if (!entity.isInPose(EntityPose.SLEEPING)) {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
//        }
//
//        if (entity.deathTime > 0) {
//            float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
//            f = MathHelper.sqrt(f);
//            if (f > 1.0F) {
//                f = 1.0F;
//            }
//
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * getLyingAngle(entity)));
//        } else if (entity.isUsingRiptide()) {
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)entity.age + tickDelta) * -75.0F));
//        } else if (entity.isInPose(EntityPose.SLEEPING)) {
//            Direction direction = entity.getSleepingDirection();
//            float g = direction != null ? getYaw(direction) : bodyYaw;
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getLyingAngle(entity)));
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
//        } else if (shouldFlipUpsideDown(entity)) {
//            matrices.translate(0.0F, entity.getHeight() + 0.1F, 0.0F);
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
//        }
//    }
//
//    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
//        if (entity instanceof PlayerEntity || entity.hasCustomName()) {
//            String string = Formatting.strip(entity.getName().getString());
//            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
//                return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
//            }
//        }
//
//        return false;
//    }
//
//    public static float getLyingAngle(LivingEntity entity) {
//        return 90.0F;
//    }
//
//    private static float getYaw(Direction direction) {
//        switch (direction) {
//            case SOUTH -> {
//                return 90.0F;
//            }
//            case WEST -> {
//                return 0.0F;
//            }
//            case NORTH -> {
//                return 270.0F;
//            }
//            case EAST -> {
//                return 180.0F;
//            }
//            default -> {
//                return 0.0F;
//            }
//        }
//    }
}
