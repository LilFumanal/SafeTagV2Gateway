# 🚪 Gateway Service (SafeTag)

## 📖 Rôle
Le `gateway-service` est le point d'entrée unique de l'architecture SafeTag. 
Il a deux responsabilités majeures :
1. **Routage** : Rediriger les requêtes entrantes vers le microservice approprié.
2. **Sécurité (Filtre JWT)** : Intercepter et valider les tokens JWT avant d'autoriser l'accès aux routes protégées. 

*Note : Les microservices en aval (user, review, etc.) font confiance à la Gateway et n'ont pas besoin de revalider le token.*

## ⚙️ Configuration (Variables d'environnement)
Pour fonctionner, la Gateway nécessite une configuration commune avec le `user-service` concernant la clé JWT :

```properties
# Application
server.port=8080

# JWT Secret (doit être STRICTEMENT identique à celui du user-service, encodé en Base64)
jwt.secret=TA_CLE_SECRETE_BASE_64
```

## 🛣️ Routage des Services (via `GatewayConfig.java`)

La Gateway intercepte les requêtes publiques et les redirige vers les microservices correspondants. 
Les routes internes (`/api/internal/**`) ne sont **pas exposées**, garantissant la sécurité des communications inter-services.

| Service concerné | Préfixe d'URL (Public) |
| :--- | :--- |
| **User Service** | `/api/v1/users/**` |
| **Review Service** | `/api/v1/reviews/**` |
| **RPPS Service** | `/api/v1/rpps/**` |
| **Moderation Service** | `/api/v1/moderation/**` |


## 🚀 Lancement
Le service se lance comme une application Spring Boot classique :
```./mvnw spring-boot:run```
