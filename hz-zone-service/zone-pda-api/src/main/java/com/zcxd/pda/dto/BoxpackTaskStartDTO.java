package com.zcxd.pda.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @ClassName BoxpackTaskStartDTO
 * @Description 早送晚收任务开始DTO
 * @author 秦江南
 * @Date 2021年12月2日上午11:34:59
 */
@Data
public class BoxpackTaskStartDTO {
	
	private BoxpackEmpDTO keyMan;
	
	private BoxpackEmpDTO operMan;
	
	private List<BoxpackDTO> boxpackList;
}
