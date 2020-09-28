JC = javac
JVM= java 
CLIENT= ACS.TCPClient
SERVEUR= ACS.TCPServer
RM= rm -f
HOST= localhost
VERSION_JAVA= 14#min version
JFLAGS= -Xlint:all -Xdiags:verbose --enable-preview --release $(VERSION_JAVA)
JAVAFLAGS= --enable-preview


all:
	$(JC) $(JFLAGS) ACS/*.java 
	
server: 
	$(JVM) $(JAVAFLAGS) $(SERVEUR)
	
client:
	$(JVM) $(JAVAFLAGS) $(CLIENT) $(HOST)

demo:
	$(JVM) $(JAVAFLAGS) $(SERVEUR) &
	$(JVM) $(JAVAFLAGS) $(CLIENT) $(HOST)

clean:
	$(RM) ACS/*.class