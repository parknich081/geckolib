package software.bernie.geckolib3.geo.render.built;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Vector3f;

import software.bernie.geckolib3.core.processor.ImmutableBone;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.geo.raw.pojo.Cube;
import software.bernie.geckolib3.geo.raw.pojo.ModelProperties;
import software.bernie.geckolib3.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3.util.VectorUtils;

public class GeoBone implements ImmutableBone {
	@Nullable
	public final GeoBone parent;

	public final ImmutableList<GeoBone> childBones;
	public final ImmutableList<GeoCube> childCubes;

	public final String name;

	public final boolean isHidden;

	public final float scaleX = 1;
	public final float scaleY = 1;
	public final float scaleZ = 1;

	public final float positionX = 0;
	public final float positionY = 0;
	public final float positionZ = 0;

	public final float rotationPointX;
	public final float rotationPointY;
	public final float rotationPointZ;

	public final float rotationX;
	public final float rotationY;
	public final float rotationZ;

	/**
	 * Deserializes a bone from a raw bone group.
	 *
	 * @param bone The raw bone group.
	 * @param properties The properties of the model.
	 * @param parent This bone's parent, or null if this is the root bone.
	 * @implNote {@code parent.childBones} will be uninitialized.
	 */
	public GeoBone(RawBoneGroup bone, ModelProperties properties, @Nullable GeoBone parent) {
		Bone rawBone = bone.selfBone;
		Vector3f rotation = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getRotation()));
		Vector3f pivot = VectorUtils.convertDoubleToFloat(VectorUtils.fromArray(rawBone.getPivot()));
		rotation.mul(-1, -1, 1);

		this.isHidden = rawBone.getNeverRender() != null && rawBone.getNeverRender();
		this.parent = parent;
		this.name = rawBone.getName();

		this.rotationX = (float) Math.toRadians(rotation.x());
		this.rotationY = (float) Math.toRadians(rotation.y());
		this.rotationZ = (float) Math.toRadians(rotation.z());

		this.rotationPointX = -pivot.x();
		this.rotationPointY = pivot.y();
		this.rotationPointZ = pivot.z();

		ImmutableList.Builder<GeoCube> cubes = ImmutableList.builder();

		boolean mirror = rawBone.getMirror() != null && rawBone.getMirror();
		Double boneInflate = rawBone.getInflate() == null ? null : rawBone.getInflate() / 16;
		if (!ArrayUtils.isEmpty(rawBone.getCubes())) {
			for (Cube cube : rawBone.getCubes()) {
				cubes.add(new GeoCube(cube, properties, boneInflate, mirror));
			}
		}

		this.childCubes = cubes.build();
		ImmutableList.Builder<GeoBone> bones = ImmutableList.builder();

		for (RawBoneGroup child : bone.getChildren()) {
			bones.add(new GeoBone(child, properties, this));
		}

		this.childBones = bones.build();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getRotationX() {
		return rotationX;
	}

	@Override
	public float getRotationY() {
		return rotationY;
	}

	@Override
	public float getRotationZ() {
		return rotationZ;
	}

	@Override
	public float getPositionX() {
		return positionX;
	}

	@Override
	public float getPositionY() {
		return positionY;
	}

	@Override
	public float getPositionZ() {
		return positionZ;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public float getScaleZ() {
		return scaleZ;
	}

	@Override
	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public float getPivotX() {
		return this.rotationPointX;
	}

	@Override
	public float getPivotY() {
		return this.rotationPointY;
	}

	@Override
	public float getPivotZ() {
		return this.rotationPointZ;
	}

	@Nullable
	public GeoBone getParent() {
		return this.parent;
	}
}
