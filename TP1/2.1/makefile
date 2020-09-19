CC = gcc

CFLAGS = -Wall -Wextra -pedantic -O2

.PHONY: clean

all : server client

server:  server.o
	$(CC) $(CFLAGS) -o server server.o

client: client.o
	$(CC) -o client client.o

client.o: client.c proto.h

server.o: server.c proto.h

.c.o: 
	$(CC) $(CFLAGS) -c -o $*.o $*.c

clean:
	$(RM) *.o *~ server client

archive: clean
	zip TCP.zip *.c *.h makefile* 
