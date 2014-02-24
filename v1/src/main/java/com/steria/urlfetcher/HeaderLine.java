package com.steria.urlfetcher;

import java.util.Comparator;

/**
 * Simple class for holding one line from the http header
 * Only the key is immutable, but the value can change
 */
public class HeaderLine{
	private String key,value;
	
	public HeaderLine(String key, String value){
		this.key = key;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "HeaderLine [key=" + key + ", value=" + value + "]";
	}	
}

// alphabetic sort of the header key
class HeaderLineKeyComparator implements Comparator<HeaderLine> {
  public int compare(HeaderLine o1, HeaderLine o2) {
      int comp = o1.getKey().compareTo(o2.getKey());
      if(comp == 0){
      	comp = o1.getValue().compareTo(o2.getValue());
      }
      return comp;
  }
}
