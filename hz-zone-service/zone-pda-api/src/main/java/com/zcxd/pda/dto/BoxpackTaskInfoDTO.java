package com.zcxd.pda.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @ClassName BoxpackTaskStartDTO
 * @Description 早送晚收任务详情
 * @author 秦江南
 * @Date 2021年12月2日上午11:34:59
 */
@Data
public class BoxpackTaskInfoDTO {
	
	private String keyManName = "";
	
	private String operManName = "";
	
	private String lpno = "";
	
	private String handOpMansName = "";
	
	private List<BoxpackDTO> boxpackList;
	
}
