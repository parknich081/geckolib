package software.bernie.geckolib3.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.eliotlash.molang.MolangParser;
import com.jozufozu.flywheel.util.AnimationTickHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.AnimationPage;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

public abstract class AnimatedGeoModel<T> implements AnimationPage<T> {

	public boolean shouldCrashOnMissing = false;

	protected AnimatedGeoModel() {
	}

	public abstract ResourceLocation getModelResource(T object);

	public abstract ResourceLocation getTextureResource(T object);

	/**
	 * This resource location needs to point to a json file of your animation file,
	 * i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	public abstract ResourceLocation getAnimationFileResource(T animatable);

	public void setLivingAnimations(T entity, AnimationData data) {
		this.setLivingAnimations(entity, data, new AnimationEvent<>(entity, 0, 0, 0, false, Collections.emptyList()));
	}

	public void setLivingAnimations(T entity, AnimationData manager, @Nonnull AnimationEvent<T> event) {
		// Each animation has it's own collection of animations (called the
		// EntityAnimationManager), which allows for multiple independent animations
		MolangParser molangParser = GeckoLibCache.getInstance().getParser();

		setMolangQueries(molangParser, entity, AnimationTickHolder.getPartialTicks());

		manager.setBoneTree(getModel(entity));

		// TODO: this value seems to be unused
		event.animationTick = AnimationTickHolder.getRenderTime();
		manager.tickAnimation(event, this, molangParser, AnimationTickHolder.getRenderTime());
	}

	@Override
	public Animation getAnimation(T animatable, String name) {
		ResourceLocation animationLoc = this.getAnimationFileResource(animatable);

		AnimationFile animation = GeckoLibCache.getInstance().getAnimation(animationLoc);
		if (animation == null) {
			throw new GeckoLibException(animationLoc, "Could not find animation file. Please double check name.");
		}
		return animation.getAnimation(name);
	}

	private final Map<T, Map<ResourceLocation, AnimatingModel>> safeModels = new WeakHashMap<>();

	public AnimatingModel getModel(T entity) {
		ResourceLocation location = getModelResource(entity);
		GeoModel masterModel = GeckoLibCache.getInstance().getModel(location);

		if (masterModel == null) {
			throw new GeckoLibException(location, "Could not find model. If you are getting this with a built mod, please just restart your game.");
		}
		return safeModels.computeIfAbsent(entity, $ -> new HashMap<>())
				.computeIfAbsent(location, $ -> new AnimatingModel(masterModel));
	}

	public void setMolangQueries(MolangParser parser, T animatable, double renderTime) {
		Minecraft minecraftInstance = Minecraft.getInstance();

		parser.setValue("query.actor_count", minecraftInstance.level.getEntityCount());
		parser.setValue("query.time_of_day", MolangUtils.normalizeTime(minecraftInstance.level.getDayTime()));
		parser.setValue("query.moon_phase", minecraftInstance.level.getMoonPhase());

		if (animatable instanceof Entity entity) {
			parser.setValue("query.distance_from_camera", minecraftInstance.gameRenderer.getMainCamera().getPosition()
					.distanceTo(entity.position()));
			parser.setValue("query.is_on_ground", MolangUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue("query.is_in_water", MolangUtils.booleanToFloat(entity.isInWater()));
			// Should probably check specifically whether it's in rain?
			parser.setValue("query.is_in_water_or_rain", MolangUtils.booleanToFloat(entity.isInWaterRainOrBubble()));

			if (animatable instanceof LivingEntity livingEntity) {
				parser.setValue("query.health", livingEntity.getHealth());
				parser.setValue("query.max_health", livingEntity.getMaxHealth());
				parser.setValue("query.is_on_fire", MolangUtils.booleanToFloat(livingEntity.isOnFire()));
				// Doesn't work for some reason?
				parser.setValue("query.on_fire_time", livingEntity.getRemainingFireTicks());

				Vec3 velocity = livingEntity.getDeltaMovement();
				float groundSpeed = Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
				parser.setValue("query.ground_speed", groundSpeed);

				float yawSpeed = livingEntity.getViewYRot((float) renderTime) - livingEntity.getViewYRot((float) (renderTime - 0.1));
				parser.setValue("query.yaw_speed", yawSpeed);
			}
		}
	}
}
