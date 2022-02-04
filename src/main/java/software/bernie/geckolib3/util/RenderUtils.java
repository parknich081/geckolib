package software.bernie.geckolib3.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import software.bernie.geckolib3.core.bone.ImmutableBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public class RenderUtils {
	public static void moveToPivot(GeoCube cube, PoseStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(pivot.x() / 16, pivot.y() / 16, pivot.z() / 16);
	}

	public static void moveBackFromPivot(GeoCube cube, PoseStack stack) {
		Vector3f pivot = cube.pivot;
		stack.translate(-pivot.x() / 16, -pivot.y() / 16, -pivot.z() / 16);
	}

	public static void moveToPivot(ImmutableBone bone, PoseStack stack) {
		stack.translate(bone.getRotationX() / 16, bone.getRotationY() / 16, bone.getRotationZ() / 16);
	}

	public static void moveBackFromPivot(ImmutableBone bone, PoseStack stack) {
		stack.translate(-bone.getRotationX() / 16, -bone.getRotationY() / 16, -bone.getRotationZ() / 16);
	}

	public static void scale(ImmutableBone bone, PoseStack stack) {
		stack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translate(ImmutableBone bone, PoseStack stack) {
		stack.translate(-bone.getPositionX() / 16, bone.getPositionY() / 16, bone.getPositionZ() / 16);
	}

	public static void rotate(ImmutableBone bone, PoseStack stack) {
		if (bone.getRotationZ() != 0.0F) {
			stack.mulPose(Vector3f.ZP.rotation(bone.getRotationZ()));
		}

		if (bone.getRotationY() != 0.0F) {
			stack.mulPose(Vector3f.YP.rotation(bone.getRotationY()));
		}

		if (bone.getRotationX() != 0.0F) {
			stack.mulPose(Vector3f.XP.rotation(bone.getRotationX()));
		}
	}

	public static void rotate(GeoCube cube, PoseStack stack) {
		Vector3f rotation = cube.rotation;

		stack.mulPose(new Quaternion(0, 0, rotation.z(), false));
		stack.mulPose(new Quaternion(0, rotation.y(), 0, false));
		stack.mulPose(new Quaternion(rotation.x(), 0, 0, false));
	}
}
