//package kx.myfirstmod;
//
//import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
//import net.minecraft.client.render.model.json.ModelTransformationMode;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.client.world.ClientWorld;
//import net.minecraft.item.ItemStack;
//import net.minecraft.particle.ParticleTypes;
//
//import net.minecraft.util.math.random.Random;
//
//
//public class LightningStickItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
//    @Override
//    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        MinecraftClient client = MinecraftClient.getInstance();
//        if (client.world == null || client.player == null) return;
//        // render sprite
//        ClientWorld world = client.world;
//        Random random = world.random;
//        FishingBobberEntityRenderer
//        // render sprite
//        SpriteRenderer.render(
//                ModItems.LIGHTNING_STICK_MODEL,
//                mode,
//                matrices,
//                vertexConsumers,
//                light,
//                overlay,
//                world
//        );
//
//        for (int i = 0; i < 3; i++) {
//            world.addParticle(ParticleTypes.ELECTRIC_SPARK,
//                    client.player.getX() + random.nextDouble() - 0.5,
//                    client.player.getY() + 0.5 + random.nextDouble(),
//                    client.player.getZ() + random.nextDouble() - 0.5,
//                    0, 0, 0);
//        }
//    }
//}