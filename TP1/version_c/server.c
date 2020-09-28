#include "proto.h"

#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>
#define __USE_XOPEN
#include <sys/resource.h>
#include <time.h>

#define MAX_CLIENT 3

typedef struct
{
  Language language;
  _Bool isStarted;
} Sessions;

void handleClient(int client_socket, Sessions session);

void mainloop(void)
{
  // limit nb client by limiting the maximum process 
  // a user can create
  struct rlimit rlim;
  rlim.rlim_cur = rlim.rlim_max = MAX_CLIENT;

  if( getrlimit(RLIMIT_NPROC, &rlim) == -1 )
  {
      perror( "getrlimit failed" );
      exit( EXIT_FAILURE );
  }

  if( setrlimit(RLIMIT_NPROC, &rlim) == -1 )
  {
      perror( "setrlimit failed" );
      exit( EXIT_FAILURE );
  }

  struct addrinfo srvr_info;
  struct addrinfo *srvr_addrs = NULL;
  int s;
  int res;
  char servername[1024];

  /* get the (system) name of the current machine */
  gethostname(servername, 1023);
  servername[1023] = '\0';
  //strcpy(servername,"localhost");

  /* Define communication parameters */
  memset(&srvr_info, 0, sizeof(srvr_info));
  srvr_info.ai_family = AF_INET;       /* IPv4 */
  srvr_info.ai_socktype = SOCK_STREAM; /* TCP */
  srvr_info.ai_flags = INADDR_ANY;     /* Any Address of the server fits */

  /* find the IP address(-es) of the server */
  res = getaddrinfo(servername, MYPORT, &srvr_info, &srvr_addrs);
  if (res != 0 || srvr_addrs == NULL)
  {
    perror("getaddrinfo");
    exit(EXIT_FAILURE);
  }

  /* Creation de la socket */
  s = socket(srvr_addrs->ai_family, srvr_addrs->ai_socktype,
             srvr_addrs->ai_protocol);
  if (s == -1)
  {
    perror("socket");
    exit(EXIT_FAILURE);
  }

  res = bind(s, srvr_addrs->ai_addr, srvr_addrs->ai_addrlen);
  if (res == -1)
  {
    perror("bind");
    exit(EXIT_FAILURE);
  }

  listen(s, 5); /* Max number of waiting clients */

  /* Infinite loop of server */
  for (;;)
  {
    int client_socket;
    int size;
    struct sockaddr_in clnt_info;
    long addr;

    printf("Waiting for connection\n");

    /* Get a new socket for the client of an incomming request */
    size = sizeof(struct sockaddr_in);
    client_socket = accept(s, (struct sockaddr *)&clnt_info, (socklen_t *)&size);
    if (client_socket < 0)
    {
      perror("accept");
      exit(EXIT_FAILURE);
    }

    /* Find client address to display it */
    addr = ntohl(clnt_info.sin_addr.s_addr);
    printf("Connection received from %ld.%ld.%ld.%ld\n",
           (addr >> 24) & 255, (addr >> 16) & 255, (addr >> 8) & 255, (addr & 255));

    /* Launch a process to handle request */
    int res = fork();
    if (res == 0)
    {
      /* Child process */
      Sessions session; //= startSessions(getpid(), sessions);
      handleClient(client_socket, session);
    }
    else 
    {
      if (res == -1){
        // too much client
        Response response;
        response.code = ERROR_SERVER;
        response.STR[0] = '\n';
        int numbytes = send(client_socket, &response, sizeof(response), 0);
        if (numbytes < 0)
        {
          perror("Send");
          //exit(EXIT_FAILURE);
        }
      }
      close(client_socket);
    }
      
  }
}

void handleClient(int client_socket, Sessions session)
{

  _Bool connect = true;
  printf("ok");
  do
  {
    int numbytes;
    Request request;
    Response response;

    /* Get a request */
    numbytes = recv(client_socket, &request, sizeof(request), 0);
    if (numbytes < 0)
    {
      perror("Receive");
      exit(EXIT_FAILURE);
    }

    printf("action => %d\n", request.action);

    if (!session.isStarted)
    {
      if (request.action == NEW_LANG)
      {
        session.isStarted = true;
        session.language = request.data.language;
        response.code = OK;
      }
      else
      {
        response.code = ERROR_CLIENT;
        connect = false;
      }
      response.STR[0] = '\0';
    }
    else
    {
      switch (request.action)
      {
      case NEW_LANG:
        // TODO : should check language
        session.language = request.data.language; 
        response.code = OK;
        response.STR[0] = '\0';
        break;
      case HELLO:
        response.code = OK;
        response.STR[0] = '\0';
        switch (session.language)
        {
        case ENGLISH:
          strcpy(response.STR, "Hello ");
          break;
        case FRENCH:
          strcpy(response.STR, "Bonjour ");
          break;
        case SPANISH:
          strcpy(response.STR, "Hola ");
          break;
        }
        printf("\nname = > %s\n", request.data.NAME);
        //TODO :should check name length
        strcat(response.STR, request.data.NAME); 
        break;
      case TIME:
        response.code = OK;
        response.STR[0] = '\0';
        switch (session.language)
        {
        case ENGLISH:
          strcpy(response.STR, "It's ");
          break;
        case FRENCH:
          strcpy(response.STR, "Il est ");
          break;
        case SPANISH:
          strcpy(response.STR, "Son las ");
          break;
        }
        time_t timestamp;
        struct tm *tm;
        int hour, min;
        timestamp = time(NULL);
        tm = gmtime(&timestamp);
        // TODO : check if the zone is in -12..14
        hour = (tm->tm_hour + request.data.zone) % 24;
        min = tm->tm_min;
        sprintf(response.STR,"%s %d:%d",response.STR,hour,min);
        break;
      case DISCONNECT:
        response.code = OK;
        response.STR[0] = '\0';
        connect = false;
        break;
      default:
        response.code = ERROR_CLIENT;
        connect = false;
        break;
      }
      printf("action => %d\n", request.action);
      printf("language => %d", request.data.language);
    }
    /* Send a response */
    numbytes = send(client_socket, &response, sizeof(response), 0);
    if (numbytes < 0)
    {
      perror("Send");
      exit(EXIT_FAILURE);
    }
  } while (connect);

  printf("disconnected");

  /* Deconnexion and termination of the process */
  close(client_socket);
  exit(EXIT_SUCCESS);
}

int main(int argc, char *argv[])
{
  if (argc != 1)
  {
    printf("Usage: %s\n", argv[0]);
  }
  mainloop();
  return EXIT_SUCCESS;
}
