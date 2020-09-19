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
  struct addrinfo  srvr_info;
  struct addrinfo *srvr_addrs=NULL;
  int s	/* socket */;
  int address;
  int numbytes;
  Request request;
  Response response;
	
  memset(&srvr_info,0,sizeof(srvr_info));
  srvr_info.ai_family   = AF_INET; /* IPv4 */
  srvr_info.ai_socktype = SOCK_STREAM; /* TCP */
  srvr_info.ai_flags    = INADDR_ANY; /* Any Server Address */
	
  /* Seek for server address */
  address = getaddrinfo(host,MYPORT,&srvr_info,&srvr_addrs);
  if (address!=0 || srvr_addrs==NULL) {
    perror("getaddrinfo");
    exit(EXIT_FAILURE);
  }
	
  /* Socket creation */
  s=socket(srvr_addrs->ai_family, srvr_addrs->ai_socktype,
           srvr_addrs->ai_protocol) ;
  if (s==-1) {
    perror("cannot create socket");
    exit(EXIT_FAILURE);
  }

  /* Connection to server */
  if (connect(s, srvr_addrs->ai_addr, sizeof(*(srvr_addrs->ai_addr)))<0){
    perror("Cannot connect to server");
    exit(EXIT_FAILURE);
  }
  printf("Socket created and connected\n");

  /* ====================================================================== */
  /* Modify following code to send an appropriate request et get a response */
  /* ====================================================================== */

  /* Send a request... */
  numbytes=send(s,&request, sizeof(request),0);
  if (numbytes<0) {
    perror("send");
    exit(EXIT_FAILURE);
  }

  /* Get a response */
  numbytes=recv(s,&response, sizeof(response),0);
  if(numbytes<0) {
    perror("receive");
    exit(EXIT_FAILURE);
  }
  
  close(s);
}


int main(int argc, char *argv[]) /*argv : 1=serveur 2=message */
{
  if(argc != 2) {
    printf("Usage: %s host\n",argv[0]);
    exit(EXIT_FAILURE);
  }
  dialog(argv[1]);
  return EXIT_SUCCESS;
}


