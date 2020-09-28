#define MYPORT "12343"
// we could send the size instead of
// those constants ...
#define MAXNAME 50
#define MAX_RESPONSE_LENGTH 100

// ==== RequestActionEnum ==== //

enum RequestActionEnum
{
  HELLO = 1,
  TIME = 2,
  DISCONNECT = 3,
  NEW_LANG = 4
};

typedef enum RequestActionEnum RequestAction;

// ============================= //

// ==== Language ==== //

typedef enum 
{
  ENGLISH,
  FRENCH,
  SPANISH
}Language;


// =================== //

// ==== Request ==== //

typedef union 
{
  int zone;
  char NAME[MAXNAME];
  Language language;
} UnionData;


typedef struct
{
  RequestAction action;
  UnionData data;
} Request;

// ================== //

// ==== Response ==== //

typedef enum
{
  OK = 200,
  ERROR_CLIENT = 400,//BAD REQUEST
  ERROR_SERVER = 500,
}ErrorCode;

// this is not oprimal because even 
// if there is just the error code 
// to send we have a 100 useless 
// bytes to add to the packet
typedef struct
{
  ErrorCode code;
  char STR[MAX_RESPONSE_LENGTH];
} Response;

// =================== //