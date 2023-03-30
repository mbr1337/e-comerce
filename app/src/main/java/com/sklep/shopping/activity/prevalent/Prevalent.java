package com.sklep.shopping.activity.prevalent;

import com.sklep.shopping.activity.model.Users;

public class Prevalent {
    public static Users currentOnlineUser;

    private static final String UsernameKey = "Username";
    private static final String UserPhoneKey = "UserPhone";
    private static final String UserPasswordKey = "UserPassword";

    public static Users getCurrentOnlineUser()
    {
        return currentOnlineUser;
    }

    public static String getUsernameKey()
    {
        return UsernameKey;
    }

    public static String getUserPhoneKey()
    {
        return UserPhoneKey;
    }

    public static String getUserPasswordKey()
    {
        return UserPasswordKey;
    }

    public static void setCurrentOnlineUser(Users currentOnlineUser)
    {
        Prevalent.currentOnlineUser = currentOnlineUser;
    }
}
