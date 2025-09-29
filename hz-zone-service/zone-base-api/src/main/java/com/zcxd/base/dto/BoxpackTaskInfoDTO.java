package com.zcxd.base.dto;

import java.util.List;

import lombok.Data;

@Data
public class BoxpackTaskInfoDTO{

	private String KeyManPhoto;
	
	private String OperManPhoto;
	
	private String handOpMansName;

	private Long handTime;

	private String createUserName;
	
	private List<BoxpackDTO> boxpackList;
}
