JC = javac
JVM= java 
CLIENT= ACS.TCPClient
SERVEUR= ACS.TCPServer
RM= rm -f
HOST= localhost
VERSION_JAVA= 11#min version
DEP= -classpath '.:gson-2.8.5.jar'
JFLAGS = -Xlint:all -Xdiags:verbose -source $(VERSION_JAVA) -target $(VERSION_JAVA) $(DEP)

all:
	$(JC) $(JFLAGS) ACS/*.java 
	
server: 
	$(JVM) $(SERVEUR)
	
client:
	$(JVM) $(DEP) $(CLIENT) $(HOST)

demo:
	$(JVM) $(SERVEUR) &
	$(JVM) $(CLIENT) $(HOST)

clean:
	$(RM) ACS/*.class