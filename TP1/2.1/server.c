#include "proto.h"

#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>
#define __USE_XOPEN
#include <time.h>

void mainloop(void)
{
  struct addrinfo srvr_info;
  struct addrinfo *srvr_addrs = NULL;
  int s;
  int res;
  char servername[1024];

  /* get the (system) name of the current machine */
  gethostname(servername, 1023);
  servername[1023] = '\0';

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
    if ((fork()) == 0)
    { /* Child process */
      int numbytes;
      Request request;
      Response response;

      /* ======================================================= */
      /* Modify the following code to analyse and handle request */
      /* ======================================================= */

      /* Get a request */
      numbytes = recv(client_socket, &request, sizeof(request), 0);
      if (numbytes < 0)
      {
        perror("Receive");
        exit(EXIT_FAILURE);
      }

      /* Send a response */
      numbytes = send(client_socket, &response, sizeof(response), 0);
      if (numbytes < 0)
      {
        perror("Send");
        exit(EXIT_FAILURE);
      }

      /* Deconnexion and termination of the process */
      close(client_socket);
      exit(EXIT_SUCCESS);
    }
    else
      close(client_socket);
  }
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
