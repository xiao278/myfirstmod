package kx.myfirstmod.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class CustomRenderLayer extends RenderLayer {
    public CustomRenderLayer(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    public static RenderLayer createCustomLayer(Identifier texture) {
        // Use protected MultiPhaseParameters via subclassing
        MultiPhaseParameters parameters = MultiPhaseParameters.builder()
                .program(RenderPhase.POSITION_COLOR_TEXTURE_PROGRAM) // Shader
                .texture(new RenderPhase.Texture(texture, false, false)) // Custom texture
                .transparency(Transparency.TRANSLUCENT_TRANSPARENCY) // Transparency
                .lightmap(Lightmap.DISABLE_LIGHTMAP) // Enable lightmaps
                .cull(Cull.DISABLE_CULLING) // Disable backface culling
                .depthTest(DepthTest.ALWAYS_DEPTH_TEST)
                .build(true); // Sort for transparency

        return RenderLayer.of(
                "custom_layer",
                VertexFormats.POSITION_COLOR_TEXTURE, // Vertex format
                VertexFormat.DrawMode.QUADS, // Draw mode
                256, // Buffer size
                false, // Has crumbling
                true, // Is translucent
                parameters
        );
    }
}
