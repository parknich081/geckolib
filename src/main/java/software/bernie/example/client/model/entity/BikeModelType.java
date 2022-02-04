package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.model.GeoModelType;

public class BikeModelType extends GeoModelType<BikeEntity> {
	@Override
	public ResourceLocation getAnimationResource(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bike.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bike.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BikeEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bike.png");
	}

	@Override
	protected Animator<BikeEntity> createAnimator(BikeEntity bikeEntity) {
		return new Animator<>(bikeEntity, this)
				.createChannel()
				.setPredicate(this::predicate)
				.build();
	}

	private AnimationBuilder predicate(AnimationChannel<BikeEntity> controller, AnimationEvent<BikeEntity> event) {
		return new AnimationBuilder().addAnimation("animation.bike.idle", true);
	}
}
