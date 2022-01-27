package software.bernie.geckolib3.geo.render;

import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public interface IGeoBuilder {
	GeoModel constructGeoModel(RawGeometryTree geometryTree);
}
