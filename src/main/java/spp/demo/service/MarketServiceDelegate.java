package spp.demo.service;

import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spp.demo.generated.api.MarketServiceApiDelegate;
import spp.demo.generated.model.GetMarketResourceForecastSetByDayRequest;
import spp.demo.generated.model.GetMarketResourceForecastSetByDayResponse;
import spp.demo.generated.model.ResourceForecast;
import spp.demo.generated.model.ResourceForecastSet;
import spp.demo.generated.model.ResourceForecastTerm;

@Service
public class MarketServiceDelegate implements MarketServiceApiDelegate {

    @Override
    public ResponseEntity<GetMarketResourceForecastSetByDayResponse> getMarketResourceForecastSetByDay(
        GetMarketResourceForecastSetByDayRequest request
    ) {
        ResourceForecast forecast = new ResourceForecast(
            OffsetDateTime.now(),
            ResourceForecastTerm.SHORTTERM,
            125.5
        );

        ResourceForecastSet forecastSet = new ResourceForecastSet().addResourceForecastItem(forecast);

        GetMarketResourceForecastSetByDayResponse response = new GetMarketResourceForecastSetByDayResponse(
            request.getAssetOwnerName(),
            forecastSet
        ).externalId(request.getExternalId());

        return ResponseEntity.ok(response);
    }
}
