package com.agileengine;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JsoupUtils {

     private static String CHARSET_NAME = "utf8";
	
	 public static Optional<Element> findElementById(File htmlFile, String targetElementId) {
	        try {
	            Document doc = Jsoup.parse(
	                    htmlFile,
	                    CHARSET_NAME,
	                    htmlFile.getAbsolutePath());

	            return Optional.of(doc.getElementById(targetElementId));

	        } catch (IOException e) {
	            return Optional.empty();
	        }
	    }
	 
	 public static Optional<Elements> findElementsByQuery(File htmlFile, String cssQuery) {
	        try {
	            Document doc = Jsoup.parse(
	                    htmlFile,
	                    CHARSET_NAME,
	                    htmlFile.getAbsolutePath());

	            
	           // doc.select(cssQuery).forEach((element)-> System.out.println(element.hashCode() + " text " + element.text()));
	            return Optional.of(doc.select(cssQuery));

	        } catch (IOException e) {
	            return Optional.empty();
	        }
	    }
	
}
