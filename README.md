# Intyg Common
Intyg Common tillhandahåller hjälpprojekt för de övriga intygsprojekten under [SKL Intyg](http://github.com/sklintyg).

## Utvecklingssetup
Intyg Common innehåller flera olika underprojekt och byggs med hjälp av Maven enligt följande:

```
$ git clone https://github.com/sklintyg/common.git

$ cd common/pom
$ mvn install

$ cd ../support
$ mvn install

$ cd ../web
$ mvn install

$ cd ../util/logging-util
$ mvn install

$ cd ../../util/integration-util
$ mvn install
```

## Licens
Copyright (C) 2014 Inera AB (http://www.inera.se)

Intyg Common is free software: you can redistribute it and/or modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Intyg Common is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU LESSER GENERAL PUBLIC LICENSE for more details.

Se även [LICENSE.md](https://github.com/sklintyg/common/blob/master/LICENSE.md). 
