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
     CREATE_DELIVERY,
     GET_DELIVERY,

     GET_BRANCH_MENU,
     FETCH_BRANCH_TABLES,
    UPDATE_BRANCH,

    GET_RES_REPORT,

    CANCEL_RESERVATION,
    ADD_RESERVATION,

    SUBMIT_COMPLAINT


}
