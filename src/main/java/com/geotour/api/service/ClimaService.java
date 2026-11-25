package com.geotour.api.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geotour.api.dto.ClimaResponseDTO;
import com.geotour.api.entity.Destino;
import com.geotour.api.exception.IntegrationException;
import com.geotour.api.exception.ResourceNotFoundException;
import com.geotour.api.repository.DestinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClimaService {

    private final DestinoRepository destinoRepository;
    private final WebClient openWeatherWebClient;

    @Value("${app.openweather.api-key}")
    private String apiKey;

    @Cacheable(value = "clima", key = "#destinoId")
    public ClimaResponseDTO obtenerClima(Long destinoId) {
        Destino destino = destinoRepository.findById(destinoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Destino no encontrado con id " + destinoId
                ));

        if (apiKey == null || apiKey.isBlank()) {
            throw new IntegrationException(
                    "La API key de OpenWeatherMap no esta configurada"
            );
        }

        OpenWeatherPayload payload;

        try {
            payload = openWeatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", destino.getLatitud())
                            .queryParam("lon", destino.getLongitud())
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .queryParam("lang", "es")
                            .build()
                    )
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.releaseBody()
                                    .then(Mono.error(new IntegrationException(
                                            "El servicio de clima no esta disponible en este momento"
                                    )))
                    )
                    .bodyToMono(OpenWeatherPayload.class)
                    .block(Duration.ofSeconds(10));
        } catch (IntegrationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new IntegrationException(
                    "No fue posible consultar el servicio de clima",
                    exception
            );
        }

        if (payload == null || payload.main() == null) {
            throw new IntegrationException("Respuesta invalida del servicio de clima");
        }

        String descripcion = "Sin descripcion disponible";
        String icono = null;

        if (payload.weather() != null && !payload.weather().isEmpty()) {
            OpenWeatherPayload.Weather clima = payload.weather().get(0);
            if (clima.description() != null && !clima.description().isBlank()) {
                descripcion = clima.description();
            }
            icono = clima.icon();
        }

        double velocidadViento = payload.wind() == null ? 0.0 : payload.wind().speed();

        return new ClimaResponseDTO(
                destino.getId(),
                destino.getNombre(),
                redondear(payload.main().temp()),
                redondear(payload.main().feelsLike()),
                payload.main().humidity(),
                redondear(velocidadViento),
                descripcion,
                icono,
                Instant.now()
        );
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record OpenWeatherPayload(
            Main main,
            List<Weather> weather,
            Wind wind
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Main(
                double temp,
                @JsonProperty("feels_like") double feelsLike,
                int humidity
        ) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Weather(
                String description,
                String icon
        ) {
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        record Wind(
                double speed
        ) {
        }
    }
}
