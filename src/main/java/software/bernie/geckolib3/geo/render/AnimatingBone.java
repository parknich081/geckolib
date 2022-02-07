package software.bernie.geckolib3.geo.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import software.bernie.geckolib3.core.bone.IBone;
import software.bernie.geckolib3.core.bone.ImmutableBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;

public class AnimatingBone implements IBone {

	public final GeoBone bone;
	public final AnimatingBone parent;
	public final AnimatingModel model;

	public final List<AnimatingBone> childBones = new ArrayList<>();

	public boolean isHidden;

	public float scaleX;
	public float scaleY;
	public float scaleZ;

	public float positionX;
	public float positionY;
	public float positionZ;

	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;

	public float rotationX;
	public float rotationY;
	public float rotationZ;

	public AnimatingBone(AnimatingModel model, GeoBone bone) {
		this(model, bone, null);
	}

	private AnimatingBone(AnimatingModel model, GeoBone bone, @Nullable AnimatingBone parent) {
		this.bone = bone;
		this.parent = parent;
		this.model = model;

		this.scaleX = bone.scaleX;
		this.scaleY = bone.scaleY;
		this.scaleZ = bone.scaleZ;
		this.positionX = bone.positionX;
		this.positionY = bone.positionY;
		this.positionZ = bone.positionZ;
		this.rotationPointX = bone.rotationPointX;
		this.rotationPointY = bone.rotationPointY;
		this.rotationPointZ = bone.rotationPointZ;
		this.rotationX = bone.rotationX;
		this.rotationY = bone.rotationY;
		this.rotationZ = bone.rotationZ;
		this.isHidden = bone.isHidden;

		for (GeoBone childBone : bone.childBones) {
			this.childBones.add(new AnimatingBone(model, childBone, this));
		}
	}

	public void walkDepthFirst(Consumer<AnimatingBone> consumer) {
		consumer.accept(this);
		for (AnimatingBone childBone : this.childBones) {
			childBone.walkDepthFirst(consumer);
		}
	}

	@Override
	public ImmutableBone getSourceBone() {
		return this.bone;
	}

	@Override
	public void setRotationX(float value) {
		this.rotationX = value;
	}

	@Override
	public void setRotationY(float value) {
		this.rotationY = value;
	}

	@Override
	public void setRotationZ(float value) {
		this.rotationZ = value;
	}

	@Override
	public void setPositionX(float value) {
		this.positionX = value;
	}

	@Override
	public void setPositionY(float value) {
		this.positionY = value;
	}

	@Override
	public void setPositionZ(float value) {
		this.positionZ = value;
	}

	@Override
	public void setScaleX(float value) {
		this.scaleX = value;
	}

	@Override
	public void setScaleY(float value) {
		this.scaleY = value;
	}

	@Override
	public void setScaleZ(float value) {
		this.scaleZ = value;
	}

	@Override
	public void setPivotX(float value) {
		this.rotationPointX = value;
	}

	@Override
	public void setPivotY(float value) {
		this.rotationPointY = value;
	}

	@Override
	public void setPivotZ(float value) {
		this.rotationPointZ = value;
	}

	@Override
	public void setHidden(boolean hidden) {
		this.isHidden = hidden;
	}

	@Override
	public float getRotationX() {
		return this.rotationX;
	}

	@Override
	public float getRotationY() {
		return this.rotationY;
	}

	@Override
	public float getRotationZ() {
		return this.rotationZ;
	}

	@Override
	public float getPositionX() {
		return this.positionX;
	}

	@Override
	public float getPositionY() {
		return this.positionY;
	}

	@Override
	public float getPositionZ() {
		return this.positionZ;
	}

	@Override
	public float getScaleX() {
		return this.scaleX;
	}

	@Override
	public float getScaleY() {
		return this.scaleY;
	}

	@Override
	public float getScaleZ() {
		return this.scaleZ;
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

	@Override
	public boolean isHidden() {
		return this.isHidden;
	}

	@Override
	public String getName() {
		return bone.name;
	}
}
