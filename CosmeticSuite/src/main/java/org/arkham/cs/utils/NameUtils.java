package org.arkham.cs.utils;

import java.util.List;

import org.arkham.cs.interfaces.Button;

public class NameUtils {

	/**
	 * A little utility for formatting name. I got to0 tired of typing this out as many times as I have.
	 * @param str A String[] for splitting 
	 * @param sep The separator to put after each String from the Array
	 * @param capitalize Whether or not to Capitalize the word. (bob >> Bob || IRON_SWORD >> Iron Sword)
	 * @return The formatted name
	 */
	public static String format(String[] str, String sep, boolean capitalize){
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < str.length; i++){
			String s = str[i];
			s = s.replace("_", " ");
			if(capitalize){
				s = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
			}
			builder.append(s);
			if(i != (str.length - 1)){
				builder.append(sep);
			}
		}
		return builder.toString();
	}
	
	/**
	 * A little utility for formatting name. I got to0 tired of typing this out as many times as I have.
	 * @param array An array for splitting 
	 * @param sep The separator to put after each String from the Array
	 * @param capitalize Whether or not to Capitalize the word. (bob >> Bob || IRON_SWORD >> Iron Sword)
	 * @return The formatted name
	 */
	public static String format(List<String> array, String sep, boolean capitalize){
		String[] str = array.toArray(new String[array.size()]);
		return format(str, sep, capitalize);
	}
	
	/**
	 * A little utility for formatting name. I got to0 tired of typing this out as many times as I have.
	 * @param buttons A List of buttons for splitting 
	 * @param sep The separator to put after each String from the Array
	 * @param capitalize Whether or not to Capitalize the word. (bob >> Bob || IRON_SWORD >> Iron Sword)
	 * @return The formatted permissions from the button
	 */
	public static String formatButtons(List<Button> buttons, String sep, boolean capitalize){
		String[] str = new String[buttons.size()];
		for(int i = 0; i < str.length; i++){
			str[i] = buttons.get(i).getPermission();
		}
		return format(str, sep, capitalize);
	}
	
	public static String[] formatAndReturn(String[] str, String sep, boolean capitalize){
		return format(str, sep, capitalize).split(sep);
	}
	
}
