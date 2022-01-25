package software.bernie.geckolib3.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.eliotlash.molang.MolangParser;

import net.minecraft.client.Minecraft;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.Blaze3D;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

public abstract class AnimatedGeoModel<T> implements GeoModelProvider<T>, IAnimatableModel<T>, IAnimatableModelProvider<T> {
	private final AnimationProcessor<T> animationProcessor;

	public boolean shouldCrashOnMissing = false;

	protected AnimatedGeoModel() {
		this.animationProcessor = new AnimationProcessor<>(this);
	}

	public abstract ResourceLocation getModelLocation(T object);

	public abstract ResourceLocation getTextureLocation(T object);

	@Override
	public void setLivingAnimations(T entity, AnimationData manager, @Nullable AnimationEvent<T> customPredicate) {
		// Each animation has it's own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations

		AnimationEvent<T> event;
		if (customPredicate == null) {
			event = new AnimationEvent<>(entity, 0, 0, 0, false, Collections.emptyList());
		} else {
			event = customPredicate;
		}

		manager.setModelRendererList(getModel(entity).getBones());

		// TODO: this value seems to be unused
		event.animationTick = AnimationTickHolder.getRenderTime();
		animationProcessor.preAnimationSetup(event.getAnimatable(), AnimationTickHolder.getPartialTicks());
		if (this.animationProcessor.isNotEmpty()) {
			animationProcessor.tickAnimation(manager, AnimationTickHolder.getRenderTime(), event, GeckoLibCache.getInstance().parser.get(),
					shouldCrashOnMissing);
		}
	}

	@Override
	public AnimationProcessor<T> getAnimationProcessor() {
		return this.animationProcessor;
	}

	@Override
	public Animation getAnimation(String name, T animatable) {
		ResourceLocation animationLoc = this.getAnimationFileLocation(animatable);

		AnimationFile animation = GeckoLibCache.getInstance().getAnimation(animationLoc);
		if (animation == null) {
			throw new GeckoLibException(animationLoc,
					"Could not find animation file. Please double check name.");
		}
		return animation.getAnimation(name);
	}

	private final Map<T, Map<ResourceLocation, GeoModel>> safeModels = new WeakHashMap<>();

	@Override
	public GeoModel getModel(T entity) {
		ResourceLocation location = getModelLocation(entity);
		GeoModel masterModel = GeckoLibCache.getInstance()
				.getModel(location);

		if (masterModel == null) {
			throw new GeckoLibException(location,
					"Could not find model. If you are getting this with a built mod, please just restart your game.");
		}
		return safeModels.computeIfAbsent(entity, $ -> new HashMap<>()).computeIfAbsent(location, $ -> masterModel.copy());
	}

	@Override
	public void setMolangQueries(T animatable, double partialTicks) {
		MolangParser parser = GeckoLibCache.getInstance().parser.get();
		Minecraft minecraftInstance = Minecraft.getInstance();

		parser.setValue("query.actor_count", minecraftInstance.level.getEntityCount());
		parser.setValue("query.time_of_day", MolangUtils.normalizeTime(minecraftInstance.level.getDayTime()));
		parser.setValue("query.moon_phase", minecraftInstance.level.getMoonPhase());

		if (animatable instanceof Entity) {
			parser.setValue("query.distance_from_camera", minecraftInstance.gameRenderer.getMainCamera().getPosition()
					.distanceTo(((Entity) animatable).position()));
			parser.setValue("query.is_on_ground", MolangUtils.booleanToFloat(((Entity) animatable).isOnGround()));
			parser.setValue("query.is_in_water", MolangUtils.booleanToFloat(((Entity) animatable).isInWater()));
			// Should probably check specifically whether it's in rain?
			parser.setValue("query.is_in_water_or_rain",
					MolangUtils.booleanToFloat(((Entity) animatable).isInWaterRainOrBubble()));

			if (animatable instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) animatable;
				parser.setValue("query.health", livingEntity.getHealth());
				parser.setValue("query.max_health", livingEntity.getMaxHealth());
				parser.setValue("query.is_on_fire", MolangUtils.booleanToFloat(livingEntity.isOnFire()));
				// Doesn't work for some reason?
				parser.setValue("query.on_fire_time", livingEntity.getRemainingFireTicks());

				Vec3 velocity = livingEntity.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				parser.setValue("query.ground_speed", groundSpeed);

				float yawSpeed = livingEntity.getViewYRot((float) partialTicks)
						- livingEntity.getViewYRot((float) (partialTicks - 0.1));
				parser.setValue("query.yaw_speed", yawSpeed);
			}
		}
	}
}
