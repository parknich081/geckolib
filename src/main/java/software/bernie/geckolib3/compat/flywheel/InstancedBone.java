package software.bernie.geckolib3.compat.flywheel;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib3.geo.render.AnimatingBone;

public class InstancedBone {

	public final AnimatingBone bone;
	private final ModelData boneInstance;

	private final List<InstancedBone> children;

	private UpdateTask action;

	private boolean hidden = false;

	private float lastScaleX = Float.NaN;
	private float lastScaleY = Float.NaN;
	private float lastScaleZ = Float.NaN;
	private float lastPositionX = Float.NaN;
	private float lastPositionY = Float.NaN;
	private float lastPositionZ = Float.NaN;
	private float lastRotationX = Float.NaN;
	private float lastRotationY = Float.NaN;
	private float lastRotationZ = Float.NaN;
	private float lastPivotX = Float.NaN;
	private float lastPivotY = Float.NaN;
	private float lastPivotZ = Float.NaN;

	private final Matrix4f poseMat = new Matrix4f();
	private final Matrix3f normalMat = new Matrix3f();

	public InstancedBone(MaterialManager materialManager, RenderType tex, AnimatingBone bone) {
		this.bone = bone;

		if (bone.bone.childCubes.isEmpty()) {
			boneInstance = null;
		} else {
			boneInstance = materialManager.cutout(tex).material(Materials.TRANSFORMED)
					.model(bone.bone, () -> new BoneModel(bone.bone)).createInstance().loadIdentity();
		}

		ImmutableList.Builder<InstancedBone> builder = ImmutableList.builder();
		for (AnimatingBone childBone : bone.childBones) {
			builder.add(new InstancedBone(materialManager, tex, childBone));
		}

		children = builder.build();
	}

	/**
	 * Figure out what needs to be done this frame.
	 *
	 * <p>
	 * This calculates the minimal amount of work needed to correctly update this bone.
	 * </p>
	 *
	 * @return The task we will perform.
	 */
	public UpdateTask recursiveCheckNeedsUpdate() {
		// by default, we assume there's nothing to do
		action = UpdateTask.SKIP;

		// hidden propagates to children, no need to search further
		if (bone.isHidden() && !hidden) {
			return action = UpdateTask.HIDE;
		}

		// no need to search further
		// changes to our bone MUST pe passed on to all our child bones
		if (boneNeedsUpdate()) {
			return action = UpdateTask.UPDATE;
		}

		for (InstancedBone child : children) {
			UpdateTask childTask = child.recursiveCheckNeedsUpdate();

			// don't early return here because we need to check all the children too
			if (childTask.needsParentPassthrough()) action = UpdateTask.PASSTHROUGH;
		}

		return action;
	}

	public void transform(PoseStack stack) {
		switch (action) {
		case HIDE:
			hide();
			break;
		case PASSTHROUGH:
			passThrough(stack);
			break;
		case UPDATE:
			forceUpdate(stack);

			//
			this.lastScaleX = bone.getScaleX();
			this.lastScaleY = bone.getScaleY();
			this.lastScaleZ = bone.getScaleZ();
			this.lastPositionX = bone.getPositionX();
			this.lastPositionY = bone.getPositionY();
			this.lastPositionZ = bone.getPositionZ();
			this.lastRotationX = bone.getRotationX();
			this.lastRotationY = bone.getRotationY();
			this.lastRotationZ = bone.getRotationZ();
			this.lastPivotX = bone.getPivotX();
			this.lastPivotY = bone.getPivotY();
			this.lastPivotZ = bone.getPivotZ();
		default:
		}
	}

	private void passThrough(PoseStack stack) {
		pushTransforms(stack);

		for (InstancedBone child : children) {
			child.transform(stack);
		}

		stack.popPose();
	}

	private void forceUpdate(PoseStack stack) {
		pushTransforms(stack);

		if (boneInstance != null) boneInstance.setTransform(stack);

		for (InstancedBone child : children) {
			child.forceUpdate(stack);
		}

		stack.popPose();
	}

	private void pushTransforms(PoseStack stack) {
		stack.pushPose();
		if (boneNeedsUpdate()) recalculate();
		stack.last().pose().multiply(poseMat);
		stack.last().normal().mul(normalMat);
	}

	private void recalculate() {
		var rotation = new Quaternion(bone.getRotationX(), bone.getRotationY(), bone.getRotationZ(), false);

		poseMat.setIdentity();
		poseMat.translate(new Vector3f( -bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16));
		poseMat.multiplyWithTranslation(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
		poseMat.multiply(rotation);
		poseMat.multiplyWithTranslation(-bone.getPivotX() / 16, -bone.getPivotY() / 16, -bone.getPivotZ() / 16);
		poseMat.multiply(Matrix4f.createScaleMatrix(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()));

		normalMat.load(new Matrix3f(rotation));
	}

	private boolean boneNeedsUpdate() {
		return this.lastScaleX != bone.getScaleX() || this.lastScaleY != bone.getScaleY() || this.lastScaleZ != bone.getScaleZ() || this.lastPositionX != bone.getPositionX() || this.lastPositionY != bone.getPositionY() || this.lastPositionZ != bone.getPositionZ() || this.lastRotationX != bone.getRotationX() || this.lastRotationY != bone.getRotationY() || this.lastRotationZ != bone.getRotationZ() || this.lastPivotX != bone.getPivotX() || this.lastPivotY != bone.getPivotY() || this.lastPivotZ != bone.getPivotZ();
	}

	private void hide() {
		if (boneInstance != null) boneInstance.setEmptyTransform();

		children.forEach(InstancedBone::hide);

		hidden = true;
	}

	public void delete() {
		if (boneInstance != null) boneInstance.delete();

		children.forEach(InstancedBone::delete);
	}

	public void updateLight(int blockLight, int skyLight) {
		if (boneInstance != null) {
			boneInstance.setBlockLight(blockLight).setSkyLight(skyLight);
		}

		for (InstancedBone child : children) {
			child.updateLight(blockLight, skyLight);
		}
	}

	@Override
	public String toString() {
		return bone.getName() + (bone.parent == null ? "" : " :- " + bone.parent.getName());
	}
}
