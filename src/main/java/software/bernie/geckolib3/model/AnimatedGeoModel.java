package software.bernie.geckolib3.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.eliotlash.molang.MolangParser;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimated;
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

public abstract class AnimatedGeoModel<T extends IAnimated> extends GeoModelProvider<T>
		implements IAnimatableModel<T>, IAnimatableModelProvider<T> {
	private final AnimationProcessor<T> animationProcessor;

	protected AnimatedGeoModel() {
		this.animationProcessor = new AnimationProcessor<>(this);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setLivingAnimations(T entity, AnimationData manager, @Nullable AnimationEvent customPredicate) {
		// Each animation has it's own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		if (manager.startTick == null) {
			manager.startTick = getCurrentTick();
		}

		if (!Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused) {
			manager.tick = (getCurrentTick() - manager.startTick);
			double gameTick = manager.tick;
			double deltaTicks = gameTick - lastGameTickTime;
			seekTime += deltaTicks;
			lastGameTickTime = gameTick;
		}

		AnimationEvent<T> predicate;
		if (customPredicate == null) {
			predicate = new AnimationEvent<T>(entity, 0, 0, 0, false, Collections.emptyList());
		} else {
			predicate = customPredicate;
		}

		predicate.animationTick = seekTime;
		animationProcessor.preAnimationSetup(predicate.getAnimatable(), seekTime);
		if (this.animationProcessor.isNotEmpty()) {
			animationProcessor.tickAnimation(entity, manager, seekTime, predicate, GeckoLibCache.getInstance().parser,
					shouldCrashOnMissing);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AnimationProcessor getAnimationProcessor() {
		return this.animationProcessor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Animation getAnimation(String name, IAnimated animatable) {
		AnimationFile animation = GeckoLibCache.getInstance().getAnimations()
				.get(this.getAnimationFileLocation((T) animatable));
		if (animation == null) {
			throw new GeckoLibException(this.getAnimationFileLocation((T) animatable),
					"Could not find animation file. Please double check name.");
		}
		return animation.getAnimation(name);
	}

	private final Map<Pair<T, ResourceLocation>, GeoModel> models = new HashMap<>();

	@Override
	public GeoModel getModel(T entity) {
		ResourceLocation location = getModelLocation(entity);
		GeoModel masterModel = super.getModel(location);

		if (masterModel == null) {
			throw new GeckoLibException(location,
					"Could not find model. If you are getting this with a built mod, please just restart your game.");
		}
		return models.computeIfAbsent(Pair.of(entity, location), $ -> masterModel.copy());
	}

	@Override
	public void setMolangQueries(IAnimated animatable, double currentTick) {
		MolangParser parser = GeckoLibCache.getInstance().parser;
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

				float yawSpeed = livingEntity.getViewYRot((float) currentTick)
						- livingEntity.getViewYRot((float) (currentTick - 0.1));
				parser.setValue("query.yaw_speed", yawSpeed);
			}
		}
	}

	@Override
	public double getCurrentTick() {
		return Blaze3D.getTime() * 20;
	}
}
