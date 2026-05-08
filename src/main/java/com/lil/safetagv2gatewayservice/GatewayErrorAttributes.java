package com.lil.safetagv2gatewayservice;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GatewayErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

        // Récupération du statut HTTP (souvent mis par la Gateway)
        int status = (int) errorAttributes.getOrDefault("status", 500);
        String path = (String) errorAttributes.getOrDefault("path", "inconnu");

        // Personnalisation selon le cas
        if (status == 503) {
            errorAttributes.put("message", "Le service cible est actuellement indisponible ou éteint.");
            errorAttributes.put("error", "Service Unavailable");
        } else if (status == 404) {
            errorAttributes.put("message", "La route demandée (" + path + ") n'existe pas sur la Gateway.");
            errorAttributes.put("error", "Not Found");
        } else if (status == 500) {
            errorAttributes.put("message", "Erreur interne au niveau de la Gateway.");
        }

        // Nettoyage pour le front
        errorAttributes.remove("requestId");
        errorAttributes.remove("trace");

        return errorAttributes;
    }
}
