package com.floginfe_be.backend.constants;

public enum Categories {
	LAPTOP("Máy tính xách tay"),
	DESKTOP("Máy tính để bàn"),
	SMARTPHONE("Điện thoại thông minh"),
	TABLET("Máy tính bảng"),
	WEARABLE("Thiết bị đeo thông minh"),
	MONITOR("Màn hình"),
	PRINTER("Máy in"),
	ACCESSORY("Phụ kiện"),
	NETWORK_DEVICE("Thiết bị mạng");

	private final String label;

	Categories(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
