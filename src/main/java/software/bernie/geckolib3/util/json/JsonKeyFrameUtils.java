/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.util.json;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.eliotlash.molang.math.Constant;
import com.eliotlash.molang.math.IValue;
import com.eliotlash.molang.MolangException;
import com.eliotlash.molang.MolangParser;
import com.eliotlash.molang.math.Negative;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.Timeline;
import software.bernie.geckolib3.core.keyframe.VectorTimeline;
import software.bernie.geckolib3.util.AnimationUtils;

/**
 * Helper class to convert json to keyframes
 */
public class JsonKeyFrameUtils {
	private static VectorTimeline convertJson(List<Map.Entry<String, JsonElement>> element,
			boolean isRotation, MolangParser parser) throws
			NumberFormatException,
			MolangException {
		IValue previousXValue = null;
		IValue previousYValue = null;
		IValue previousZValue = null;

		ImmutableList.Builder<KeyFrame> xKeyFrames = ImmutableList.builder();
		ImmutableList.Builder<KeyFrame> yKeyFrames = ImmutableList.builder();
		ImmutableList.Builder<KeyFrame> zKeyFrames = ImmutableList.builder();

		for (int i = 0; i < element.size(); i++) {
			Map.Entry<String, JsonElement> keyframe = element.get(i);
			if (keyframe.getKey().equals("easing") || keyframe.getKey().equals("easingArgs")) continue;
			Map.Entry<String, JsonElement> previousKeyFrame = i == 0 ? null : element.get(i - 1);

			Double previousKeyFrameLocation = previousKeyFrame == null ? 0 : Double.parseDouble(previousKeyFrame.getKey());
			Double currentKeyFrameLocation = NumberUtils.isCreatable(keyframe.getKey()) ? Double.parseDouble(keyframe.getKey()) : 0;
			double length = AnimationUtils.convertSecondsToTicks(currentKeyFrameLocation - previousKeyFrameLocation);

			JsonArray vectorJsonArray = getKeyFrameVector(keyframe.getValue());

			// For rotation:
			// Dynamic X and Y values get negated.
			// Constant values get converted to radians.
			IValue currentXValue = parseExpression(parser, vectorJsonArray.get(0), true, isRotation);
			IValue currentYValue = parseExpression(parser, vectorJsonArray.get(1), true, isRotation);
			IValue currentZValue = parseExpression(parser, vectorJsonArray.get(2), false, isRotation);

			KeyFrame xKeyFrame;
			KeyFrame yKeyFrame;
			KeyFrame zKeyFrame;

			if (keyframe.getValue().isJsonObject() && hasEasingType(keyframe.getValue())) {
				EasingType easingType = getEasingType(keyframe.getValue());
				if (hasEasingArgs(keyframe.getValue())) {
					double[] easingArgs = getEasingArgs(keyframe.getValue());
					Double arg = easingArgs.length > 0 ? easingArgs[0] : null;
					xKeyFrame = new KeyFrame(length, i == 0 ? currentXValue : previousXValue, currentXValue, easingType, arg);
					yKeyFrame = new KeyFrame(length, i == 0 ? currentYValue : previousYValue, currentYValue, easingType, arg);
					zKeyFrame = new KeyFrame(length, i == 0 ? currentZValue : previousZValue, currentZValue, easingType, arg);
				} else {
					xKeyFrame = new KeyFrame(length, i == 0 ? currentXValue : previousXValue, currentXValue, easingType);
					yKeyFrame = new KeyFrame(length, i == 0 ? currentYValue : previousYValue, currentYValue, easingType);
					zKeyFrame = new KeyFrame(length, i == 0 ? currentZValue : previousZValue, currentZValue, easingType);

				}
			} else {
				xKeyFrame = new KeyFrame(length, i == 0 ? currentXValue : previousXValue, currentXValue);
				yKeyFrame = new KeyFrame(length, i == 0 ? currentYValue : previousYValue, currentYValue);
				zKeyFrame = new KeyFrame(length, i == 0 ? currentZValue : previousZValue, currentZValue);
			}

			previousXValue = currentXValue;
			previousYValue = currentYValue;
			previousZValue = currentZValue;

			xKeyFrames.add(xKeyFrame);
			yKeyFrames.add(yKeyFrame);
			zKeyFrames.add(zKeyFrame);
		}

		return new VectorTimeline(new Timeline(xKeyFrames.build()), new Timeline(yKeyFrames.build()), new Timeline(zKeyFrames.build()));
	}

	private static JsonArray getKeyFrameVector(JsonElement element) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		} else {
			return element.getAsJsonObject().get("vector").getAsJsonArray();
		}
	}

	private static boolean hasEasingType(JsonElement element) {
		return element.getAsJsonObject().has("easing");
	}

	private static boolean hasEasingArgs(JsonElement element) {
		return element.getAsJsonObject().has("easingArgs");
	}

	private static EasingType getEasingType(JsonElement element) {
		final String easingString = element.getAsJsonObject().get("easing").getAsString();
		try {
			final String uppercaseEasingString = Character.toUpperCase(easingString.charAt(0)) + easingString.substring(1);
			return EasingType.valueOf(uppercaseEasingString);
		} catch (Exception e) {
			GeckoLib.LOGGER.fatal("Unknown easing type: {}", easingString);
			throw new RuntimeException(e);
		}
	}

	private static double[] getEasingArgs(JsonElement element) {
		JsonArray args = element.getAsJsonObject().getAsJsonArray("easingArgs");

		double[] out = new double[args.size()];
		for (int i = 0; i < args.size(); i++) {
			out[i] = args.get(i).getAsDouble();
		}
		return out;
	}

	/**
	 * Convert json to a rotation key frame vector list. This method also converts
	 * degrees to radians.
	 *
	 * @param element The keyframe parent json element
	 * @param parser
	 * @return the vector key frame list
	 * @throws NumberFormatException The number format exception
	 */
	public static VectorTimeline convertJsonToKeyFrames(List<Map.Entry<String, JsonElement>> element,
			MolangParser parser) throws
			NumberFormatException,
			MolangException {
		return convertJson(element, false, parser);
	}

	/**
	 * Convert json to normal json keyframes
	 *
	 * @param element The keyframe parent json element
	 * @param parser
	 * @return the vector key frame list
	 * @throws NumberFormatException
	 */
	public static VectorTimeline convertJsonToRotationKeyFrames(
			List<Map.Entry<String, JsonElement>> element, MolangParser parser) throws
			NumberFormatException,
			MolangException {
		return convertJson(element, true, parser);
	}

	public static IValue parseExpression(MolangParser parser, JsonElement element, boolean negateExpression, boolean isRotation) throws
			MolangException {
		if (element.getAsJsonPrimitive().isString()) {
			if (negateExpression && isRotation) {
				return new Negative(parser.parseJson(element));
			} else {
				return parser.parseJson(element);
			}
		} else {
			if (isRotation) {
				return new Constant(Math.toRadians(element.getAsDouble()));
			} else {
				return new Constant(element.getAsDouble());
			}
		}
	}
}
