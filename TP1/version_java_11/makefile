JFLAGS = -Xlint:all -Xdiags:verbose
JC = javac
JVM= java 
CLIENT= ACS.TCPClient
SERVEUR= ACS.TCPServer
RM= rm -f
HOST= localhost
VERSION_JAVA= 11#min version



all:
	$(JC) $(JFLAGS) -source $(VERSION_JAVA) -target $(VERSION_JAVA) ACS/*.java 
	
server: 
	$(JVM) $(SERVEUR)
	
client:
	$(JVM) $(CLIENT) $(HOST)

demo:
	$(JVM) $(SERVEUR) &
	$(JVM) $(CLIENT) $(HOST)

clean:
	$(RM) ACS/*.class