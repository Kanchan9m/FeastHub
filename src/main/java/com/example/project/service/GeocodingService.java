package com.example.project.service;

import com.example.project.dto.response.LocationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {
    private final RestClient restClient;

    public GeocodingService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(
                        "User-Agent",
                        "FeasthubRMS/1.0 (contact: er.kanchanscm1229@gmail.com)"
                )
                .build();
    }

    public LocationResponse reverseGeocode(double latitude, double longitude) {

        String uri = UriComponentsBuilder
                .fromPath("/reverse")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("format", "jsonv2")
                .queryParam("addressdetails", 1)
                .build()
                .encode()
                .toUriString();

        JsonNode result = restClient.get()
                .uri(uri)
                .retrieve()
                .body(JsonNode.class);

        if (result == null || result.path("address").isMissingNode()) {
            throw new IllegalArgumentException(
                    "No address found for the selected coordinates"
            );
        }

        JsonNode address = result.path("address");

        LocationResponse response = new LocationResponse();

        response.setAddress(textOrNull(result, "display_name"));

        response.setCity(firstAvailable(
                address, "city", "town", "village",
                "municipality", "county"
        ));

        response.setState(textOrNull(address, "state"));
        response.setCountry(textOrNull(address, "country"));
        response.setPincode(textOrNull(address, "postcode"));

        response.setLatitude(parseCoordinate(
                result.path("lat").asText(null)
        ));
        response.setLongitude(parseCoordinate(
                result.path("lon").asText(null)
        ));

        return response;
    }



    public LocationResponse findCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City is required");
        }

        String uri = UriComponentsBuilder
                .fromHttpUrl("https://nominatim.openstreetmap.org/search")
                .queryParam("q", city.trim())
                .queryParam("format", "jsonv2")
                .queryParam("addressdetails", 1)
                .queryParam("limit", 5)
                .queryParam("countrycodes", "in")
                .queryParam("accept-language", "en")
                .build()
                .encode()
                .toUriString();

        System.out.println("Nominatim URI: " + uri);

        String rawResponse = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);

        System.out.println("Nominatim raw response: " + rawResponse);

        JsonNode results;

        try {
            results = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(rawResponse);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to parse Nominatim response", ex);
        }

        if (results == null || !results.isArray() || results.isEmpty()) {
            throw new IllegalArgumentException(
                    "No location found for city: " + city);
        }

        JsonNode selectedResult = null;

        for (JsonNode result : results) {
            JsonNode address = result.path("address");

            String resultName = textOrNull(result, "name");
            String resultCity = firstAvailable(
                    address, "city", "town", "village",
                    "municipality", "county"
            );
            String state = textOrNull(address, "state");

            if (state != null &&
                    (city.trim().equalsIgnoreCase(resultName)
                            || city.trim().equalsIgnoreCase(resultCity))) {
                selectedResult = result;
                break;
            }
        }

        if (selectedResult == null) {
            selectedResult = results.get(0);
        }

        JsonNode address = selectedResult.path("address");

        LocationResponse response = new LocationResponse();

        response.setCity(firstAvailable(
                address, "city", "town", "village",
                "municipality", "county"
        ));
        response.setState(textOrNull(address, "state"));
        response.setCountry(textOrNull(address, "country"));
        response.setPincode(textOrNull(address, "postcode"));
        response.setLatitude(
                parseCoordinate(selectedResult.path("lat").asText(null))
        );
        response.setLongitude(
                parseCoordinate(selectedResult.path("lon").asText(null))
        );

        if (response.getCity() == null) {
            response.setCity(city.trim());
        }

        if (response.getState() == null) {
            throw new IllegalArgumentException(
                    "State information was not available for: " + city);
        }

        return response;
    }


    public LocationResponse findAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }

        try {
            java.net.URI uri = UriComponentsBuilder
                    .fromPath("/search")
                    .queryParam("q", address.trim())
                    .queryParam("format", "jsonv2")
                    .queryParam("addressdetails", 1)
                    .queryParam("limit", 5)
                    .queryParam("countrycodes", "in")
                    .queryParam("accept-language", "en")
                    .build()
                    .encode(java.nio.charset.StandardCharsets.UTF_8)
                    .toUri();

            String rawResponse = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            JsonNode results =
                    new com.fasterxml.jackson.databind.ObjectMapper()
                            .readTree(rawResponse);

            if (results == null || !results.isArray() || results.isEmpty()) {
                return null;
            }

            String requested = normalize(address);
            JsonNode selected = null;
            int bestScore = Integer.MIN_VALUE;

            for (JsonNode result : results) {
                JsonNode details = result.path("address");

                String displayName = textOrNull(result, "display_name");
                String name = textOrNull(result, "name");

                String searchable = normalize(
                        (name == null ? "" : name) + " " +
                                (displayName == null ? "" : displayName)
                );

                int score = 0;

                // Prefer a result whose name/display address matches the query.
                if (searchable.contains(requested)) {
                    score += 10;
                }

                // Check for a requested sector number, e.g. Sector 91.
                java.util.regex.Matcher sectorMatcher =
                        java.util.regex.Pattern
                                .compile("\\bsector\\s*[- ]?\\s*(\\d+)\\b",
                                        java.util.regex.Pattern.CASE_INSENSITIVE)
                                .matcher(address);

                if (sectorMatcher.find()) {
                    String requestedSector = sectorMatcher.group(1);

                    String resultSector = firstAvailable(
                            details, "quarter", "neighbourhood",
                            "suburb", "city_district"
                    );

                    String resultName = name == null ? "" : name;
                    String resultDisplay =
                            displayName == null ? "" : displayName;

                    String sectorText = normalize(
                            resultName + " " + resultDisplay + " " +
                                    (resultSector == null ? "" : resultSector)
                    );

                    boolean exactSector = java.util.regex.Pattern
                            .compile("\\bsector\\s*[- ]?\\s*"
                                    + requestedSector + "\\b")
                            .matcher(sectorText)
                            .find();

                    if (exactSector) {
                        score += 100;
                    } else {
                        // Reject candidates that do not identify the requested sector.
                        continue;
                    }
                }

                String requestedLower = normalize(address);
                String displayLower = normalize(
                        displayName == null ? "" : displayName
                );

                // Prefer candidates mentioning the requested city/locality.
                if (requestedLower.contains("faridabad")
                        && displayLower.contains("faridabad")) {
                    score += 10;
                }

                if (score > bestScore) {
                    bestScore = score;
                    selected = result;
                }
            }

            // Don't silently return an unrelated first result.
            if (selected == null) {
                return null;
            }

            Double latitude =
                    parseCoordinate(selected.path("lat").asText(null));
            Double longitude =
                    parseCoordinate(selected.path("lon").asText(null));

            if (latitude == null || longitude == null) {
                throw new IllegalStateException(
                        "Geocoding response did not contain valid coordinates");
            }

            JsonNode details = selected.path("address");

            LocationResponse location = new LocationResponse();
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setAddress(textOrNull(selected, "display_name"));
            location.setCity(firstAvailable(
                    details, "city", "town", "village",
                    "municipality", "county"
            ));
            location.setState(textOrNull(details, "state"));
            location.setCountry(textOrNull(details, "country"));
            location.setPincode(textOrNull(details, "postcode"));

            return location;

        } catch (org.springframework.web.client.RestClientException e) {
            throw new IllegalStateException(
                    "Nominatim geocoding request failed", e);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException(
                    "Unable to parse Nominatim response", e);
        }
    }

    private String normalize(String value) {
        return value == null ? "" :
                value.toLowerCase(java.util.Locale.ROOT)
                        .replaceAll("[^a-z0-9]+", " ")
                        .trim()
                        .replaceAll("\\s+", " ");
    }



