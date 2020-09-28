# VERSION JAVA 11-13

## Utilisation du makefile
* **compilation** : ```$ make```
* **nettoyage dossier** : ```$ make clean```
* **run serveur** : ```$ make server```
* **run client** : ```$ make server```
* **run les deux** : ```$ make demo```

## Description
### ClientData
Data Classe abstraite qui est utilisée pour faire passer les données du client au serveur 
___
### ClientDataDisconnect
Classe de donnée indiquant au server que le client veut se déconnecter
___
### ClientDataInit
Classe de donnée utilisée pour transporté la langue lors de la première connection
___
### ClientDataLanguage
Classe de donnée utilisée pour transporté la langue lors d'un changement de langue
___
### ClientDataName
Classe de donnée utilisée pour transporté le nom lors d'une demande de bonjour
___
### ClientDataZone
Classe de donnée utilisée pour transporté la zone UTC du client lors d'une demande d'heure
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