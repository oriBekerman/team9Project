package il.cshaifasweng.OCSFMediatorExample.entities;

//each request has a type (enum)
// //so the server can navigate the request to the right controller by identifying the requestType
public enum ReqCategory
{
    BASE_MENU,
    BRANCH,
    LOGIN,
    DELIVERY,
    RESERVATION,
    COMPLAINT,
}
