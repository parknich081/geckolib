// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLibMod
// Paste this class into your mod and follow the documentation for GeckoLibMod to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.model.tile;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.GeoModelType;

public class BotariumModelType extends GeoModelType<BotariumTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(BotariumTileEntity animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/botarium.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BotariumTileEntity animatable) {
		return new ResourceLocation(GeckoLib.ModID, "geo/botarium.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BotariumTileEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/block/botarium.png");
	}

	@Override
	protected Animator<BotariumTileEntity> createAnimator(BotariumTileEntity botariumTileEntity) {
		return new Animator<>(botariumTileEntity, createModelFor(botariumTileEntity), this)
				.createChannel()
				.setPredicate(this::predicate)
				.build();
	}

	private AnimationBuilder predicate(AnimationChannel<BotariumTileEntity> controller,
			AnimationEvent<BotariumTileEntity> event) {
		controller.transitionLengthTicks = 0;
		return new AnimationBuilder().addAnimation("Botarium.anim.deploy", true);
	}

}
