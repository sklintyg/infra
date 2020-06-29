# XMLDSig i Intyg

### Logga mer

Lägg till följande i enhetstest:

    -Djava.util.logging.config.file=/path/to/infra/xmldsig/src/test/resources/logging.properties
    
### Signatur 2.0

##### Delflöde 1:

1. Användaren klickar "Signera" i frontend.
2. Frontend kör REST-anrop _/initsigningprocess_ till backend.
3. Backend utför erforderliga kontroller av utkastet i backend.
4.1 För användare inloggade med SITHS eller EFOS:
    - utkastJSON konverteras till RegisterCertificate v3 XML.
    - digest beräknas på v3 XML.
4.2 Partiell XMLDSig _SignatureType_ skapas av infra/xmldsig-komponenten med innehåll:
    - Reference, Transforms, Kanoniserad <intyg>-XML
    - digest-värde för kanoniserad <intyg>-XML.
    - Innehålle ej: SignatureValue och KeyInfo.   
5. Backend skapar upp SignaturTicket i REDIS. SignaturTicket skall innehålla:
    - id (uuid)
    - intygsId
    - version (concurrency)
    - status (STARTED, IN_PROGRESS, COMPLETE, FAILED osv)
    - digest av JSON-representationen (för ts-bas, ts-diabetes samt övriga om bankid/telia används)
    - Base64-kodad digest av <SignedInfo> för XMLDSig. (SITHS/EFOS för FKs och SoS intyg)
    - Binär representation av vår pågående _SignatureType_.
6. Backend returnerar SignaturTicket till frontend.

##### Delflöde 2:

1. Frontend skapar upp sin "poll-timeout" som 1 gång per sekund anropar _/pollsignature/{id}_ i backend.
2. I backend så hämtar _/pollsignature/{id}_ SignaturTicket från REDIS och returnerar den.
3. Frontend tittar på status. Om COMPLETE kör success-callback. Om FAILED kör error-callback.

##### Delflöde 3a: NetiD-plugin
1. Frontend anropar iid_Invoke med <SignedInfo>-digesten alternativt JSON-digesten (för ts-bas/ts-diabetes).
2. Användaren signerar.
3. NetiD-plugin anropar javascript callback med raw-signatur + X509Certificate
4. Frontend anropar _/completesignature/{id}_ med raw + x509.
5. Backend tar emot raw + x509
6. Backend hämtar SignatureTicket från REDIS
  - SignatureTicket hämtas från REDIS
  - RAW-signatur infogas i <SignedInfo>
  - X509Certificate skrivs till <KeyInfo>
7. Följande skrivs till Signatur-tabellen:
  - Den kanoniska <intyg>-XML som digesten bygger på.
  - Digest-värdet för <intyg>-XML
  - SignatureType serialiserad till sträng.
8. Till Utkast-eniteten infogas <Signature> i XML-format till fältet "signature" som Base64-kodad sträng.
9. SignatureTicket skrivs med status COMPLETE till REDIS*
10. Se delflöde 2 efter att frontend tagit emot COMPLETE vid sin poll.
forts...

##### Delflöde 3c: BankID/Mobilt BankID
1. Frontend anropar _/signeragrp/{id}_ och visar samtidigt sin signerings-modal.
2. Backend hämtar SignatureTicket från REDIS
3. Backend startar GRP-processen:
3.1 Utför /authenticate över GRP-API för att initiera signeringen.
3.2 Utför Collect-poll var tredje sekund. (* egen tråd!)
4. Collect-poll svarar med COMPLETE - PKCS7-blob (signaturen) skrivs till Signatur-tabellen.*
  - Intygets kanoniska JSON-representation
  - JSON-representationens digest
  - PKCS7-blobben / Base64
5. SignatureTicket skrivs med status COMPLETE till REDIS*
6. Se delflöde 2 efter att frontend tagit emot COMPLETE.

Alternativt har fel uppstått:
4. Collect-poll svarar med FAIL av något slag.*
5. SignatureTicket skrivs med status ERROR till REDIS.*
6. Se delflöde 2 efter att frontend tagit emot ERROR.

#### Städningsmekanismer

Om en användare aldrig slutför NetiD Access eller GRP-signering så kommer Poll efter ca 3 minuter returnera ett feltillstånd. 
- Vi bör i det fallet ändra status på SignatureTicket i REDIS till ERROR.
- När en poll från frontend läser fram en SignatureTicket från REDIS i ett sluttillstånd (COMPLETE, ERROR etc) så bör vi genast ta bort posten från Redis innan vi returnerar SignatureTicket till frontend?

För NetiD plugin finns inget sätt att detektera att användaren klickat Avbryt. I det fallet ligger SignatureTicket kvar i REDIS.
- Vi bör överväga att tidsstämpla SignatureTicket när sådan skapas eller sätta en TTL i Redis för den posten på t.ex. 15 minuter.

 ..