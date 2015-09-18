package org.pure4j.checker.basic.pure.setting_fields;

import org.pure4j.annotations.pure.Pure;

@Pure
public class FieldSetting {

	public int x = 0;
	private int y = 0;
	
	
	public void increment() {
		x++;
		y++;
	}
	
	@Pure
	public static FieldSetting smuggleSomeMutableState() {
		return new FieldSetting();
	}
}
