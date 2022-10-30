package com.example.demo.Service;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;

import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class GoogleVisionAPI {

	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	public Map<String, Float> getImageLabels(final String resourcePath) {

		AnnotateImageResponse response = cloudVisionTemplate.analyzeImage(
				resourceLoader.getResource(resourcePath), Type.LABEL_DETECTION, Type.TEXT_DETECTION);

		Map<String, Float> imageLabels = response.getLabelAnnotationsList().stream()
				.collect(
						Collectors.toMap(
								EntityAnnotation::getDescription,
								EntityAnnotation::getScore,
								(u, v) -> {
									throw new IllegalStateException(String.format("Duplicate key %s", u));
								},
								LinkedHashMap::new));
		return imageLabels;
	}
}
