package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.Animator;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.model.GeoModelType;
import software.bernie.geckolib3.resource.GeckoLibCache;

public abstract class GeoBlockRenderer<T extends BlockEntity> implements IGeoRenderer<T>, BlockEntityRenderer<T> {

	private final GeoModelType<T> modelType;

	public GeoBlockRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn,
			GeoModelType<T> modelType) {
		this.modelType = modelType;
	}

	@Override
	public void render(T tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		this.render(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
	}

	public void render(T tile, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		AnimatingModel model = modelType.getOrCreateBoneTree(tile);
		Animator<T> data = modelType.getOrCreateAnimator(tile);

		data.tickAnimation(new AnimationEvent<>(tile), GeckoLibCache.getInstance().getParser(), AnimationTickHolder.getRenderTime());
		stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0, 0.5);

		rotateBlock(getFacing(tile), stack);

		RenderSystem.setShaderTexture(0, getTextureLocation(tile));
		Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn, getTextureLocation(tile));
		render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	@Override
	public GeoModelType<T> getModelType() {
		return this.modelType;
	}

	protected void rotateBlock(Direction facing, PoseStack stack) {
		switch (facing) {
		case SOUTH:
			stack.mulPose(Vector3f.YP.rotationDegrees(180));
			break;
		case WEST:
			stack.mulPose(Vector3f.YP.rotationDegrees(90));
			break;
		case NORTH:
			stack.mulPose(Vector3f.YP.rotationDegrees(0));
			break;
		case EAST:
			stack.mulPose(Vector3f.YP.rotationDegrees(270));
			break;
		case UP:
			stack.mulPose(Vector3f.XP.rotationDegrees(90));
			break;
		case DOWN:
			stack.mulPose(Vector3f.XN.rotationDegrees(90));
			break;
		}
	}

	private Direction getFacing(T tile) {
		BlockState blockState = tile.getBlockState();
		if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
			return blockState.getValue(HorizontalDirectionalBlock.FACING);
		} else if (blockState.hasProperty(DirectionalBlock.FACING)) {
			return blockState.getValue(DirectionalBlock.FACING);
		} else {
			return Direction.NORTH;
		}
	}

	public ResourceLocation getTextureLocation(T instance) {
		return this.modelType.getTextureResource(instance);
	}
}
