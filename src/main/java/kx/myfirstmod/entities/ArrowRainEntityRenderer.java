package kx.myfirstmod.entities;

import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

public class ArrowRainEntityRenderer extends ArrowEntityRenderer {

    public ArrowRainEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(ArrowEntity arrowEntity, float yaw, float tickDelta, MatrixStack matrices,
                       net.minecraft.client.render.VertexConsumerProvider vertexConsumers, int light) {
        // Apply scaling transformation
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F); // Scale the arrow (2x size)

        // Call the super method to render the arrow
        super.render(arrowEntity, yaw, tickDelta, matrices, vertexConsumers, light);

        matrices.pop();
    }

    @Override
    public Identifier getTexture(ArrowEntity entity) {
        return super.getTexture(entity); // Use default arrow texture
    }
}
