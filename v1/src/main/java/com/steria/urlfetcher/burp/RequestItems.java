package com.steria.urlfetcher.burp;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A list of "items" in the xml of requests from BurpSuite
 */
@XmlRootElement(name="items")
@XmlAccessorType(XmlAccessType.FIELD)
class RequestItems{
	private List<RequestItem> list;
	
	public List<RequestItem> getItemList(){
		return list;
	}
	@XmlElement(name="item")
	public void setItemList(List<RequestItem> itemList){
		this.list = itemList;
	}
}
