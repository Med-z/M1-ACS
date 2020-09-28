# VERSION JAVA 14
## Raisons de l'utilisation de java 14
* pattern matching sur les instanceof 
* type record 

_J'ai gardé les flags ``` -Xlint:all -Xdiags:verbose ```  mais ils vont produire plein de fois le même warning : ``` ... is a preview feature and may be removed in a future release```._

## Utilisation du makefile
* **compilation** : ```$ make```
* **nettoyage dossier** : ```$ make clean```
* **run serveur** : ```$ make server```
* **run client** : ```$ make server```
* **run les deux** : ```$ make demo```

## Description classes
___
### ClientDataDisconnect
Record indiquant au server que le client veut se déconnecter
___
### ClientDataInit
Record utilisée pour transporté la langue lors de la première connection
___
### ClientDataLanguage
Record utilisée pour transporté la langue lors d'un changement de langue
___
### ClientDataName
Record utilisée pour transporté le nom lors d'une demande de bonjour
___
### ClientDataZone
Record utilisée pour transporté la zone UTC du client lors d'une demande d'heure
___
### Language
Enumération des languages disponibles
___
### LanguageUnknownExeption
Exception levée dans le serveur quand le language est inconnu
___
### Protocol
Contient le port et les code d'erreur
___
### TCPClient 
Contient le client TCP
___
### TCPServeur 
Contient le serveur TCP
___
### Counter _dans TCPServeur_ 
Counter utilisé pour la synchronisation
___