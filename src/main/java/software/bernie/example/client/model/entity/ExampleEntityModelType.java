package software.bernie.example.client.model.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.example.entity.ExampleEntityAnimator;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.engine.AnimationChannel;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.model.GeoModelType;

public class ExampleEntityModelType extends GeoModelType<GeoExampleEntity> {
	@Override
	public ResourceLocation getAnimationResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "animations/bat.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "geo/bat.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GeoExampleEntity entity) {
		return new ResourceLocation(GeckoLib.ModID, "textures/model/entity/bat.png");
	}

	@Override
	protected Animator<GeoExampleEntity> createAnimator(GeoExampleEntity geoExampleEntity) {
		return new ExampleEntityAnimator(geoExampleEntity, this)
				.createChannel()
				.setPredicate(this::predicate)
				.build();
	}

	private AnimationBuilder predicate(
			AnimationChannel<GeoExampleEntity> controller, AnimationEvent<GeoExampleEntity> event) {
		return new AnimationBuilder().addAnimation("animation.bat.fly", true);
	}

}
