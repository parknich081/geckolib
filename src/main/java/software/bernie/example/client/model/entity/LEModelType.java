package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.LEEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.model.GeoModelType;

public class LEModelType extends GeoModelType<LEEntity> {

	@Override
	public ResourceLocation getModelResource(LEEntity object) {
		return new ResourceLocation(GeckoLib.ModID, "geo/le.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(LEEntity object) {
		return new ResourceLocation(GeckoLib.ModID, "textures/entity/le.png");
	}

	@Override
	public ResourceLocation getAnimationResource(LEEntity animatable) {
		return new ResourceLocation(GeckoLib.ModID, "animations/le.animations.json");
	}

	@Override
	protected Animator<LEEntity> createAnimator(LEEntity entity) {
		return new Animator<>(entity, this)
				.createChannel()
				.setTransitionLengthTicks(5)
				.setPredicate(this::predicate)
				.build();
	}

	private AnimationBuilder predicate(AnimationChannel<LEEntity> controller, AnimationEvent<LEEntity> event) {
		return new AnimationBuilder().addAnimation("animation.geoLayerEntity.idle", true);
	}

}
