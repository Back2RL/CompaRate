package persistance;

import java.io.Serializable;
import java.util.HashMap;


public class Tag implements Serializable
{
	private static HashMap<String,Tag> existingTags;
	private String tagName;

	// find or insert a tagobject
	public static Tag createTag(final String name){
		if(existingTags.containsKey(name)){
			return existingTags.get(name);
		}
		Tag toAdd = new Tag(name);
		existingTags.put(name, toAdd);
		return toAdd;
	}

	public static Tag getTag(final String name){
			return existingTags.get(name);
	}

	private Tag(final String tagName) {
		this.tagName = tagName;
	}
}
