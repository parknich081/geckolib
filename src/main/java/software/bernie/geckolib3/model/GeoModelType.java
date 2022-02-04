package software.bernie.geckolib3.model;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.eliotlash.molang.MolangParser;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.ModelType;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.MolangUtils;

public abstract class GeoModelType<T> implements ModelType<T> {

	private final Map<T, Map<ResourceLocation, AnimatingModel>> models = new WeakHashMap<>();
	private final Map<T, Animator<T>> animators = new WeakHashMap<>();

	protected GeoModelType() {
	}

	@Override
	public Animator<T> getOrCreateAnimator(T entity) {
		return animators.computeIfAbsent(entity, this::createAnimator);
	}

	@Override
	public Animation getAnimation(T animatable, String name) {
		ResourceLocation animationLoc = this.getAnimationResource(animatable);

		AnimationFile animation = GeckoLibCache.getInstance().getAnimation(animationLoc);
		if (animation == null) {
			throw new GeckoLibException(animationLoc, "Could not find animation file. Please double check name.");
		}
		return animation.getAnimation(name);
	}

	@Override
	public AnimatingModel getOrCreateBoneTree(T entity) {
		ResourceLocation location = getModelResource(entity);
		GeoModel masterModel = GeckoLibCache.getInstance().getModel(location);

		if (masterModel == null) {
			throw new GeckoLibException(location, "Could not find model. If you are getting this with a built mod, please just restart your game.");
		}
		return models.computeIfAbsent(entity, $ -> new HashMap<>())
				.computeIfAbsent(location, $ -> new AnimatingModel(masterModel));
	}

	@Override
	public void setMolangQueries(T object, MolangParser parser, double renderTime) {
		Minecraft minecraftInstance = Minecraft.getInstance();

		parser.setValue("query.actor_count", minecraftInstance.level.getEntityCount());
		parser.setValue("query.time_of_day", MolangUtils.normalizeTime(minecraftInstance.level.getDayTime()));
		parser.setValue("query.moon_phase", minecraftInstance.level.getMoonPhase());

		if (object instanceof Entity entity) {
			parser.setValue("query.distance_from_camera", minecraftInstance.gameRenderer.getMainCamera().getPosition()
					.distanceTo(entity.position()));
			parser.setValue("query.is_on_ground", MolangUtils.booleanToFloat(entity.isOnGround()));
			parser.setValue("query.is_in_water", MolangUtils.booleanToFloat(entity.isInWater()));
			// Should probably check specifically whether it's in rain?
			parser.setValue("query.is_in_water_or_rain", MolangUtils.booleanToFloat(entity.isInWaterRainOrBubble()));

			if (entity instanceof LivingEntity livingEntity) {
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

	protected abstract Animator<T> createAnimator(T t);

	public abstract ResourceLocation getModelResource(T object);

	public abstract ResourceLocation getTextureResource(T object);

	/**
	 * This resource location needs to point to a json file of your animation file,
	 * i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	public abstract ResourceLocation getAnimationResource(T animatable);
}
