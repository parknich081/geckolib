package software.bernie.example.client.renderer.entity;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.ExampleModelTypes;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.geo.render.AnimatingBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class ExampleGeoRenderer extends ExtendedGeoEntityRenderer<GeoExampleEntity> {

	protected final ResourceLocation CAPE_TEXTURE = new ResourceLocation(GeckoLib.ModID,
			"textures/entity/extendedrendererentity_cape.png");

	public ExampleGeoRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, ExampleModelTypes.EXAMPLE_ENTITY, 1F, 1F, 0.5F);
	}

	@Override
	public RenderType getRenderType(GeoExampleEntity animatable, float partialTicks, PoseStack stack,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	protected boolean isArmorBone(AnimatingBone bone) {
		return bone.getName().startsWith("armor");
	}

	@Override
	protected @Nullable ResourceLocation getTextureForBone(String boneName, GeoExampleEntity currentEntity) {
		switch (boneName) {
		case "bipedCape":
			return CAPE_TEXTURE;
		default:
			return null;
		}
	}

	@Override
	protected ModelPart getArmorPartForBone(String name, HumanoidModel<?> armorModel) {
		switch (name) {
		case "armorBipedLeftFoot":
		case "armorBipedLeftLeg":
		case "armorBipedLeftFoot2":
		case "armorBipedLeftLeg2":
			return armorModel.leftLeg;
		case "armorBipedRightFoot":
		case "armorBipedRightLeg":
		case "armorBipedRightFoot2":
		case "armorBipedRightLeg2":
			return armorModel.rightLeg;
		case "armorBipedRightArm":
			return armorModel.rightArm;
		case "armorBipedLeftArm":
			return armorModel.leftArm;
		case "armorBipedBody":
			return armorModel.body;
		case "armorBipedHead":
			return armorModel.head;
		default:
			return null;
		}
	}

	@Override
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, GeoExampleEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
		case "armorBipedLeftFoot2":
		case "armorBipedRightFoot2":
			return EquipmentSlot.FEET;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
		case "armorBipedLeftLeg2":
		case "armorBipedRightLeg2":
			return EquipmentSlot.LEGS;
		case "armorBipedRightArm":
			return !currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		case "armorBipedLeftArm":
			return currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
		case "armorBipedBody":
			return EquipmentSlot.CHEST;
		case "armorBipedHead":
			return EquipmentSlot.HEAD;
		default:
			return null;
		}
	}

	@Override
	protected ItemStack getArmorForBone(String boneName, GeoExampleEntity currentEntity) {
		switch (boneName) {
		case "armorBipedLeftFoot":
		case "armorBipedRightFoot":
		case "armorBipedLeftFoot2":
		case "armorBipedRightFoot2":
			return boots;
		case "armorBipedLeftLeg":
		case "armorBipedRightLeg":
		case "armorBipedLeftLeg2":
		case "armorBipedRightLeg2":
			return leggings;
		case "armorBipedBody":
		case "armorBipedRightArm":
		case "armorBipedLeftArm":
			return chestplate;
		case "armorBipedHead":
			return helmet;
		default:
			return null;
		}
	}

	@Override
	protected @Nullable ItemStack getHeldItemForBone(String boneName, GeoExampleEntity currentEntity) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? mainHand : offHand;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return currentEntity.isLeftHanded() ? offHand : mainHand;
		case DefaultBipedBoneIdents.POTION_BONE_IDENT:
			break;
		}
		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		switch (boneName) {
		case DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		case DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT:
			return TransformType.THIRD_PERSON_RIGHT_HAND;
		default:
			return TransformType.NONE;
		}
	}

	@Override
	protected @Nullable BlockState getHeldBlockForBone(String boneName, GeoExampleEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderItem(PoseStack stack, ItemStack item, String boneName, GeoExampleEntity currentEntity,
			IBone bone) {
		if (item == this.mainHand || item == this.offHand) {
			stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
			boolean shieldFlag = item.getItem() instanceof ShieldItem;
			if (item == this.mainHand) {
				if (shieldFlag) {
					stack.translate(0.0, 0.125, -0.25);
				} else {

				}
			} else {
				if (shieldFlag) {
					stack.translate(0, 0.125, 0.25);
					stack.mulPose(Vector3f.YP.rotationDegrees(180));
				} else {

				}

			}
		}
	}

	@Override
	protected void preRenderBlock(BlockState block, String boneName, GeoExampleEntity currentEntity) {

	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName,
			GeoExampleEntity currentEntity, IBone bone) {

	}

	@Override
	protected void postRenderBlock(BlockState block, String boneName, GeoExampleEntity currentEntity) {

	}
}
