# ============================================================================
# ESTÁGIO 1: Build (JDK 21)
# Usamos a imagem do Temurin (antigo AdoptOpenJDK) versão 21 no Alpine Linux
# ============================================================================
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Copia arquivos do maven wrapper e pom
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Resolve dependências
# Dica: No Windows, se der erro no ./mvnw, certifique-se que o arquivo tem
# quebras de linha estilo Unix (LF) ou adicione 'dos2unix' antes.
RUN ./mvnw dependency:go-offline

# Copia o código fonte e compila
COPY src src
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# ============================================================================
# ESTÁGIO 2: Execução (JRE 21)
# Usamos apenas o JRE (Java Runtime Environment) para a imagem final ficar leve
# ============================================================================
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

# Copia as camadas da aplicação (libs, meta-inf, classes) separadamente
# Isso otimiza o cache do Docker
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Ponto de entrada
# Lembre-se de verificar se o caminho do pacote (com.example...) está correto
ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.todolist.TodolistApplication"]