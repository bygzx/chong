package com.dto;

import lombok.Data;

/**
 * @author eric
 * @date 2019/4/25 8:35
 **/
@Data
public class Button {
    private String type;
    private String name;
    private Button[] sub_button;
}
