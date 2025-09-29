package com.zcxd.domain;

import java.util.List;

public class WechatMenu {
	 
	private List<Button> button;
 
	public List<Button> getButton() {
		return button;
	}
 
	public void setButton(List<Button> button) {
		this.button = button;
	}
 
	@Override
	public String toString() {
		return "WechatMenu [button=" + button + "]";
	}
	
	
}