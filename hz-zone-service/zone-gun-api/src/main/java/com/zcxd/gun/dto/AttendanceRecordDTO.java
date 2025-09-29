package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class AttendanceRecordDTO {
    String enrollNumber;
    String time;
    Integer verifyMode;
    Integer inOutMode;

    public AttendanceRecordDTO(String enrollNumber, String time, Integer verifyMode, Integer inOutMode) {
        this.enrollNumber = enrollNumber;
        this.time = time;
        this.verifyMode = verifyMode;
        this.inOutMode = inOutMode;
    }
}
