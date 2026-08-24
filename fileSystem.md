src/
└── main/
    └── java/
        └── com/
            └── tuempresa/
                └── invoicing/
                    ├── domain/
                    │   ├── model/
                    │   │   ├── Customer.java
                    │   │   ├── Invoice.java
                    │   │   ├── InvoiceItem.java          <-- (Agregado)
                    │   │   ├── ConsumoDetail.java        <-- (Actualizado con campos de facturabilidad)
                    │   │   └── valueobjects/
                    │   │       ├── Money.java
                    │   │       ├── InvoiceStatus.java
                    │   │       └── InvoiceId.java
                    │   ├── port/
                    │   │   ├── in/                        <-- Puertos de Entrada (Casos de uso)
                    │   │   │   ├── CreateInvoiceUseCase.java
                    │   │   │   └── ProcessConsumoUseCase.java
                    │   │   └── out/                       <-- Puertos de Salida (Contratos hacia afuera)
                    │   │       ├── InvoiceRepositoryPort.java
                    │   │       ├── ConsumoRepositoryPort.java
                    │   │       └── CustomerRepositoryPort.java
                    │   └── exception/
                    │       └── NonBillableConsumoException.java
                    │
                    ├── application/
                    │   └── service/                       <-- Servicios de aplicación que implementan casos de uso
                    │       ├── CreateInvoiceService.java
                    │       └── ProcessConsumoService.java
                    │
                    └── infrastructure/
                        ├── adapter/
                        │   ├── in/                        <-- Adaptadores de Entrada (Puntos de acceso)
                        │   │   └── rest/
                        │   │       ├── InvoiceController.java
                        │   │       ├── dto/
                        │   │       │   ├── CreateInvoiceRequest.java
                        │   │       │   └── InvoiceResponse.java
                        │   │       └── mapper/
                        │   │           └── InvoiceRestMapper.java
                        │   │
                        │   └── out/                       <-- Adaptadores de Salida (Detalles de implementación)
                        │       └── persistence/
                        │           ├── jpa/
                        │           │   ├── entity/        <-- Entidades JPA de Infraestructura (Mapeadas a la BD)
                        │           │   │   ├── InvoiceJpaEntity.java
                        │           │   │   ├── InvoiceItemJpaEntity.java
                        │           │   │   └── ConsumoDetailJpaEntity.java
                        │           │   └── repository/    <-- Repositorios Spring Data JPA
                        │           │       ├── SpringDataInvoiceRepository.java
                        │           │       └── SpringDataConsumoRepository.java
                        │           ├── mapper/            <-- Mappers entre Dominio y JPA
                        │           │   ├── InvoicePersistenceMapper.java
                        │           │   └── ConsumoPersistenceMapper.java
                        │           └── InvoicePersistenceAdapter.java  <-- Implementa InvoiceRepositoryPort
                        │
                        └── config/                        <-- Configuración e Inyección de Spring
                            └── BeanConfiguration.java