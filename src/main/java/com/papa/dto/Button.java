package com.papa.dto;

import lombok.Data;

/**
 * @author binfeng huang
 * @date 2019/4/25 8:35
 **/
@Data
public class Button {
    private String type;
    private String name;
    private Button[] sub_button;
}