//    public LocationResponse findAddress(String address) {
//        if (address == null || address.isBlank()) {
//            throw new IllegalArgumentException("Address is required");
//        }
//
//        try {
//            java.net.URI uri = UriComponentsBuilder
//                    .fromPath("/search")
//                    .queryParam("q", address.trim())
//                    .queryParam("format", "jsonv2")
//                    .queryParam("addressdetails", 1)
//                    .queryParam("limit", 5)
//                    .build()
//                    .encode(java.nio.charset.StandardCharsets.UTF_8)
//                    .toUri();
//
//            System.out.println("Address being searched: " + address);
//            System.out.println("Nominatim URI: " + uri);
//
//            String rawResponse = restClient.get()
//                    .uri(uri)
//                    .retrieve()
//                    .body(String.class);
//
//            System.out.println("Nominatim response: " + rawResponse);
//
//            JsonNode results = new com.fasterxml.jackson.databind.ObjectMapper()
//                    .readTree(rawResponse);
//
//            if (results == null || !results.isArray() || results.isEmpty()) {
//                return null;
//            }
//
//            JsonNode result = results.get(0);
//
//            Double latitude = parseCoordinate(result.path("lat").asText(null));
//            Double longitude = parseCoordinate(result.path("lon").asText(null));
//
//            if (latitude == null || longitude == null) {
//                throw new IllegalStateException(
//                        "Geocoding response did not contain valid coordinates");
//            }
//
//            LocationResponse location = new LocationResponse();
//            location.setLatitude(latitude);
//            location.setLongitude(longitude);
//            location.setAddress(textOrNull(result, "display_name"));
//
//            JsonNode addressDetails = result.path("address");
//
//            location.setCity(firstAvailable(
//                    addressDetails,
//                    "city", "town", "village", "municipality", "county"
//            ));
//            location.setState(textOrNull(addressDetails, "state"));
//            location.setCountry(textOrNull(addressDetails, "country"));
//            location.setPincode(textOrNull(addressDetails, "postcode"));
//
//            return location;
//
//        } catch (org.springframework.web.client.RestClientException e) {
//            throw new IllegalStateException(
//                    "Nominatim geocoding request failed", e);
//        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
//            throw new IllegalStateException(
//                    "Unable to parse Nominatim response", e);
//        }
//    }


    private String firstAvailable(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = textOrNull(node, field);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);

        if (value == null || value.isNull()
                || value.asText().isBlank()) {
            return null;
        }

        return value.asText();
    }

    private Double parseCoordinate(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }


    private String searchNominatim(String address) {
        String uri = UriComponentsBuilder
                .fromHttpUrl("https://nominatim.openstreetmap.org/search")
                .queryParam("q", address)
                .queryParam("format", "jsonv2")
                .queryParam("addressdetails", 1)
                .queryParam("limit", 1)
                .build()
                .encode()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }


}
