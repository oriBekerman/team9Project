package il.cshaifasweng.OCSFMediatorExample.entities;

public enum RequestType{

    ADD_DISH,
    REMOVE_DISH,
    UPDATE_INGREDIENTS,
    UPDATE_DISH_TYPE,

    //base menu related requests
    GET_BASE_MENU,//menuItems
    UPDATE_PRICE,//need two one for branch one for base
    PERMISSION_REQUEST,

     //login related requests
     CHECK_USER,
     //delivery related requests

     //branch related requests
     GET_BRANCH_BY_NAME,
     GET_BRANCHES,

     GET_DELIVERABLES,
     CREATE_DELIVERY,
     GET_DELIVERY,
     CANCEL_DELIVERY,

     GET_BRANCH_MENU,
     FETCH_BRANCH_TABLES,
     UPDATE_BRANCH,

     GET_RES_REPORT,
     GET_DELIV_REPORT,
     GET_COMP_REPORT,

     CANCEL_RESERVATION,
     ADD_RESERVATION,
     GET_ACTIVE_RESERVATIONS,

     SUBMIT_COMPLAINT,
     GET_ALL_COMPLAINTS,
     HANDLE_COMPLAINT_TABLE
}
