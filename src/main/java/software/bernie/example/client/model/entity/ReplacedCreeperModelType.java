package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.GeoModelType;

public class ReplacedCreeperModelType extends GeoModelType<Creeper> {
	@Override
	public ResourceLocation getModelResource(Creeper object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/creeper.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(Creeper object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/creeper.png");
	}

	@Override
	public ResourceLocation getAnimationResource(Creeper animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/creeper.animation.json");
	}

	@Override
	public Animator<Creeper> createAnimator(Creeper creeper) {
		return new Animator<>(creeper, createModelFor(creeper), this)
				.createChannel()
				.setPredicate(this::predicate)
				.setTransitionLengthTicks(20)
				.build();
	}

	private AnimationBuilder predicate(AnimationChannel<Creeper> controller, AnimationEvent<Creeper> event) {
		if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
			return new AnimationBuilder().addAnimation("creeper_walk", true);
		} else {
			return new AnimationBuilder().addAnimation("creeper_idle", true);
		}
	}
}
