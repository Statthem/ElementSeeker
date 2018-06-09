package com.agileengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main {

	private static String CHARSET_NAME = "utf8";

	final static String PATH = "./samples/";

	public static void main(String args[]) {

		String originalResource = PATH + args[0];
		String diff_caseResource = PATH + args[1];
		
		String targetElementId = "make-everything-ok-button";

		getElement(targetElementId, originalResource, diff_caseResource);
	}

	private static Element getElement(String elementId, String originalResource, String diff_caseResource) {

		String originalResourcePath = originalResource;
		String targetElementId = elementId;

		Element element = null;

		// getting element by id
		Optional<Element> buttonOpt = JsoupUtils.findElementById(new File(originalResourcePath),
				targetElementId);
		
		System.out.println("Output for " + diff_caseResource + "\n");

		if (buttonOpt.isPresent()) {
			element = buttonOpt.get();
		} else
			throw new NullPointerException("No such element");

		// Get original element tag name
		String tagName = buttonOpt.get().tagName();

		Map<String, String> attributesMap = new HashMap();

		// Putting original element attributes to Map
		// Key - attribute name, Value - attribute value
		element.attributes().forEach((attr) -> attributesMap.put(attr.getKey(), attr.getValue()));
		attributesMap.put("text", element.text());
		
		System.out.println("Original element attributes: ");
		attributesMap.forEach((key,value) -> System.out.print(" [ " + key + " = " + value + " ] "));
		System.out.println();
		System.out.println();
		
		// Map for storing elements with similar attributes
		// attribute hashCodes stored as keys
		Map<Integer, Element> similarElementsMap = new HashMap<>();
		// List for storing hashCodes of the similar elements
		List<Integer> hashCodesList = new ArrayList<>();

		attributesMap.forEach((key, value) -> {

			// Building cssQuery with each original attribute
			String cssQuery = "" + tagName + "[" + key + "=\"" + value + "\"]";
			if (key.equals("text"))
				cssQuery = "" + tagName + ":matches(" + value + ")";
			
			// Get all the elements whit at least one same attribute as the original
			Elements similarElements = JsoupUtils.findElementsByQuery(new File(diff_caseResource), cssQuery).get();
			
			System.out.println("Elements that has the same attribute  -  (" + key + " = " + value + "):");
			similarElements.forEach(similarElement -> {
				
				System.out.println(similarElement);
				int attributesHashCode = similarElement.attributes().hashCode();
				similarElementsMap.put(attributesHashCode, similarElement);
				hashCodesList.add(attributesHashCode);
			});

			System.out.println();
		});
		
		System.out.println("All similar elements with attribute hashCodes");
		similarElementsMap.forEach((attributeHashCode, elementAttributes) -> System.out.println(elementAttributes + " -- " + attributeHashCode));

		
		// Map key - attribute hashCodes, value - number of repetitions
		Map<Integer, Integer> repetitions = new HashMap<Integer, Integer>();
		hashCodesList.forEach((hashcode) -> {
			int item = hashcode;
			if (repetitions.containsKey(item))
				repetitions.put(item, repetitions.get(item) + 1);
			else
				repetitions.put(item, 1);
		});
		
		System.out.println();
		System.out.println("attribute hashCodes with number of repetitions");
		repetitions.forEach((hashCode,repNumber) -> System.out.println(hashCode + " - " + repNumber));

		// Get hashCode with max number of repetitions
		int maxRpetitions = repetitions.keySet()
				.stream()
				.mapToInt(v -> v)
				.max()
				.getAsInt();

		Element searchedElement = similarElementsMap.get(maxRpetitions);

		StringBuilder pathBuilder = new StringBuilder();
		getXmlPath(searchedElement, pathBuilder);

		
		System.out.println();
		System.out.println();
		System.out.println("Searched element - element with the most number of repetitions");
		System.out.println("Path to element " + pathBuilder.toString());
		System.out.println("Element attributes: " + searchedElement);

		return searchedElement;
	}

	private static void getXmlPath(Element element, StringBuilder pathBuilder) {
		Element currentElement = element;
		pathBuilder.insert(0, (String) currentElement.tagName() + "[" + currentElement.className() + "]" + " > ");

		if (element.hasParent()) {
			getXmlPath(element.parent(), pathBuilder);
		}

	}

}
