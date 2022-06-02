package software.bernie.example.client.model.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.GeoModelType;

public class PistolModelType extends GeoModelType<ItemStack> {
	@Override
	public ResourceLocation getModelResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/pistol.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(ItemStack object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/item/pistol.png");
	}

	@Override
	public ResourceLocation getAnimationResource(ItemStack animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/pistol.animation.json");
	}

	@Override
	public Animator<ItemStack> createAnimator(ItemStack entity) {
		return new Animator<>(entity, createModelFor(entity), this)
				.createChannel()
				.setTransitionLengthTicks(1)
				.setPredicate(this::predicate)
				.build();
	}

	public AnimationBuilder predicate(AnimationChannel<ItemStack> controller, AnimationEvent<ItemStack> event) {
		// TODO: item animation refactor
		return null;
	}
}
