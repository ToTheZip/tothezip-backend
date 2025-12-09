package com.ssafy.tothezip.user.model;

import lombok.Data;

@Data
public class UserDto {

    private int userId;
    private String email;
    private String password;
    private String userName;
    private String profileImg;

}
