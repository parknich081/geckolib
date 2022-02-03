package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;
import java.util.Collections;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.Animator;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.model.GeoModelType;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class GeoItemRenderer<T extends Item> extends BlockEntityWithoutLevelRenderer implements IGeoRenderer<ItemStack> {

	protected GeoModelType<ItemStack> modelProvider;
	protected ItemStack currentItemStack;

	public GeoItemRenderer(GeoModelType<ItemStack> modelProvider) {
		this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance()
				.getEntityModels(), modelProvider);
	}

	public GeoItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet,
			GeoModelType<ItemStack> modelProvider) {
		super(dispatcher, modelSet);
		this.modelProvider = modelProvider;
	}

	public void setModel(GeoModelType<ItemStack> model) {
		this.modelProvider = model;
	}

	@Override
	public GeoModelType<ItemStack> getModelType() {
		return modelProvider;
	}

	// fixes the item lighting
	@Override
	public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType p_239207_2_, PoseStack matrixStack,
			MultiBufferSource bufferIn, int combinedLightIn, int p_239207_6_) {
		if (p_239207_2_ == ItemTransforms.TransformType.GUI) {
			matrixStack.pushPose();
			MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers()
					.bufferSource();
			Lighting.setupForFlatItems();
			this.render(matrixStack, bufferIn, combinedLightIn, itemStack);
			irendertypebuffer$impl.endBatch();
			RenderSystem.enableDepthTest();
			Lighting.setupFor3DItems();
			matrixStack.popPose();
		} else {
			this.render(matrixStack, bufferIn, combinedLightIn, itemStack);
		}
	}

	public void render(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn, ItemStack itemStack) {
		this.currentItemStack = itemStack;
		AnimationEvent<ItemStack> itemEvent = new AnimationEvent<>(itemStack, 0, 0, Minecraft.getInstance()
				.getFrameTime(), false, Collections.emptyList());
		Animator<ItemStack> data = modelProvider.getOrCreateAnimator(itemStack);
		AnimatingModel model = modelProvider.getOrCreateBoneTree(itemStack);

        data.tickAnimation(itemEvent, GeckoLibCache.getInstance().getParser(), AnimationTickHolder.getRenderTime());
        stack.pushPose();
		stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		RenderSystem.setShaderTexture(0, getTextureLocation(itemStack));
		Color renderColor = getRenderColor(itemStack, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(itemStack, 0, stack, bufferIn, null, packedLightIn, getTextureLocation(itemStack));
		render(model, itemStack, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}

	public ResourceLocation getTextureLocation(ItemStack instance) {
		return this.modelProvider.getTextureResource(instance);
	}

}
