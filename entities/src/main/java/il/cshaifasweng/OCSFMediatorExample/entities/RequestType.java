package il.cshaifasweng.OCSFMediatorExample.entities;

public enum RequestType{

       //base menu related requests
        GET_BASE_MENU,//menuItems
        UPDATE_PRICE,//need two one for branch one for base

        //login related requests
        CHECK_USER,
        //delivery related requests

        //branch related requests
        GET_BRANCH_BY_NAME,
        GET_BRANCHES,
        GET_DELIVERABLES,
        GET_BRANCH_MENU,//branch
    }
