# Intyg Infra Integration HSA-integration

Provides access to various HSA services over NTjP using infrastructure:directory:* TjK:s.

Typical use is an external application (Webcert, Rehabstod, Intygstjansten) including se.inera.intyg.infra:hsa-integration
as a dependency. This then requires a number of properties being set in the executing application's context as well as
some common Spring profile names.

### Profiles
To activate the Stub that mimics external HSA behaviour, the profile 'wc-hsa-stub' must be active.

### Creating data in the stub
The application is responsible for creating data in the stub, just including the hsa-integration artifact and activating wc-hsa-stub is not sufficient.

Typically, one needs to declare Vårdgivare -> Vårdenhet -> Mottagningar in one JSON file and then Medarbetaruppdrag for employees in another, where the Medarbetaruppdrag specifies one of the Vårdenheter.

Example:
#### Vårdgivare

Place in a .json file inside folder: bootstrap-vardgivare/ on your classpath

     {
         "@class" : "se.inera.intyg.infra.integration.hsa.model.Vardgivare",
         "id" : "IFV1239877878-1041",
         "namn" : "WebCert-Vårdgivare1",
         "vardenheter" : [
             {
             	"@class" : "se.inera.intyg.infra.integration.hsa.model.Vardenhet",
                 "id" : "IFV1239877878-1042",
                 "namn" : "WebCert-Enhet1",
                 "epost" : "enhet1@webcert.invalid.se",
                 "telefonnummer" : "0101234567890",
                 "postadress" : "Storgatan 1",
                 "postnummer" : "12345",
                 "postort" : "Småmåla",
                 "arbetsplatskod" : "1234567890"
             }
         ]
     }

#### Medarbetaruppdrag / personal

Place in a .json file inside folder: bootstrap-medarbetaruppdrag/ on your classpath

    {
        "hsaId": "IFV1239877878-1049",
        "uppdrag":[
            {
                "enhet":"IFV1239877878-1042",
                "andamal":["Vård och behandling", "Admin"]
            }
        ]
    }

#### Loading the JSON above
All JSON files within classpath:/bootstrap-medarbetaruppdrag and classpath:/bootstrap-vardgivare are then loaded by the hsa-integration project on startup.

### Runtime properties
The HSA-integration requires the following properties being set:

#### URLs and NTjP access
- infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponderinterface.endpoint.url=[ntjp hostname]/vp/infrastructure/directory/authorizationmanagement/GetCredentialsForPersonIncludingProtectedPerson/1/rivtabp21
- infrastructure.directory.organization.getunit.endpoint.url=[ntjp hostname]/vp/infrastructure/directory/organization/GetUnit/1/rivtabp21
- infrastructure.directory.organization.gethealthcareunit.endpoint.url=[ntjp hostname]/vp/infrastructure/directory/organization/GetHealthCareUnit/1/rivtabp21
- infrastructure.directory.organization.gethealthcareunitmembers.endpoint.url=[ntjp hostname]/vp/infrastructure/directory/organization/GetHealthCareUnitMembers/1/rivtabp21
- infrastructure.directory.employee.getemployee.endpoint.url=[ntjp hostname]/vp/infrastructure/directory/employee/GetEmployeeIncludingProtectedPerson/1/rivtabp21
- infrastructure.directory.logicalAddress=SE165565594230-1000

- ntjp.ws.certificate.file=${config.dir}/certifikat/[our cert]
- ntjp.ws.certificate.type=JKS
- ntjp.ws.truststore.file=${config.dir}/certifikat/[the truststore]
- ntjp.ws.truststore.type=JKS

#### Credentials
- ntjp.ws.certificate.password=[some password]
- ntjp.ws.key.manager.password=[some password]
- ntjp.ws.truststore.password=[some password]

## Licens
Copyright (C) 2016 Inera AB (http://www.inera.se)

Intyg Infra is free software: you can redistribute it and/or modify it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

Intyg Infra is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU LESSER GENERAL PUBLIC LICENSE for more details.

Se även [LICENSE.md](https://github.com/sklintyg/common/blob/master/LICENSE.md).

-----
