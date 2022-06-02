package software.bernie.geckolib3.renderers.geo;

import java.awt.Color;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.core.engine.Animator;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.AnimatingModel;
import software.bernie.geckolib3.model.GeoModelType;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeoUtils;

@EventBusSubscriber
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class GeoArmorRenderer<T extends ArmorItem> extends HumanoidModel implements IGeoRenderer<ItemStack> {

	private static Map<Class<? extends ArmorItem>, Supplier<GeoArmorRenderer>> CONSTRUCTORS = new ConcurrentHashMap<>();

	private static Map<Class<? extends ArmorItem>, Map<UUID, GeoArmorRenderer<?>>> LIVING_ENTITY_RENDERERS = new ConcurrentHashMap<>();

	@SuppressWarnings("unused")
	private Class<? extends ArmorItem> assignedItemClass = null;

	protected T currentArmorItem;
	protected LivingEntity entityLiving;
	protected ItemStack itemStack;
	protected EquipmentSlot armorSlot;

	// Set these to the names of your armor's bones, or null if you aren't using
	// them
	public String headBone = "armorHead";
	public String bodyBone = "armorBody";
	public String rightArmBone = "armorRightArm";
	public String leftArmBone = "armorLeftArm";
	public String rightLegBone = "armorRightLeg";
	public String leftLegBone = "armorLeftLeg";
	public String rightBootBone = "armorRightBoot";
	public String leftBootBone = "armorLeftBoot";

	public static void registerArmorRenderer(Class<? extends ArmorItem> itemClass, GeoArmorRenderer instance) {
		for (Constructor<?> c : instance.getClass().getConstructors()) {
			if (c.getParameterCount() == 0) {
				registerArmorRenderer(itemClass, new Supplier<GeoArmorRenderer>() {

					@Override
					public GeoArmorRenderer get() {
						try {
							return (GeoArmorRenderer) c.newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
			}
		}
	}

	public static void registerArmorRenderer(Class<? extends ArmorItem> itemClass,
			Supplier<GeoArmorRenderer> rendererConstructor) {
		CONSTRUCTORS.put(itemClass, rendererConstructor);
		LIVING_ENTITY_RENDERERS.put(itemClass, new ConcurrentHashMap<>());
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item, final Entity wearer) {
		return getRenderer(item, wearer, false);
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item, final Entity wearer,
			boolean forExtendedEntity) {
		final Map<UUID, GeoArmorRenderer<?>> renderers = LIVING_ENTITY_RENDERERS.putIfAbsent(item,
				new ConcurrentHashMap<>());
		if (renderers != null) {
			GeoArmorRenderer renderer = renderers.getOrDefault(wearer.getUUID(), null);
			if (renderer == null) {
				renderer = CONSTRUCTORS.get(item).get();
				if (renderer != null) {
					renderer.assignedItemClass = item;
					renderers.put(wearer.getUUID(), renderer);
				}
			}
			if (renderer == null) {
				throw new IllegalArgumentException("Renderer not registered for item " + item);
			}
			return renderer;
		} else {
			throw new IllegalArgumentException("Renderer not registered for item " + item);
		}
	}

	private final GeoModelType<ItemStack> modelType;

	public GeoArmorRenderer(GeoModelType<ItemStack> modelType) {
		super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
		this.modelType = modelType;
	}

	@SubscribeEvent
	public static void onEntityRemoved(EntityLeaveWorldEvent event) {
		if (event.getEntity() == null) {
			return;
		}
		if (event.getEntity().getUUID() == null) {
			return;
		}
		LIVING_ENTITY_RENDERERS.values().forEach(instances -> {
			instances.remove(event.getEntity().getUUID());
		});
	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.render(0, matrixStackIn, bufferIn, packedLightIn);
	}

	public void render(float partialTicks, PoseStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 24 / 16F, 0.0D);
		stack.scale(-1.0F, -1.0F, 1.0F);

		AnimationEvent<ItemStack> itemEvent = new AnimationEvent<>(itemStack, 0, 0, 0, false,
				Arrays.asList(this.entityLiving, this.armorSlot));
		Animator<ItemStack> animator = modelType.getOrCreateAnimator(itemStack);

		animator.tickAnimation(itemEvent, GeckoLibCache.getInstance().getParser(), AnimationTickHolder.getRenderTime());
		this.fitToBiped();
		stack.pushPose();
		RenderSystem.setShaderTexture(0, getTextureResource(itemStack));
		Color renderColor = getRenderColor(itemStack, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(itemStack, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureResource(itemStack));
		render((AnimatingModel) animator.boneTree, itemStack, partialTicks, renderType, stack, null, bufferIn,
				packedLightIn, OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f,
				(float) renderColor.getGreen() / 255f, (float) renderColor.getBlue() / 255f,
				(float) renderColor.getAlpha() / 255);
		if (ModList.get().isLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		stack.scale(-1.0F, -1.0F, 1.0F);
		stack.translate(0.0D, -24 / 16F, 0.0D);
	}

	protected void fitToBiped() {
		Animator<ItemStack> data = modelType.getOrCreateAnimator(itemStack);
		if (this.headBone != null) {
			IBone headBone = data.getBone(this.headBone);
			GeoUtils.copyRotations(this.head, headBone);
			headBone.setPositionX(this.head.x);
			headBone.setPositionY(-this.head.y);
			headBone.setPositionZ(this.head.z);
		}

		if (this.bodyBone != null) {
			IBone bodyBone = data.getBone(this.bodyBone);
			GeoUtils.copyRotations(this.body, bodyBone);
			bodyBone.setPositionX(this.body.x);
			bodyBone.setPositionY(-this.body.y);
			bodyBone.setPositionZ(this.body.z);
		}

		if (this.rightArmBone != null) {
			IBone rightArmBone = data.getBone(this.rightArmBone);
			GeoUtils.copyRotations(this.rightArm, rightArmBone);
			rightArmBone.setPositionX(this.rightArm.x + 5);
			rightArmBone.setPositionY(2 - this.rightArm.y);
			rightArmBone.setPositionZ(this.rightArm.z);
		}

		if (this.leftArmBone != null) {
			IBone leftArmBone = data.getBone(this.leftArmBone);
			GeoUtils.copyRotations(this.leftArm, leftArmBone);
			leftArmBone.setPositionX(this.leftArm.x - 5);
			leftArmBone.setPositionY(2 - this.leftArm.y);
			leftArmBone.setPositionZ(this.leftArm.z);
		}

		if (this.rightLegBone != null) {
			IBone rightLegBone = data.getBone(this.rightLegBone);
			GeoUtils.copyRotations(this.rightLeg, rightLegBone);
			rightLegBone.setPositionX(this.rightLeg.x + 2);
			rightLegBone.setPositionY(12 - this.rightLeg.y);
			rightLegBone.setPositionZ(this.rightLeg.z);
			if (this.rightBootBone != null) {
				IBone rightBootBone = data.getBone(this.rightBootBone);
				GeoUtils.copyRotations(this.rightLeg, rightBootBone);
				rightBootBone.setPositionX(this.rightLeg.x + 2);
				rightBootBone.setPositionY(12 - this.rightLeg.y);
				rightBootBone.setPositionZ(this.rightLeg.z);
			}
		}

		if (this.leftLegBone != null) {
			IBone leftLegBone = data.getBone(this.leftLegBone);
			GeoUtils.copyRotations(this.leftLeg, leftLegBone);
			leftLegBone.setPositionX(this.leftLeg.x - 2);
			leftLegBone.setPositionY(12 - this.leftLeg.y);
			leftLegBone.setPositionZ(this.leftLeg.z);
			if (this.leftBootBone != null) {
				IBone leftBootBone = data.getBone(this.leftBootBone);
				GeoUtils.copyRotations(this.leftLeg, leftBootBone);
				leftBootBone.setPositionX(this.leftLeg.x - 2);
				leftBootBone.setPositionY(12 - this.leftLeg.y);
				leftBootBone.setPositionZ(this.leftLeg.z);
			}
		}
	}

	@Override
	public GeoModelType<ItemStack> getModelType() {
		return this.modelType;
	}

	public ResourceLocation getTextureResource(ItemStack instance) {
		return this.modelType.getTextureResource(instance);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer<T> setCurrentItem(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		return this;
	}

	public final GeoArmorRenderer<T> applyEntityStats(HumanoidModel defaultArmor) {
		this.young = defaultArmor.young;
		this.crouching = defaultArmor.crouching;
		this.riding = defaultArmor.riding;
		this.rightArmPose = defaultArmor.rightArmPose;
		this.leftArmPose = defaultArmor.leftArmPose;
		return this;
	}

	@SuppressWarnings("incomplete-switch")
	public GeoArmorRenderer<T> applySlot(EquipmentSlot slot) {
		IBone headBone = this.getAndHideBone(this.headBone);
		IBone bodyBone = this.getAndHideBone(this.bodyBone);
		IBone rightArmBone = this.getAndHideBone(this.rightArmBone);
		IBone leftArmBone = this.getAndHideBone(this.leftArmBone);
		IBone rightLegBone = this.getAndHideBone(this.rightLegBone);
		IBone leftLegBone = this.getAndHideBone(this.leftLegBone);
		IBone rightBootBone = this.getAndHideBone(this.rightBootBone);
		IBone leftBootBone = this.getAndHideBone(this.leftBootBone);

		switch (slot) {
		case HEAD:
			if (headBone != null)
				headBone.setHidden(false);
			break;
		case CHEST:
			if (bodyBone != null)
				bodyBone.setHidden(false);
			if (rightArmBone != null)
				rightArmBone.setHidden(false);
			if (leftArmBone != null)
				leftArmBone.setHidden(false);
			break;
		case LEGS:
			if (rightLegBone != null)
				rightLegBone.setHidden(false);
			if (leftLegBone != null)
				leftLegBone.setHidden(false);
			break;
		case FEET:
			if (rightBootBone != null)
				rightBootBone.setHidden(false);
			if (leftBootBone != null)
				leftBootBone.setHidden(false);
			break;
		}
		return this;
	}

	protected IBone getAndHideBone(String boneName) {
		if (boneName != null) {
			final IBone bone = modelType.getOrCreateAnimator(itemStack).getBone(boneName);
			if (bone != null)
				bone.setHidden(true);
			return bone;
		}
		return null;
	}

}
