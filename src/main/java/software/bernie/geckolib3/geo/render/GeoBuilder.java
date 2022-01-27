package software.bernie.geckolib3.geo.render;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import software.bernie.geckolib3.geo.raw.tree.RawBoneGroup;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class GeoBuilder implements IGeoBuilder {
	private static final Map<String, IGeoBuilder> moddedGeoBuilders = new HashMap<>();
	private static final IGeoBuilder defaultBuilder = new GeoBuilder();

	public static void registerGeoBuilder(String modID, IGeoBuilder builder) {
		moddedGeoBuilders.put(modID, builder);
	}

	public static IGeoBuilder getGeoBuilder(String modID) {
		IGeoBuilder builder = moddedGeoBuilders.get(modID);
		return builder == null ? defaultBuilder : builder;
	}

	@Override
	public GeoModel constructGeoModel(RawGeometryTree geometryTree) {
		ImmutableList.Builder<GeoBone> boneBuilder = ImmutableList.builder();
		for (RawBoneGroup rawBone : geometryTree.getTopLevelBones()) {
			boneBuilder.add(new GeoBone(rawBone, geometryTree.properties, null));
		}
		return new GeoModel(boneBuilder.build(), geometryTree.properties);
	}

}
