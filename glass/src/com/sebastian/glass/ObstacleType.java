package com.sebastian.glass;

public enum ObstacleType {
	Invalid(""), kill("kill"), bounce("bounce"), refuel("refuel"), friction("friction"), acceleration("acceleration"), win("win");

	private String value;

	private ObstacleType (String value) {
		this.value = value;
	}

	public String getValue () {
		return value;
	}
}
