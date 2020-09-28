#include "proto.h"

#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>

static void dialog(char *host)
{
  struct addrinfo srvr_info;
  struct addrinfo *srvr_addrs = NULL;
  int s /* socket */;
  int address;
  int numbytes;
  Response response;

  memset(&srvr_info, 0, sizeof(srvr_info));
  srvr_info.ai_family = AF_INET;       /* IPv4 */
  srvr_info.ai_socktype = SOCK_STREAM; /* TCP */
  srvr_info.ai_flags = INADDR_ANY;     /* Any Server Address */

  /* Seek for server address */
  address = getaddrinfo(host, MYPORT, &srvr_info, &srvr_addrs);
  if (address != 0 || srvr_addrs == NULL)
  {
    perror("getaddrinfo");
    exit(EXIT_FAILURE);
  }

  /* Socket creation */
  s = socket(srvr_addrs->ai_family, srvr_addrs->ai_socktype,
             srvr_addrs->ai_protocol);
  if (s == -1)
  {
    perror("cannot create socket");
    exit(EXIT_FAILURE);
  }

  /* Connection to server */
  if (connect(s, srvr_addrs->ai_addr, sizeof(*(srvr_addrs->ai_addr))) < 0)
  {
    perror("Cannot connect to server");
    exit(EXIT_FAILURE);
  }
  printf("Socket created and connected\n");

  /* Send request init */
  
  Request* requestInit = (Request*) malloc(sizeof(Request));
  requestInit->action = NEW_LANG;
  requestInit->data.language = SPANISH;
  numbytes = send(s, requestInit, sizeof(requestInit), 0);
  if (numbytes < 0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes = recv(s, &response, sizeof(response), 0);
  if (numbytes < 0)
  {
    perror("receive");
    exit(EXIT_FAILURE);
  }

  printf("code => %d \n",response.code);
  printf("response => %s \n",response.STR);
  
  /* Send request Hello */
  
  Request requestHello;
  requestHello.action = HELLO;
  strcpy(requestHello.data.NAME,"myName");

  numbytes = send(s, &requestHello, sizeof(requestHello), 0);
  if (numbytes < 0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes = recv(s, &response, sizeof(response), 0);
  if (numbytes < 0)
  {
    perror("receive");
    exit(EXIT_FAILURE);
  }

  printf("code => %d \n",response.code);
  printf("response => %s \n",response.STR);

  /* Send request Change lang */
  
  Request requestlang;
  requestlang.action = NEW_LANG;
  requestlang.data.language = FRENCH;


  numbytes = send(s, &requestlang, sizeof(requestlang), 0);
  if (numbytes < 0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes = recv(s, &response, sizeof(response), 0);
  if (numbytes < 0)
  {
    perror("receive");
    exit(EXIT_FAILURE);
  }

  printf("code => %d \n",response.code);
  printf("response => %s \n",response.STR);

  //temp:
  sleep(30);

  /* Send request Time*/
  
  Request requestTime;
  requestTime.action = TIME;
  requestTime.data.zone = 0;


  numbytes = send(s, &requestTime, sizeof(requestTime), 0);
  if (numbytes < 0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes = recv(s, &response, sizeof(response), 0);
  if (numbytes < 0)
  {
    perror("receive");
    exit(EXIT_FAILURE);
  }

  printf("code => %d \n",response.code);
  printf("response => %s \n",response.STR);

  /* Send request Disconnect*/
  
  Request requestDis;
  requestDis.action = DISCONNECT;
  requestDis.data.zone = 0;


  numbytes = send(s, &requestDis, sizeof(requestDis), 0);
  if (numbytes < 0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes = recv(s, &response, sizeof(response), 0);
  if (numbytes < 0)
  {
    perror("receive");
    exit(EXIT_FAILURE);
  }

  printf("code => %d \n",response.code);
  printf("response => %s \n",response.STR);

  close(s);
}

int main(int argc, char *argv[]) /*argv : 1=serveur 2=message */
{
  if (argc != 2)
  {
    printf("Usage: %s host\n", argv[0]);
    exit(EXIT_FAILURE);
  }
  dialog(argv[1]);
  return EXIT_SUCCESS;
}
