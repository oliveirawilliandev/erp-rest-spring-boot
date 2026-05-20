FROM eclipse-temurin:21-jdk

# Evita prompts interativos
ENV DEBIAN_FRONTEND=noninteractive

# Aceita licença ANTES
RUN echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" \
    | debconf-set-selections

# Instala dependências + Arial
RUN apt-get update && apt-get install -y \
    wget \
    cabextract \
    xfonts-utils \
    fontconfig \
    ttf-mscorefonts-installer \
    && fc-cache -f -v \
    && rm -rf /var/lib/apt/lists/*

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]