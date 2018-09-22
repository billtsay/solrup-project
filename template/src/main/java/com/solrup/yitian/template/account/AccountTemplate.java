package com.solrup.yitian.template.account;

import com.solrup.yitian.annotation.Application;
import com.solrup.yitian.annotation.Attribute;
import com.solrup.yitian.annotation.Closure;
import com.solrup.yitian.annotation.Field;

@Application(name="account", description="Account Management")
public class AccountTemplate extends Account {

    @Field
    String f_userId;

    @Field
    String f_password;


    @Closure(state = "instantiate",
            consume = {"f_userId"},
            produce = {"userId"}
    )
    public void uniqueUserId(Context context) throws Exception {
        userId = f_userId;
    }

    @Closure(state = "instantiate",
            consume = {"f_password"},
            produce = {"password"}
    )
    public void checkPasswordFormat(Context context) throws Exception {
        password = f_password;
    }


    @Closure(state = "materialize",
            consume = {"userId", "password"}
    )
    public void createUserAccount(Context context) throws Exception {

    }

    @Closure(state = "synchronize",
            produce = {"userId", "password"}
    )
    public void loadUserAccount(Context context) throws Exception {
        userId = "abc";
        password = "xyz";
    }

}
