package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import software.bernie.example.ExampleModelTypes;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.Animator;
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
		Animator<Creeper> data = new Animator<>(creeper, this);
		data.addAnimationController(new AnimationController<>(creeper, "controller", 20, this::predicate));
		return data;
	}

	private AnimationBuilder predicate(AnimationController<Creeper> controller, AnimationEvent<Creeper> event) {
		if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
			return new AnimationBuilder().addAnimation("creeper_walk", true);
		} else {
			return new AnimationBuilder().addAnimation("creeper_idle", true);
		}
	}
}
