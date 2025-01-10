package kx.myfirstmod.mixin;

import kx.myfirstmod.items.EffectGem;
import kx.myfirstmod.items.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.apache.commons.math3.complex.Quaternion;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class EffectGemPoseMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    public void modifyPose(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (player.getActiveItem() == item && item.getItem() instanceof EffectGem) {
//             Adjust transformations for the pose
            matrices.translate(0.3, -0.3, -0.8);

            Quaternionf quat = new Quaternionf().rotateAxis((float) Math.toRadians(90), new Vector3f(0,1,0));
            Quaternionf quat1 = new Quaternionf().rotateAxis((float) Math.toRadians(25), new Vector3f(1,0,0));

            matrices.multiply(quat, 0, 0, 0);
            matrices.multiply(quat1, 0, 0, 0);
        }
    }

    private static Vector3f[] extractAxes(Matrix3f normalMatrix) {
        // Extract the X, Y, and Z axes
        Vector3f xAxis = new Vector3f(normalMatrix.m00, normalMatrix.m10, normalMatrix.m20); // X-axis
        Vector3f yAxis = new Vector3f(normalMatrix.m01, normalMatrix.m11, normalMatrix.m21); // Y-axis
        Vector3f zAxis = new Vector3f(normalMatrix.m02, normalMatrix.m12, normalMatrix.m22); // Z-axis

        // Normalize the axes to ensure they are unit vectors
        xAxis = xAxis.normalize();
        yAxis = yAxis.normalize();
        zAxis = zAxis.normalize();

        return new Vector3f[]{xAxis, yAxis, zAxis};
    }
}
