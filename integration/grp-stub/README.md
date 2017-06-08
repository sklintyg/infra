# Intyg Infra Integration GRP-stub

GRP-stubben är tänkt att användas för automatisera tester av signeringar utförda mha BankID och Mobilt BankID 
så att det inte krävs riktiga testklienter för BankID, riktig kommunikation mot CGIs GRP-testserver osv vid 
utveckling och test.

Då protokollet och kontraktet gentemot GRP-servern är publikt kan vi mha av stubben hanterar signeringsanrop, 
collect-anrop och mha ett testbarhets-API, fejka att en signering sker från BankID säkerhetsprogram / klient 
för Mobilt BankID.

### Profiles
Stubben aktiveras genom profilen 'wc-grp-stub'.

### Runtime properties
I applikationen som använder stubben behöver följande properties finnas:

    cgi.funktionstjanster.grp.url=http://localhost:${server.port}/services/grp
 
### URLer
GRP-stubben har två endpoints
    
##### GET: http://localhost:${server.port}/services/grp/status/{orderRef}
    
Hämtar status på en pågående signering.<br>
{orderRef} är signeringens unika referens-id.<br>
Producerar JSON.

##### PUT: http://localhost:${server.port}/services/grp/status
Uppdaterar signeringens status<br>
Konsumerar och producerar JSON.

# Licens

Copyright (C) 2016 Inera AB (http://www.inera.se)

Intyg Infra is free software: you can redistribute it and/or modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Intyg Infra is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU LESSER GENERAL PUBLIC LICENSE for more details.

Se även [LICENSE.md](https://github.com/sklintyg/common/blob/master/LICENSE.md).

-----
