package kx.myfirstmod.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SpriteRenderer {
    public static void render(Item underlyingItem, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, World world) {
        MinecraftClient client = MinecraftClient.getInstance();
        matrices.push();
        switch (mode) {
            case THIRD_PERSON_RIGHT_HAND:
                break;

            case THIRD_PERSON_LEFT_HAND:
                break;

            case FIRST_PERSON_RIGHT_HAND:
                break;

            case FIRST_PERSON_LEFT_HAND:
                break;

            case GUI:
                matrices.translate(0.5,0.5,0);
                overlay = OverlayTexture.DEFAULT_UV;
                light = 0xF000F0;
                break;

            default:
                break;
        }
        client.getItemRenderer().renderItem(new ItemStack(underlyingItem), mode, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
    }
}
