/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.inera.intyg.common.loggtjanststub;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.ehr.log.store.storelog.rivtabp21.v1.StoreLogResponderInterface;
import se.riv.ehr.log.store.storelogresponder.v1.StoreLogRequestType;
import se.riv.ehr.log.store.storelogresponder.v1.StoreLogResponseType;
import se.riv.ehr.log.store.v1.ResultType;
import se.riv.ehr.log.v1.LogType;
import se.riv.ehr.log.v1.ResultCodeType;

import javax.xml.ws.WebServiceException;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author andreaskaltenbach
 */
public class StoreLogStubResponder implements StoreLogResponderInterface {

    @Autowired
    private CopyOnWriteArrayList<LogType> logEntries;

    @Autowired(required = false)
    private StubState stubState;

    @Override
    public StoreLogResponseType storeLog(String logicalAddress, StoreLogRequestType request) {
        StoreLogResponseType response = new StoreLogResponseType();
        ResultType result = new ResultType();

        if (stubState != null) {

            if (stubState.getArtificialLatency() > 0L) {
                try {
                    Thread.sleep(stubState.getArtificialLatency());
                } catch (InterruptedException e) {
                    // I was interrupted.
                }
            }

            if (!stubState.isActive()) {
                throw new WebServiceException("Stub is faking unaccessible StoreLog service");
            } else if (stubState.isActive() && stubState.isFakeError()) {
                result.setResultCode(ResultCodeType.ERROR);
                result.setResultText("Stub is faking errors.");
                response.setResultType(result);
                return response;
            }

            if (stubState.getErrorState() != null && stubState.getErrorState() != ErrorState.NONE) {
                switch (stubState.getErrorState()) {
                    case ERROR:
                        result.setResultCode(ResultCodeType.ERROR);
                        break;
                    case VALIDATION:
                        result.setResultCode(ResultCodeType.VALIDATION_ERROR);
                        break;
                    default:
                        result.setResultCode(ResultCodeType.OK);
                        break;
                }
                response.setResultType(result);
                result.setResultText("Stub is triggering error: " + stubState.getErrorState().name());
                return response;
            }
        }

        logEntries.addAll(request.getLog());

        result.setResultCode(ResultCodeType.OK);
        result.setResultText("Done");
        response.setResultType(result);
        return response;
    }
}
