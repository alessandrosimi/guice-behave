package com.googlecode.guicebehave;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public interface MethodConverter {

	String convert(String message, Object[] arguments);

	public static class Global implements MethodConverter {
		
		private static final Logger logger = LoggerFactory.getLogger(Global.class);

		@Inject private ReplaceCamelCaseWithSpace camelCase;
		@Inject private ReplaceUnderscoreAndDollarWithSpace underscoreAndDollar;
		@Inject private ReplaceDoubleUnderscoreWithCommaSpace doubleUnderscore;
		@Inject private ReplaceArguments arguments;
		@Inject private RemoveMultiSpacesAndTrim multiSpaces;
		@Inject private SetFirstLetterUpperCase firstLetterUpperCase;
		
		@Override
		public String convert(String message, Object[] args) {
			String convertion = message;
			try {
				convertion = camelCase.convert(convertion, args);
				convertion = doubleUnderscore.convert(convertion, args);
				convertion = underscoreAndDollar.convert(convertion, args);
				convertion = arguments.convert(convertion, args);
				convertion = multiSpaces.convert(convertion, args);
				convertion = firstLetterUpperCase.convert(convertion, args);
			} catch (Exception e) {
				logger.error("Impossiple to convert \"" + message + "\" to a message", e);
			}
			return convertion;
		}
		
	}
	
	public static class ReplaceCamelCaseWithSpace implements MethodConverter {

		private static final String REGEX_CAMEL_CASE = "(?<!_)([A-Z])";
		private static final String SPACE = " ";
		
		@Override
		public String convert(String message, Object[] arguments) {
			Matcher m = Pattern.compile(REGEX_CAMEL_CASE).matcher(message);
			StringBuffer buffer = new StringBuffer();
			while (m.find()) {
		        m.appendReplacement(buffer, SPACE + m.group().toLowerCase());
		    }
		    m.appendTail(buffer);
		    return buffer.toString();
		}
		
	}

	public static class ReplaceUnderscoreAndDollarWithSpace implements MethodConverter {

		private static final String REGEX_UNDER_SCORE = "_";
		private static final String REGEX_DOLLAR = "\\$";
		private static final String SPACE = " ";
		
		@Override
		public String convert(String message, Object[] arguments) {
			return message.replaceAll(REGEX_UNDER_SCORE, SPACE).replaceAll(REGEX_DOLLAR, SPACE + REGEX_DOLLAR);
		}
		
	}
	
	public static class ReplaceDoubleUnderscoreWithCommaSpace implements MethodConverter {

		private static final String REGEX_DOUBLE_UNDER_SCORE = "__";
		private static final String COMMA = ", ";
		
		@Override
		public String convert(String message, Object[] arguments) {
			return message.replaceAll(REGEX_DOUBLE_UNDER_SCORE, COMMA);
		}
		
	}
	
	public static class ReplaceArguments implements MethodConverter {

		private static final String REGEX_VARIABLE = "(\\$\\d+)(.*)";
		private static final String REGEX_VARIABLE_NUMBER = "$1";
		private static final String REGEX_VARIABLE_POST = " $2";
		static final String ARGUMENT_OUT_OF_BOUND = "<out_of_bound_argument>";
		private static final String SPACE = " ";
		
		@Override
		public String convert(String message, Object[] arguments) {
			String[] words = message.split(SPACE);
		    StringBuffer buffer = new StringBuffer();
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		    for (String word : words) {
		    	if (word.matches(REGEX_VARIABLE)) {
		    		String number = word.replaceAll(REGEX_VARIABLE, REGEX_VARIABLE_NUMBER).substring(1);
		    		String post = word.replaceAll(REGEX_VARIABLE, REGEX_VARIABLE_POST);
		    		int index = Integer.parseInt(number) - 1;
		    		if (index < arguments.length) {
			    		Object argument = arguments[index];
		    			word = format(argument, dateFormat) + post;
		    		} else {
		    			word = ARGUMENT_OUT_OF_BOUND + post;
		    		}
		    	}
		    	buffer.append(SPACE).append(word);
		    }
		    return buffer.toString().substring(1);
		}
	
		static final String ARGUMENT_NULL = "<empty>";
		
		private String format(Object argument, SimpleDateFormat dateFormat) {
			if (argument == null) {
				return ARGUMENT_NULL;
			} else if (argument instanceof Date) {
				return dateFormat.format(argument);
			} else if (argument instanceof String) {
				return "\"" + argument + "\"";
			} else if (argument instanceof Collection) {
				return collectionToString((Collection<?>) argument, dateFormat);
			} else if (argument.getClass().isArray()) {
				return arrayToString(argument, dateFormat);
			} else {
				return argument.toString();
			}
		}
		
		static final String COMMA_SEPARATOR = ", ";
		static final String AND_SEPARATOR = " and ";
		
		private String collectionToString(Collection<?> collection, SimpleDateFormat dateFormat) {
			StringBuilder result = new StringBuilder();
			Iterator<?> iterator = collection.iterator();
			while (iterator.hasNext()) {
				String item = format(iterator.next(), dateFormat);
				if (result.length() == 0) { result.append(item); }
				else if (iterator.hasNext()) { result.append(COMMA_SEPARATOR).append(item); }
				else { result.append(AND_SEPARATOR).append(item); }
			}
			return result.toString();
		}
		
		private String arrayToString(Object array, SimpleDateFormat dateFormat) {
			Collection<Object> collection = new ArrayList<Object>();
			for (int i = 0; i < Array.getLength(array); i++) {
				collection.add(Array.get(array, i));
			}
			return collectionToString(collection, dateFormat);
		}
	}
	
	public static class RemoveMultiSpacesAndTrim implements MethodConverter {

		private static final String REGEX_MULTI_SPACES = "\\s+";
		private static final String SPACE = " ";
		
		@Override
		public String convert(String message, Object[] arguments) {
			return message.trim().replaceAll(REGEX_MULTI_SPACES, SPACE);
		}
		
	}
	
	public static class SetFirstLetterUpperCase implements MethodConverter {

		@Override
		public String convert(String message, Object[] arguments) {
			return message.substring(0, 1).toUpperCase() + message.substring(1);
		}
		
	}
	
}
