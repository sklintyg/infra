/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.loggtjanststub;

import java.util.List;
import jakarta.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.informationsecurity.auditing.log.StoreLog.v2.rivtabp21.StoreLogResponderInterface;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogResponseType;
import se.riv.informationsecurity.auditing.log.StoreLogResponder.v2.StoreLogType;
import se.riv.informationsecurity.auditing.log.v2.LogType;
import se.riv.informationsecurity.auditing.log.v2.ResultCodeType;
import se.riv.informationsecurity.auditing.log.v2.ResultType;


/**
 * @author andreaskaltenbach
 */
public class StoreLogStubResponder implements StoreLogResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(StoreLogStubResponder.class);

    @Autowired
    private LogStore logStore;

    @Autowired(required = false)
    private StubState stubState;

    @Override
    public StoreLogResponseType storeLog(String logicalAddress, StoreLogType request) {
        StoreLogResponseType response = new StoreLogResponseType();
        ResultType result = new ResultType();

        if (stubState != null) {

            if (stubState.getArtificialLatency() > 0L) {
                //CHECKSTYLE:OFF EmptyCatchBlock
                try {
                    Thread.sleep(stubState.getArtificialLatency());
                } catch (InterruptedException e) {
                }
                //CHECKSTYLE:ON EmptyCatchBlock
            }

            if (!stubState.isActive()) {
                throw new WebServiceException("Stub is faking unaccessible StoreLog service");
            } else if (stubState.isActive() && stubState.isFakeError()) {
                result.setResultCode(ResultCodeType.ERROR);
                result.setResultText("Stub is faking errors.");
                response.setResult(result);
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
                response.setResult(result);
                result.setResultText("Stub is triggering error: " + stubState.getErrorState().name());
                return response;
            }
        }

        List<LogType> logItems = request.getLog();
        for (LogType lt : logItems) {
            logStore.addLogItem(lt);
        }

        result.setResultCode(ResultCodeType.OK);
        result.setResultText("Done");
        response.setResult(result);
        return response;
    }


}
